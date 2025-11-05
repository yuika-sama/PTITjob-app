package com.example.ptitjob.ui.screen.candidate.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.repository.JobRepository
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.screen.candidate.jobs.toJobListCardData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEFAULT_ERROR_MESSAGE = "Không thể tải danh sách việc làm"
private const val PAGE_SIZE = 12

@HiltViewModel
class BestJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val _uiState = MutableStateFlow(BestJobsUiState(isLoading = true))
    val uiState: StateFlow<BestJobsUiState> = _uiState.asStateFlow()

    init {
        loadJobs(resetPage = true)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateLocationQuery(query: String) {
        _uiState.update { it.copy(locationQuery = query) }
    }

    fun updateFieldQuery(query: String) {
        _uiState.update { it.copy(fieldQuery = query) }
    }

    fun submitSearch() {
        loadJobs(resetPage = true)
    }

    fun refresh() {
        loadJobs(page = _uiState.value.currentPage)
    }

    fun changePage(newPage: Int) {
        val totalPages = _uiState.value.totalPages.coerceAtLeast(1)
        if (newPage < 1 || newPage > totalPages || newPage == _uiState.value.currentPage) return
        loadJobs(page = newPage)
    }

    private fun loadJobs(page: Int = 1, resetPage: Boolean = false) {
        val stateSnapshot = _uiState.value
        val targetPage = if (resetPage) 1 else page
        viewModelScope.launch {
            val hasData = stateSnapshot.jobs.isNotEmpty()
            _uiState.update {
                it.copy(
                    isLoading = !hasData,
                    isRefreshing = hasData,
                    errorMessage = null,
                    currentPage = if (resetPage) 1 else it.currentPage
                )
            }

            val searchTerm = buildSearchTerm(
                baseQuery = _uiState.value.searchQuery,
                fieldQuery = _uiState.value.fieldQuery
            )
            val jobTypeParam = deriveJobTypeParam(_uiState.value.fieldQuery)
            val locationParam = _uiState.value.locationQuery.takeIf { it.isNotBlank() }

            val result = jobRepository.getAllJobs(
                page = targetPage,
                limit = PAGE_SIZE,
                search = searchTerm,
                jobType = jobTypeParam,
                location = locationParam,
                status = "published"
            )

            result.fold(
                onSuccess = { response ->
                    if (!response.success || response.data == null) {
                        _uiState.update {
                            it.copy(
                                jobs = emptyList(),
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = response.message.ifBlank { DEFAULT_ERROR_MESSAGE },
                                totalJobs = 0,
                                totalPages = 1
                            )
                        }
                    } else {
                        val now = Instant.now()
                        val sortedDtos = response.data.sortedByDescending { it.salaryMax }
                        val jobCards = sortedDtos.map { it.toJobListCardData(now, zoneId) }
                        val totalCount = response.count ?: jobCards.size
                        val totalPages = ((totalCount - 1) / PAGE_SIZE) + 1

                        _uiState.update {
                            it.copy(
                                jobs = jobCards,
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = null,
                                currentPage = targetPage,
                                totalJobs = totalCount,
                                totalPages = totalPages.coerceAtLeast(1),
                                lastUpdated = now
                            )
                        }
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            jobs = emptyList(),
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = throwable.message ?: DEFAULT_ERROR_MESSAGE,
                            totalJobs = 0,
                            totalPages = 1
                        )
                    }
                }
            )
        }
    }

    private fun buildSearchTerm(baseQuery: String, fieldQuery: String): String? {
        val terms = mutableListOf<String>()
        if (baseQuery.isNotBlank()) terms += baseQuery.trim()
        val jobTypeParam = deriveJobTypeParam(fieldQuery)
        if (jobTypeParam == null && fieldQuery.isNotBlank()) {
            terms += fieldQuery.trim()
        }
        val combined = terms.joinToString(separator = " ").trim()
        return combined.ifBlank { null }
    }

    private fun deriveJobTypeParam(fieldQuery: String): String? {
        val normalized = fieldQuery.lowercase().trim()
        return when {
            normalized.contains("full") -> "full_time"
            normalized.contains("part") -> "part_time"
            normalized.contains("intern") || normalized.contains("thực tập") -> "internship"
            normalized.contains("contract") || normalized.contains("hợp đồng") -> "contract"
            normalized.contains("freelance") -> "freelance"
            else -> null
        }
    }
}

data class BestJobsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val jobs: List<JobListCardData> = emptyList(),
    val searchQuery: String = "",
    val locationQuery: String = "",
    val fieldQuery: String = "",
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalJobs: Int = 0,
    val lastUpdated: Instant? = null
)
