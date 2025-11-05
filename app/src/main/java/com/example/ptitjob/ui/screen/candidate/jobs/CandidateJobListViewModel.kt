package com.example.ptitjob.ui.screen.candidate.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.repository.CompanyRepository
import com.example.ptitjob.data.repository.JobCategoryRepository
import com.example.ptitjob.data.repository.JobRepository
import com.example.ptitjob.ui.component.CompanyItem
import com.example.ptitjob.ui.component.FeaturedCompany
import com.example.ptitjob.ui.component.IndustryItem
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.component.RecommendedJob
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEFAULT_ERROR_MESSAGE = "Không thể tải danh sách việc làm"
private const val DEFAULT_LIMIT = 20
private const val COMPANY_LIMIT = 12
private const val CATEGORY_LIMIT = 12

@HiltViewModel
class CandidateJobListViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val jobCategoryRepository: JobCategoryRepository
) : ViewModel() {

    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val _uiState = MutableStateFlow(CandidateJobListUiState())
    val uiState: StateFlow<CandidateJobListUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectFilter(filter: CandidateJobFilter) {
        if (filter == _uiState.value.selectedFilter) return
        _uiState.update { it.copy(selectedFilter = filter) }
        refreshJobs()
    }

    fun refreshAll() {
        viewModelScope.launch {
            loadData(includeMeta = true)
        }
    }

    fun refreshJobs() {
        viewModelScope.launch {
            loadData(includeMeta = false)
        }
    }

    fun performSearch() {
        refreshJobs()
    }

    private suspend fun loadData(includeMeta: Boolean) = coroutineScope {
        val now = Instant.now()
        _uiState.update {
            it.copy(
                isLoading = includeMeta,
                isRefreshing = !includeMeta,
                errorMessage = null
            )
        }

        val filter = _uiState.value.selectedFilter
        val searchTerm = _uiState.value.searchQuery.takeIf { it.isNotBlank() }

        val jobsDeferred = async {
            jobRepository.getAllJobs(
                limit = DEFAULT_LIMIT,
                status = "published",
                search = searchTerm,
                jobType = filter.jobTypeParam
            )
        }

        val companiesDeferred = if (includeMeta) {
            async { companyRepository.getAllCompanies(limit = COMPANY_LIMIT, status = "active") }
        } else null

        val categoriesDeferred = if (includeMeta) {
            async { jobCategoryRepository.getAllCategories(limit = CATEGORY_LIMIT, status = "active") }
        } else null

        val jobsResult = jobsDeferred.await()

        jobsResult.fold(
            onSuccess = { response ->
                if (!response.success || response.data == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = response.message.ifBlank { DEFAULT_ERROR_MESSAGE },
                            jobs = emptyList(),
                            recommendedJobs = emptyList(),
                            totalJobs = 0,
                            newJobsToday = 0,
                            companiesHiring = 0
                        )
                    }
                } else {
                    val jobDtos = response.data
                    val jobCards = jobDtos.map { it.toJobListCardData(now, zoneId) }
                    val recommended = jobDtos
                        .sortedByDescending { it.salaryMax }
                        .take(RECOMMENDED_LIMIT)
                        .map { it.toRecommendedJob(zoneId) }
                    val newToday = jobDtos.count { dto ->
                        dto.createdInstant(zoneId)?.let { created ->
                            Duration.between(created, now).toHours() < 24
                        } ?: false
                    }
                    val companiesHiring = jobDtos.map { it.companyId }.distinct().size

                    _uiState.update {
                        it.copy(
                            jobs = jobCards,
                            recommendedJobs = recommended,
                            totalJobs = response.count ?: jobCards.size,
                            newJobsToday = newToday,
                            companiesHiring = companiesHiring,
                            isLoading = false,
                            isRefreshing = false,
                            lastUpdated = now
                        )
                    }
                }
            },
            onFailure = { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = throwable.message ?: DEFAULT_ERROR_MESSAGE,
                        jobs = emptyList(),
                        recommendedJobs = emptyList(),
                        totalJobs = 0,
                        newJobsToday = 0,
                        companiesHiring = 0
                    )
                }
            }
        )

        if (includeMeta) {
            companiesDeferred?.await()?.fold(
                onSuccess = { response ->
                    if (response.success && response.data != null) {
                        val companies = response.data.map { it.toCompanyItem() }
                        val featured = response.data
                            .sortedByDescending { it.jobCount ?: it.jobsCount ?: 0 }
                            .take(FEATURED_COMPANY_LIMIT)
                            .map { it.toFeaturedCompany() }
                        _uiState.update {
                            it.copy(
                                companies = companies,
                                featuredCompanies = featured
                            )
                        }
                    }
                },
                onFailure = {
                    // Ignore company errors but keep previous data
                }
            )

            categoriesDeferred?.await()?.fold(
                onSuccess = { response ->
                    if (response.success && response.data != null) {
                        val categories = response.data
                            .sortedByDescending { it.jobCount ?: 0 }
                            .map { it.toIndustryItem() }
                        _uiState.update { it.copy(categories = categories) }
                    }
                },
                onFailure = {
                    // Ignore category errors but keep previous data
                }
            )
        }
    }

    companion object {
        private const val RECOMMENDED_LIMIT = 10
        private const val FEATURED_COMPANY_LIMIT = 6
    }
}

data class CandidateJobListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: CandidateJobFilter = CandidateJobFilter.ALL,
    val jobs: List<JobListCardData> = emptyList(),
    val recommendedJobs: List<RecommendedJob> = emptyList(),
    val companies: List<CompanyItem> = emptyList(),
    val featuredCompanies: List<FeaturedCompany> = emptyList(),
    val categories: List<IndustryItem> = emptyList(),
    val totalJobs: Int = 0,
    val newJobsToday: Int = 0,
    val companiesHiring: Int = 0,
    val lastUpdated: Instant? = null
) {
    val availableFilters: List<CandidateJobFilter> = CandidateJobFilter.values().toList()
}

enum class CandidateJobFilter(val label: String, val jobTypeParam: String?) {
    ALL(label = "Tất cả", jobTypeParam = null),
    FULL_TIME(label = "Full-time", jobTypeParam = "full_time"),
    PART_TIME(label = "Part-time", jobTypeParam = "part_time"),
    REMOTE(label = "Remote", jobTypeParam = null),
    INTERN(label = "Thực tập", jobTypeParam = "internship"),
    FRESHER(label = "Fresher", jobTypeParam = null),
    SENIOR(label = "Senior", jobTypeParam = null);
}
