package com.example.ptitjob.ui.screen.candidate.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.api.dto.CompanyDto
import com.example.ptitjob.data.api.dto.CompanyStatsDto
import com.example.ptitjob.data.api.dto.JobCategoryDto
import com.example.ptitjob.data.api.dto.JobDto
import com.example.ptitjob.data.api.dto.LocationDto
import com.example.ptitjob.data.repository.CompanyRepository
import com.example.ptitjob.data.repository.JobCategoryRepository
import com.example.ptitjob.data.repository.JobRepository
import com.example.ptitjob.data.repository.LocationRepository
import com.example.ptitjob.ui.component.JobCategory
import com.example.ptitjob.ui.component.Location
import com.example.ptitjob.ui.component.CompanyItem
import com.example.ptitjob.ui.component.IndustryItem
import com.example.ptitjob.ui.component.JobItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import com.example.ptitjob.data.api.dto.JobType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CandidateDashboardViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val jobCategoryRepository: JobCategoryRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CandidateDashboardUiState())
    val uiState: StateFlow<CandidateDashboardUiState> = _uiState.asStateFlow()

    init {
        refreshDashboard()
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val errors = mutableListOf<String>()

            val featuredJobs = loadFeaturedJobs(errors)
            val companies = loadTopCompanies(errors)
            val industriesPayload = loadIndustries(errors)
            val industries = industriesPayload.industries
            val locations = loadLocations(errors)
            val jobCount = loadJobCount(errors)
            val companyStats = loadCompanyStats(errors)

            val stats = DashboardStats(
                totalJobs = jobCount,
                totalCompanies = companyStats?.total ?: companies.size,
                highlightRate = calculateHighlightRate(companyStats)
            )

            val errorMessage = errors.filter { it.isNotBlank() }.distinct().joinToString(separator = "\n").ifBlank { null }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    featuredJobs = featuredJobs,
                    topCompanies = companies,
                    hotIndustries = industries,
                    searchCategories = industriesPayload.categories,
                    searchLocations = locations,
                    stats = stats,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private suspend fun loadFeaturedJobs(errors: MutableList<String>): List<JobItem> {
        val response = jobRepository.getFeaturedJobs()
        val data = response.getOrNull()
        if (data == null) {
            errors += response.exceptionOrNull()?.message ?: "Không thể tải danh sách việc làm nổi bật"
            return emptyList()
        }
        if (!data.success || data.data == null) {
            errors += data.message
            return emptyList()
        }
        if (data.data.isEmpty()) {
            // Fallback: load general jobs when no featured ones are marked
            return loadFallbackJobs(errors)
        }
        return data.data.map { it.toJobItem() }
    }

    private suspend fun loadFallbackJobs(errors: MutableList<String>): List<JobItem> {
        val response = jobRepository.getAllJobs(limit = 8)
        val data = response.getOrNull()
        if (data == null || data.data.isNullOrEmpty()) {
            errors += data?.message ?: response.exceptionOrNull()?.message ?: "Không thể tải việc làm"
            return emptyList()
        }
        return data.data.map { it.toJobItem() }
    }

    private suspend fun loadTopCompanies(errors: MutableList<String>): List<CompanyItem> {
        val response = companyRepository.getAllCompanies(limit = 8)
        val data = response.getOrNull()
        if (data == null) {
            errors += response.exceptionOrNull()?.message ?: "Không thể tải danh sách công ty"
            return emptyList()
        }
        if (!data.success || data.data == null) {
            errors += data.message
            return emptyList()
        }
        return data.data.map { it.toCompanyItem() }
    }

    private suspend fun loadIndustries(errors: MutableList<String>): IndustriesPayload {
        val response = jobCategoryRepository.getAllCategories(limit = 8)
        val data = response.getOrNull()
        if (data == null) {
            errors += response.exceptionOrNull()?.message ?: "Không thể tải danh mục ngành nghề"
            return IndustriesPayload(emptyList(), emptyList())
        }
        if (!data.success || data.data == null) {
            errors += data.message
            return IndustriesPayload(emptyList(), emptyList())
        }
        val industries = data.data.mapIndexed { index, dto -> dto.toIndustryItem(index) }
        val categories = data.data.map { dto -> JobCategory(id = dto.id, name = dto.name) }
        return IndustriesPayload(industries, categories)
    }

    private suspend fun loadLocations(errors: MutableList<String>): List<Location> {
        val response = locationRepository.getAllLocations(limit = 8)
        val data = response.getOrNull()
        if (data == null) {
            errors += response.exceptionOrNull()?.message ?: "Không thể tải địa điểm"
            return emptyList()
        }
        if (!data.success || data.data == null) {
            errors += data.message
            return emptyList()
        }
        return data.data.map { it.toLocation() }
    }

    private suspend fun loadJobCount(errors: MutableList<String>): Int {
        val response = jobRepository.getAllJobs(limit = 1)
        val data = response.getOrNull()
        if (data == null) {
            errors += response.exceptionOrNull()?.message ?: "Không thể thống kê số lượng việc làm"
            return 0
        }
        if (!data.success) {
            errors += data.message
        }
        return data.count ?: data.data?.size ?: 0
    }

    private suspend fun loadCompanyStats(errors: MutableList<String>): CompanyStatsDto? {
        val response = companyRepository.getCompanyStats()
        val data = response.getOrNull()
        if (data == null) {
            errors += response.exceptionOrNull()?.message ?: "Không thể tải thống kê công ty"
            return null
        }
        if (!data.success || data.data == null) {
            errors += data.message
            return null
        }
        return data.data
    }

    private fun calculateHighlightRate(stats: CompanyStatsDto?): Int {
        if (stats == null || stats.total == 0) return DEFAULT_HIGHLIGHT_RATE
        val withLogo = stats.withLogo.takeIf { it >= 0 } ?: return DEFAULT_HIGHLIGHT_RATE
        return ((withLogo.toDouble() / stats.total.toDouble()) * 100).toInt().coerceIn(0, 100)
    }

    private fun JobDto.toJobItem(): JobItem {
        return JobItem(
            id = id,
            title = title,
            company = companyName,
            location = locationName.ifBlank { "Đang cập nhật" },
            salary = formatSalary(salaryMin, salaryMax, currency),
            tags = buildList {
                if (categoryName.isNotBlank()) add(categoryName)
                jobType?.let { add(jobTypeLabel(it)) }
            },
            isTop = jobType == JobType.FULL_TIME,
            logo = logoUrl,
            region = deriveRegion(locationName)
        )
    }

    private fun CompanyDto.toCompanyItem(): CompanyItem {
        return CompanyItem(
            id = id,
            name = name,
            category = description?.ifBlank { companySize ?: "Doanh nghiệp" } ?: (companySize ?: "Doanh nghiệp"),
            jobs = jobCount ?: jobsCount ?: jobs?.size ?: 0,
            logo = logoUrl,
            location = address ?: "Đang cập nhật",
            region = deriveRegion(address ?: ""),
            uniqueKey = id
        )
    }

    private fun JobCategoryDto.toIndustryItem(index: Int): IndustryItem {
        return IndustryItem(
            id = id.toIntOrNull() ?: index,
            name = name,
            jobs = jobCount ?: 0,
            iconUrl = iconUrl,
            backendId = id
        )
    }

    private fun LocationDto.toLocation(): Location {
        return Location(
            id = id.toIntOrNull() ?: 0,
            name = city
        )
    }

    private fun formatSalary(min: Double, max: Double, currency: String): String {
        if (min <= 0 && max <= 0) return "Thoả thuận"
        val formatter = NumberFormat.getNumberInstance(VIETNAMESE_LOCALE)
        val normalizedCurrency = currency.uppercase(Locale.getDefault())
        return when {
            min > 0 && max > 0 -> "${formatter.format(min)} - ${formatter.format(max)} $normalizedCurrency"
            max > 0 -> "Lên tới ${formatter.format(max)} $normalizedCurrency"
            else -> "Từ ${formatter.format(min)} $normalizedCurrency"
        }
    }

    private fun jobTypeLabel(jobType: JobType?): String {
        return when (jobType) {
            JobType.FULL_TIME -> "Toàn thời gian"
            JobType.PART_TIME -> "Bán thời gian"
            JobType.CONTRACT -> "Hợp đồng"
            JobType.INTERNSHIP -> "Thực tập"
            JobType.FREELANCE -> "Freelance"
            null -> "Khác"
        }
    }

    private fun deriveRegion(location: String?): String {
        if (location.isNullOrBlank()) return "Toàn quốc"
        val normalized = location.lowercase(Locale.getDefault())
        return when {
            VIETNAM_NORTH_KEYWORDS.any { normalized.contains(it) } -> "Miền Bắc"
            VIETNAM_CENTRAL_KEYWORDS.any { normalized.contains(it) } -> "Miền Trung"
            VIETNAM_SOUTH_KEYWORDS.any { normalized.contains(it) } -> "Miền Nam"
            else -> "Toàn quốc"
        }
    }

    companion object {
        private const val DEFAULT_HIGHLIGHT_RATE = 95

        private val VIETNAMESE_LOCALE = Locale("vi", "VN")

        private val VIETNAM_NORTH_KEYWORDS = listOf("hà nội", "ha noi", "hanoi", "hải phòng", "hai phong", "quảng ninh", "quang ninh", "bắc", "bac")
        private val VIETNAM_CENTRAL_KEYWORDS = listOf("đà nẵng", "da nang", "thuận", "thuong", "quảng nam", "quang nam", "huế", "hue", "miền trung", "mien trung")
        private val VIETNAM_SOUTH_KEYWORDS = listOf("tp. hồ chí minh", "ho chi minh", "hcm", "sài gòn", "sai gon", "cần thơ", "can tho", "miền nam", "mien nam", "bình dương", "binh duong", "đồng nai", "dong nai")
    }
}

data class CandidateDashboardUiState(
    val isLoading: Boolean = true,
    val featuredJobs: List<JobItem> = emptyList(),
    val topCompanies: List<CompanyItem> = emptyList(),
    val hotIndustries: List<IndustryItem> = emptyList(),
    val searchCategories: List<JobCategory> = emptyList(),
    val searchLocations: List<Location> = emptyList(),
    val stats: DashboardStats = DashboardStats(),
    val errorMessage: String? = null
)

data class DashboardStats(
    val totalJobs: Int = 0,
    val totalCompanies: Int = 0,
    val highlightRate: Int = 95
)

private data class IndustriesPayload(
    val industries: List<IndustryItem>,
    val categories: List<JobCategory>
)
