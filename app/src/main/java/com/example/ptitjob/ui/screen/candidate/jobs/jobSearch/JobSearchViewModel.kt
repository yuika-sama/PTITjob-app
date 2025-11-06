package com.example.ptitjob.ui.screen.candidate.jobs.jobSearch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.api.dto.CompanyDto
import com.example.ptitjob.data.api.dto.JobDto
import com.example.ptitjob.data.model.Company
import com.example.ptitjob.data.model.Job
import com.example.ptitjob.data.model.JobTag
import com.example.ptitjob.data.repository.CompanyRepository
import com.example.ptitjob.data.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Job Search Screen
 * Handles job searching, filtering, and pagination
 */
@HiltViewModel
class JobSearchViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get initial search parameters from navigation
    private val initialSearch: String? = savedStateHandle["search"]
    private val initialCategory: String? = savedStateHandle["category"]
    private val initialLocation: String? = savedStateHandle["location"]

    private val _uiState = MutableStateFlow(JobSearchUiState())
    val uiState: StateFlow<JobSearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow(initialSearch ?: "")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(
        JobSearchFilters(
            category = initialCategory,
            location = initialLocation
        )
    )
    val filters: StateFlow<JobSearchFilters> = _filters.asStateFlow()

    private var currentPage = 1
    private var hasMorePages = true
    private val pageSize = 10

    init {
        setupSearchDebounce()
        loadInitialData()
    }

    /**
     * Setup search query debounce to avoid too many API calls
     */
    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            searchQuery
                .debounce(300) // Wait 300ms after user stops typing
                .distinctUntilChanged()
                .collect { query ->
                    if (query != initialSearch) {
                        performSearch(resetPagination = true)
                    }
                }
        }
    }

    /**
     * Load initial data based on navigation parameters
     */
    private fun loadInitialData() {
        performSearch(resetPagination = true)
    }

    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Update filters
     */
    fun updateFilters(newFilters: JobSearchFilters) {
        _filters.value = newFilters
        performSearch(resetPagination = true)
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _filters.value = JobSearchFilters()
        performSearch(resetPagination = true)
    }

    /**
     * Load more jobs (pagination)
     */
    fun loadMoreJobs() {
        if (_uiState.value.isLoadingMore || !hasMorePages) return
        
        currentPage++
        performSearch(resetPagination = false)
    }

    /**
     * Refresh job list
     */
    fun refresh() {
        performSearch(resetPagination = true)
    }

    /**
     * Perform job search with current parameters
     */
    private fun performSearch(resetPagination: Boolean) {
        viewModelScope.launch {
            if (resetPagination) {
                currentPage = 1
                hasMorePages = true
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            }

            try {
                val currentFilters = _filters.value
                val result = jobRepository.getAllJobs(
                    limit = pageSize,
                    search = _searchQuery.value.ifBlank { null },
                    location = currentFilters.location,
                    jobType = currentFilters.category,
                    experienceLevel = currentFilters.experience,
                    status = "published"
                )

                result.fold(
                    onSuccess = { response ->
                        if (response.success && response.data != null) {
                            val newJobs = response.data.map { mapJobDtoToModel(it) }
                            
                            val updatedJobs = if (resetPagination) {
                                newJobs
                            } else {
                                _uiState.value.jobs + newJobs
                            }
                            
                            hasMorePages = newJobs.size == pageSize
                            
                            _uiState.value = _uiState.value.copy(
                                jobs = updatedJobs,
                                isLoading = false,
                                isLoadingMore = false,
                                totalResults = updatedJobs.size,
                                hasMorePages = hasMorePages
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = response.message ?: "Không thể tải danh sách công việc"
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = error.message ?: "Lỗi kết nối"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = "Lỗi không xác định: ${e.message}"
                )
            }
        }
    }

    /**
     * Apply quick filter
     */
    fun applyQuickFilter(filterType: String, value: String) {
        val currentFilters = _filters.value
        val newFilters = when (filterType) {
            "category" -> currentFilters.copy(category = value)
            "location" -> currentFilters.copy(location = value)
            "experience" -> currentFilters.copy(experience = value)
            "workType" -> currentFilters.copy(workType = value)
            else -> currentFilters
        }
        updateFilters(newFilters)
    }

    /**
     * Get available job categories
     */
    fun getJobCategories(): List<String> {
        return listOf(
            "Công nghệ thông tin",
            "Marketing",
            "Thiết kế",
            "Kinh doanh",
            "Tài chính",
            "Nhân sự",
            "Giáo dục",
            "Y tế",
            "Xây dựng",
            "Khác"
        )
    }

    /**
     * Get available locations
     */
    fun getLocations(): List<String> {
        return listOf(
            "Hà Nội",
            "TP. Hồ Chí Minh",
            "Đà Nẵng",
            "Hải Phòng",
            "Cần Thơ",
            "Bình Dương",
            "Đồng Nai",
            "Khánh Hòa",
            "Lâm Đồng",
            "Khác"
        )
    }

    /**
     * Get experience levels
     */
    fun getExperienceLevels(): List<String> {
        return listOf(
            "Không yêu cầu",
            "Dưới 1 năm",
            "1-2 năm",
            "2-5 năm", 
            "5-10 năm",
            "Trên 10 năm"
        )
    }

    /**
     * Get work types
     */
    fun getWorkTypes(): List<String> {
        return listOf(
            "Toàn thời gian",
            "Bán thời gian",
            "Thực tập",
            "Freelance",
            "Remote"
        )
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Map JobDto to Job model
     */
    private suspend fun mapJobDtoToModel(dto: JobDto): Job {
        // Get company details
        val company = dto.companyId?.let { companyId ->
            try {
                val companyResult = companyRepository.getCompanyById(companyId)
                companyResult.getOrNull()?.data?.let { companyDto ->
                    mapCompanyDtoToModel(companyDto)
                }
            } catch (e: Exception) {
                null
            }
        }

        val avgSalary: Double = (dto.salaryMax + dto.salaryMin) / 2

        return Job(
            id = dto.id,
            title = dto.title,
            company = company ?: Company(
                id = "",
                name = "Công ty không xác định",
                logo = "",
                industry = "Đa ngành"
            ),
            salary = if (avgSalary > 0) avgSalary.toString() else "Thỏa thuận",
            location = dto.locationName ?: "Không xác định",
            experience = dto.requirements ?: "Không yêu cầu",
            deadline = dto.expiryDate ?: "Không xác định",
            tags = listOf(JobTag.NEW, JobTag.FEATURED),
            category = dto.categoryName ?: "Khác",
            description = dto.description?.split("\n") ?: emptyList(),
            requirements = dto.requirements?.split("\n") ?: emptyList(),
            benefits = dto.benefits?.split("\n") ?: emptyList(),
            workLocation = dto.locationName ?: "Không xác định",
            level = "Đang cập nhật",
            education = "Đang cập nhật",
            quantity = "Đang cập nhật",
            format = "Đang cập nhật"
        )
    }

    /**
     * Map CompanyDto to Company model
     */
    private fun mapCompanyDtoToModel(dto: CompanyDto): Company {
        return Company(
            id = dto.id,
            name = dto.name,
            logo = dto.logoUrl ?: "",
            industry = "Đa ngành", // Default industry
            size = dto.companySize,
            address = dto.address,
            jobCount = dto.jobCount ?: 0
        )
    }

    /**
     * Map job tags from DTO
     */
    private fun mapJobTags(tags: List<String>?): List<JobTag>? {
        return tags?.mapNotNull { tag ->
            when (tag.uppercase()) {
                "NEW", "TIN MỚI", "MỚI" -> JobTag.NEW
                "FEATURED", "NỔI BẬT", "HOT" -> JobTag.FEATURED
                else -> null
            }
        }
    }
}

/**
 * UI State for Job Search Screen
 */
data class JobSearchUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val totalResults: Int = 0,
    val hasMorePages: Boolean = true,
    val errorMessage: String? = null
)

/**
 * Job search filters
 */
data class JobSearchFilters(
    val category: String? = null,
    val location: String? = null,
    val experience: String? = null,
    val workType: String? = null,
    val salaryMin: String? = null,
    val salaryMax: String? = null,
    val sortBy: String = "newest" // newest, salary, deadline
)