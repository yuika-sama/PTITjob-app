package com.example.ptitjob.ui.screen.candidate.companies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.api.dto.CompanyDto
import com.example.ptitjob.data.model.Company
import com.example.ptitjob.data.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Companies Screen
 * Handles company listing, search, and navigation logic
 */
@HiltViewModel
class CompaniesViewModel @Inject constructor(
    private val companyRepository: CompanyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompaniesUiState())
    val uiState: StateFlow<CompaniesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCompanies()
    }

    /**
     * Load companies from API
     */
    fun loadCompanies(forceRefresh: Boolean = false) {
        if (_uiState.value.isLoading && !forceRefresh) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val result = companyRepository.getAllCompanies(
                    page = 1,
                    limit = 50,
                    search = _searchQuery.value.ifBlank { null }
                )

                result.fold(
                    onSuccess = { response ->
                        val companies = response.data?.map { dto ->
                            mapCompanyDtoToModel(dto)
                        } ?: emptyList()
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            companies = companies,
                            filteredCompanies = companies,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Không thể tải danh sách công ty"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    /**
     * Search companies by name
     */
    fun searchCompanies(query: String) {
        _searchQuery.value = query
        
        if (query.isBlank()) {
            // Show all companies if search is empty
            _uiState.value = _uiState.value.copy(
                filteredCompanies = _uiState.value.companies
            )
        } else {
            // Filter locally first for immediate feedback
            val filtered = _uiState.value.companies.filter { company ->
                company.name.contains(query, ignoreCase = true) ||
                company.industry.contains(query, ignoreCase = true) ||
                company.address?.contains(query, ignoreCase = true) == true
            }
            _uiState.value = _uiState.value.copy(filteredCompanies = filtered)
            
            // Then fetch from API for comprehensive results
            loadCompanies()
        }
    }

    /**
     * Filter companies by size
     */
    fun filterBySize(size: String?) {
        _uiState.value = _uiState.value.copy(selectedSizeFilter = size)
        
        val filtered = if (size.isNullOrBlank()) {
            _uiState.value.companies
        } else {
            _uiState.value.companies.filter { 
                it.size?.equals(size, ignoreCase = true) == true 
            }
        }
        
        _uiState.value = _uiState.value.copy(filteredCompanies = filtered)
    }

    /**
     * Get company by ID
     */
    fun getCompanyById(id: String, onResult: (Company?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = companyRepository.getCompanyById(id)
                result.fold(
                    onSuccess = { response ->
                        val company = response.data?.let { mapCompanyDtoToModel(it) }
                        onResult(company)
                    },
                    onFailure = {
                        onResult(null)
                    }
                )
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    /**
     * Clear search and reset filters
     */
    fun clearFilters() {
        _searchQuery.value = ""
        _uiState.value = _uiState.value.copy(
            filteredCompanies = _uiState.value.companies,
            selectedSizeFilter = null
        )
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Refresh companies list
     */
    fun refresh() {
        loadCompanies(forceRefresh = true)
    }

    /**
     * Map CompanyDto to Company model
     */
    private fun mapCompanyDtoToModel(dto: CompanyDto): Company {
        return Company(
            id = dto.id,
            name = dto.name,
            logo = dto.logoUrl ?: "",
            industry = determineIndustryFromDescription(dto.description),
            size = dto.companySize,
            address = dto.address,
            jobCount = dto.jobCount ?: dto.jobsCount ?: 0,
            jobs = emptyList() // Jobs will be loaded separately if needed
        )
    }

    /**
     * Determine industry from company description or use default
     */
    private fun determineIndustryFromDescription(description: String?): String {
        // Simple industry detection based on keywords
        return when {
            description?.contains("technology", ignoreCase = true) == true ||
            description?.contains("software", ignoreCase = true) == true ||
            description?.contains("IT", ignoreCase = true) == true -> "Công nghệ thông tin"
            
            description?.contains("finance", ignoreCase = true) == true ||
            description?.contains("bank", ignoreCase = true) == true -> "Tài chính - Ngân hàng"
            
            description?.contains("healthcare", ignoreCase = true) == true ||
            description?.contains("medical", ignoreCase = true) == true -> "Y tế - Sức khỏe"
            
            description?.contains("education", ignoreCase = true) == true ||
            description?.contains("training", ignoreCase = true) == true -> "Giáo dục - Đào tạo"
            
            description?.contains("retail", ignoreCase = true) == true ||
            description?.contains("commerce", ignoreCase = true) == true -> "Bán lẻ - Thương mại"
            
            else -> "Đa ngành"
        }
    }
}

/**
 * UI State for Companies Screen
 */
data class CompaniesUiState(
    val isLoading: Boolean = false,
    val companies: List<Company> = emptyList(),
    val filteredCompanies: List<Company> = emptyList(),
    val selectedSizeFilter: String? = null,
    val errorMessage: String? = null
)