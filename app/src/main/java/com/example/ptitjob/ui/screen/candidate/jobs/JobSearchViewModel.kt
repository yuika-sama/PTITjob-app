package com.example.ptitjob.ui.screen.candidate.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.api.dto.JobDto
import com.example.ptitjob.data.api.dto.JobType
import com.example.ptitjob.data.repository.JobRepository
import com.example.ptitjob.ui.navigation.DashboardSearchPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class JobSearchViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobSearchUiState())
    val uiState: StateFlow<JobSearchUiState> = _uiState.asStateFlow()

    private var hasLoadedDefault = false
    private var lastQuery = SearchQuery()

    fun applyPayload(payload: DashboardSearchPayload?) {
        if (payload != null) {
            val category = payload.categoryId?.let { id ->
                CategoryFilter(id = id, name = payload.categoryName?.takeIf { it.isNotBlank() } ?: id)
            }
            lastQuery = lastQuery.copy(
                keyword = payload.keyword.orEmpty(),
                categoryId = category?.id,
                page = 1
            )
            _uiState.update {
                it.copy(
                    searchQuery = payload.keyword.orEmpty(),
                    selectedCategory = category,
                    activeFilters = buildActiveFilters(category)
                )
            }
            executeSearch(page = 1)
            hasLoadedDefault = true
            return
        }

        if (!hasLoadedDefault) {
            hasLoadedDefault = true
            lastQuery = SearchQuery(page = 1)
            executeSearch(page = 1)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun performSearch(page: Int = 1) {
        val current = _uiState.value
        lastQuery = lastQuery.copy(
            keyword = current.searchQuery,
            categoryId = current.selectedCategory?.id,
            page = page
        )
        executeSearch(page)
    }

    fun removeFilter(filter: String) {
        val category = _uiState.value.selectedCategory
        if (category != null && category.name == filter) {
            _uiState.update {
                it.copy(
                    selectedCategory = null,
                    activeFilters = emptyList()
                )
            }
            performSearch(page = 1)
        }
    }

    fun clearFilters() {
        if (_uiState.value.activeFilters.isEmpty()) return
        _uiState.update {
            it.copy(
                selectedCategory = null,
                activeFilters = emptyList()
            )
        }
        performSearch(page = 1)
    }

    fun changePage(page: Int) {
        val totalPages = _uiState.value.totalPages
        if (page == _uiState.value.currentPage || page !in 1..totalPages) return
        performSearch(page)
    }

    fun retryLastSearch() {
        performSearch(page = _uiState.value.currentPage)
    }

    fun applyQuickSuggestion(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        performSearch(page = 1)
    }

    fun selectCategory(categoryId: String, label: String) {
        val category = CategoryFilter(categoryId, label)
        _uiState.update {
            it.copy(
                selectedCategory = category,
                activeFilters = buildActiveFilters(category)
            )
        }
        performSearch(page = 1)
    }

    private fun executeSearch(page: Int) {
        val query = lastQuery.copy(page = page)
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    currentPage = page
                )
            }

            val result = jobRepository.searchJobs(
                title = query.keyword.takeIf { it.isNotBlank() },
                categoryId = query.categoryId?.toIntOrNull(),
                page = query.page,
                limit = PAGE_SIZE
            )

            result.fold(
                onSuccess = { response ->
                    if (!response.success || response.data == null) {
                        val message = response.message.ifBlank { DEFAULT_ERROR }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = message,
                                jobs = emptyList(),
                                totalJobs = 0,
                                totalPages = 1
                            )
                        }
                        return@fold
                    }

                    val jobs = response.data.map { it.toUiJob() }
                    val totalJobs = response.count ?: jobs.size
                    val totalPages = ((totalJobs + PAGE_SIZE - 1) / PAGE_SIZE).coerceAtLeast(1)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            jobs = jobs,
                            totalJobs = totalJobs,
                            totalPages = totalPages,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: DEFAULT_ERROR,
                            jobs = emptyList(),
                            totalJobs = 0,
                            totalPages = 1
                        )
                    }
                }
            )
        }
    }

    private fun buildActiveFilters(category: CategoryFilter?): List<String> =
        category?.let { listOf(it.name) } ?: emptyList()

    private fun JobDto.toUiJob(): UiJob {
        return UiJob(
            id = id.toIntOrNull() ?: id.hashCode(),
            backendId = id,
            title = title,
            company = UiCompany(
                id = companyId.toIntOrNull() ?: companyId.hashCode(),
                backendId = companyId,
                name = companyName,
                logo = logoUrl.orEmpty(),
                industry = categoryName,
                jobCount = jobCount
            ),
            salary = formatSalary(salaryMin, salaryMax, currency),
            location = locationName.ifBlank { "Đang cập nhật" },
            experience = jobTypeLabel(jobType),
            deadline = expiryDate,
            tags = emptyList(),
            category = categoryName,
            description = description.splitToList(),
            requirements = requirements.splitToList(),
            benefits = benefits.splitToList(),
            workLocation = locationName.ifBlank { "Đang cập nhật" },
            level = jobTypeLabel(jobType),
            education = "Không yêu cầu", // API chưa cung cấp dữ liệu
            quantity = jobCount?.takeIf { it > 0 }?.toString() ?: "1",
            format = jobTypeLabel(jobType)
        )
    }

    private fun String.splitToList(): List<String> {
        if (isBlank()) return emptyList()
        val sanitized = this
            .replace("&nbsp;", " ")
            .replace(Regex("(?i)<br\\s*/?>"), "\n")
            .replace(Regex("(?i)</p>"), "\n")
            .replace(Regex("(?i)</li>"), "\n")
            .replace(Regex("(?i)<li[^>]*>"), "\n")
            .replace(Regex("<[^>]+>"), " ")

        return sanitized
            .split('\n', ';')
            .map { segment ->
                segment
                    .trim()
                    .removePrefix("•")
                    .trimStart('-', '*')
                    .trim()
            }
            .filter { it.isNotBlank() }
            .ifEmpty { listOf(sanitized.trim()) }
    }

    private fun formatSalary(min: Double, max: Double, currency: String): String {
        if (min <= 0 && max <= 0) return "Thỏa thuận"
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        val minFormatted = min.takeIf { it > 0 }?.let { formatter.format(it) }
        val maxFormatted = max.takeIf { it > 0 }?.let { formatter.format(it) }
        val currencyLabel = if (currency.equals("USD", ignoreCase = true)) "USD" else "VNĐ"
        return when {
            minFormatted != null && maxFormatted != null -> "$minFormatted - $maxFormatted $currencyLabel"
            minFormatted != null -> "Từ $minFormatted $currencyLabel"
            maxFormatted != null -> "Đến $maxFormatted $currencyLabel"
            else -> "Thỏa thuận"
        }
    }

    private fun jobTypeLabel(type: JobType?): String = when (type) {
        JobType.FULL_TIME -> "Toàn thời gian"
        JobType.PART_TIME -> "Bán thời gian"
        JobType.CONTRACT -> "Hợp đồng"
        JobType.INTERNSHIP -> "Thực tập"
        JobType.FREELANCE -> "Freelance"
        null -> "Không xác định"
    }

    private data class SearchQuery(
        val keyword: String = "",
        val categoryId: String? = null,
        val page: Int = 1
    )

    companion object {
        private const val PAGE_SIZE = 10
        private const val DEFAULT_ERROR = "Không thể tải dữ liệu tìm kiếm"
    }
}

data class JobSearchUiState(
    val searchQuery: String = "",
    val selectedCategory: CategoryFilter? = null,
    val activeFilters: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val jobs: List<UiJob> = emptyList(),
    val totalJobs: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1
)

data class CategoryFilter(
    val id: String,
    val name: String
)
