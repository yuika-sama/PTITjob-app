package com.example.ptitjob.ui.screen.candidate.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.repository.JobRepository
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.screen.candidate.jobs.createdInstant
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

private const val ATTRACTIVE_DEFAULT_ERROR = "Không thể tải danh sách việc làm hấp dẫn"
private const val ATTRACTIVE_PAGE_SIZE = 12

@HiltViewModel
class AttractiveJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val _uiState = MutableStateFlow(AttractiveJobsUiState(isLoading = true))
    val uiState: StateFlow<AttractiveJobsUiState> = _uiState.asStateFlow()

    init {
        loadJobs(resetPage = true)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateLocationQuery(query: String) {
        _uiState.update { it.copy(locationQuery = query) }
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

    fun toggleQuickFilter(filter: AttractiveQuickFilter) {
        val updatedSet = _uiState.value.selectedFilters.toMutableSet().also { selection ->
            if (!selection.add(filter)) {
                selection.remove(filter)
            }
        }
        _uiState.update { it.copy(selectedFilters = updatedSet) }
        loadJobs(resetPage = true)
    }

    private fun loadJobs(page: Int = 1, resetPage: Boolean = false) {
        val targetPage = if (resetPage) 1 else page
        val stateSnapshot = _uiState.value
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
                filters = _uiState.value.selectedFilters
            )
            val jobTypeParam = deriveJobType(_uiState.value.selectedFilters)
            val locationParam = deriveLocation(
                explicitLocation = _uiState.value.locationQuery,
                filters = _uiState.value.selectedFilters
            )

            val result = jobRepository.getAllJobs(
                page = targetPage,
                limit = ATTRACTIVE_PAGE_SIZE,
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
                                errorMessage = response.message.ifBlank { ATTRACTIVE_DEFAULT_ERROR },
                                totalJobs = 0,
                                totalPages = 1
                            )
                        }
                    } else {
                        val now = Instant.now()
                        val filters = _uiState.value.selectedFilters
                        val sortedDtos = if (AttractiveQuickFilter.HIGH_SALARY in filters) {
                            response.data.sortedByDescending { it.salaryMax }
                        } else {
                            response.data.sortedByDescending { dto ->
                                dto.createdInstant(zoneId) ?: Instant.EPOCH
                            }
                        }
                        val jobCards = sortedDtos.map { it.toJobListCardData(now, zoneId) }
                        val totalCount = response.count ?: jobCards.size
                        val totalPages = ((totalCount - 1) / ATTRACTIVE_PAGE_SIZE) + 1

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
                            errorMessage = throwable.message ?: ATTRACTIVE_DEFAULT_ERROR,
                            totalJobs = 0,
                            totalPages = 1
                        )
                    }
                }
            )
        }
    }

    private fun buildSearchTerm(baseQuery: String, filters: Set<AttractiveQuickFilter>): String? {
        val terms = mutableSetOf<String>()
        if (baseQuery.isNotBlank()) terms += baseQuery.trim()
        if (AttractiveQuickFilter.STARTUP in filters) terms += "startup"
        if (AttractiveQuickFilter.TECHNOLOGY in filters) terms += "công nghệ"
        if (AttractiveQuickFilter.MARKETING in filters) terms += "marketing"
        if (AttractiveQuickFilter.DESIGN in filters) terms += "design"
        val combined = terms.joinToString(separator = " ").trim()
        return combined.ifBlank { null }
    }

    private fun deriveJobType(filters: Set<AttractiveQuickFilter>): String? {
        return when {
            AttractiveQuickFilter.FULL_TIME in filters -> "full_time"
            AttractiveQuickFilter.PART_TIME in filters -> "part_time"
            else -> null
        }
    }

    private fun deriveLocation(explicitLocation: String, filters: Set<AttractiveQuickFilter>): String? {
        if (AttractiveQuickFilter.REMOTE in filters) return "remote"
        return explicitLocation.takeIf { it.isNotBlank() }
    }
}

data class AttractiveJobsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val jobs: List<JobListCardData> = emptyList(),
    val searchQuery: String = "",
    val locationQuery: String = "",
    val selectedFilters: Set<AttractiveQuickFilter> = emptySet(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalJobs: Int = 0,
    val lastUpdated: Instant? = null
)

enum class AttractiveQuickFilter {
    HIGH_SALARY,
    REMOTE,
    PART_TIME,
    FULL_TIME,
    STARTUP,
    TECHNOLOGY,
    MARKETING,
    DESIGN
}
