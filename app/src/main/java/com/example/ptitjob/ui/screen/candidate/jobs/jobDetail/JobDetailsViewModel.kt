package com.example.ptitjob.ui.screen.candidate.jobs.jobDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.api.dto.JobDto
import com.example.ptitjob.data.api.request.CreateJobApplicationRequest
import com.example.ptitjob.data.model.JobDetailsPageData
import com.example.ptitjob.data.repository.AuthRepository
import com.example.ptitjob.data.repository.CompanyRepository
import com.example.ptitjob.data.repository.JobApplicationRepository
import com.example.ptitjob.data.repository.JobRepository
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.screen.candidate.jobs.toJobDetailsPageData
import com.example.ptitjob.ui.screen.candidate.jobs.toJobListCardData
import com.example.ptitjob.ui.screen.candidate.jobs.toJobCompanyInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

private const val DEFAULT_ERROR_MESSAGE = "Không thể tải thông tin việc làm"

/**
 * Enhanced ViewModel for Job Detail Screen with comprehensive API integration
 */
@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val jobApplicationRepository: JobApplicationRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = requireNotNull(savedStateHandle["jobId"])
    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val _uiState = MutableStateFlow(JobDetailsUiState(isLoading = true))
    val uiState: StateFlow<JobDetailsUiState> = _uiState.asStateFlow()

    init {
        if (jobId.isBlank()) {
            _uiState.value = JobDetailsUiState(
                isLoading = false,
                errorMessage = "Không tìm thấy mã việc làm"
            )
        } else {
            loadJobDetails()
            checkApplicationStatus()
        }
    }

    fun refresh() {
        loadJobDetails()
        checkApplicationStatus()
    }

    fun toggleSave() {
        _uiState.update { it.copy(isSaved = !it.isSaved) }
    }

    fun openApplicationDialog() {
        _uiState.update { it.copy(isApplicationDialogVisible = true) }
    }

    fun closeApplicationDialog() {
        _uiState.update { it.copy(isApplicationDialogVisible = false) }
    }

    /**
     * Apply to this job with comprehensive error handling
     */
    fun applyToJob(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isApplying = true) }

            try {
                // Check if user is authenticated and get current user
                val userResult = authRepository.getCurrentUser()
                userResult.fold(
                    onSuccess = { userResponse ->
                        if (userResponse.success && userResponse.data != null) {
                            val user = userResponse.data
                            
                            // Validate user data for application
                            if (user.id.isBlank()) {
                                _uiState.update { it.copy(isApplying = false) }
                                onResult(false, "Không thể xác thực người dùng. Vui lòng đăng nhập lại.")
                                return@fold
                            }
                            
                            // Check for required user information
                            if (user.fullName.isBlank()) {
                                _uiState.update { it.copy(isApplying = false) }
                                onResult(false, "Vui lòng cập nhật họ tên trong hồ sơ trước khi ứng tuyển.")
                                return@fold
                            }
                            
                            // Create job application request
                            val applicationRequest = CreateJobApplicationRequest(
                                jobId = jobId,
                                userId = user.id,
                                coverLetter = "", // Could be expanded to include cover letter
                                resumeId = null // Could be expanded to include resume selection
                            )
                            
                            // Submit application
                            val applicationResult = jobApplicationRepository.createApplication(applicationRequest)
                            applicationResult.fold(
                                onSuccess = { applicationResponse ->
                                    if (applicationResponse.success) {
                                        _uiState.update { 
                                            it.copy(
                                                isApplying = false,
                                                hasApplied = true,
                                                isApplicationDialogVisible = false
                                            ) 
                                        }
                                        onResult(true, "Ứng tuyển thành công!")
                                    } else {
                                        _uiState.update { it.copy(isApplying = false) }
                                        onResult(false, applicationResponse.message ?: "Ứng tuyển thất bại")
                                    }
                                },
                                onFailure = { error ->
                                    _uiState.update { it.copy(isApplying = false) }
                                    val errorMessage = when {
                                        error.message?.contains("already applied", ignoreCase = true) == true -> 
                                            "Bạn đã ứng tuyển vào vị trí này rồi"
                                        error.message?.contains("unauthorized", ignoreCase = true) == true -> 
                                            "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại"
                                        error.message?.contains("network", ignoreCase = true) == true -> 
                                            "Lỗi kết nối mạng. Vui lòng thử lại"
                                        else -> error.message ?: "Ứng tuyển thất bại"
                                    }
                                    onResult(false, errorMessage)
                                }
                            )
                        } else {
                            _uiState.update { it.copy(isApplying = false) }
                            val errorMessage = when {
                                userResponse.message.contains("token", ignoreCase = true) -> 
                                    "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại"
                                userResponse.message.contains("unauthorized", ignoreCase = true) -> 
                                    "Vui lòng đăng nhập để ứng tuyển"
                                else -> userResponse.message.ifBlank { "Vui lòng hoàn thiện hồ sơ trước khi ứng tuyển" }
                            }
                            onResult(false, errorMessage)
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isApplying = false) }
                        val errorMessage = when {
                            error.message?.contains("network", ignoreCase = true) == true -> 
                                "Lỗi kết nối mạng. Vui lòng kiểm tra internet và thử lại"
                            error.message?.contains("timeout", ignoreCase = true) == true -> 
                                "Kết nối bị timeout. Vui lòng thử lại"
                            error.message?.contains("unauthorized", ignoreCase = true) == true -> 
                                "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại"
                            else -> error.message ?: "Không thể xác thực người dùng"
                        }
                        onResult(false, errorMessage)
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isApplying = false) }
                val errorMessage = when {
                    e.message?.contains("network", ignoreCase = true) == true -> 
                        "Lỗi kết nối mạng. Vui lòng thử lại"
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Kết nối bị timeout. Vui lòng thử lại"
                    else -> "Lỗi không xác định: ${e.message}"
                }
                onResult(false, errorMessage)
            }
        }
    }

    private fun loadJobDetails() {
        if (jobId.isBlank()) return
        viewModelScope.launch {
            val hasCachedJob = _uiState.value.job != null
            _uiState.update {
                it.copy(
                    isLoading = !hasCachedJob,
                    isRefreshing = hasCachedJob,
                    errorMessage = null
                )
            }

            val jobResult = jobRepository.getJobById(jobId)
            jobResult.fold(
                onSuccess = { response ->
                    if (!response.success || response.data == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = response.message.ifBlank { DEFAULT_ERROR_MESSAGE }
                            )
                        }
                    } else {
                        val now = Instant.now()
                        val jobDto = response.data
                        val jobDetails = jobDto.toJobDetailsPageData(zoneId)
                        _uiState.update {
                            it.copy(
                                job = jobDetails,
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = null,
                                lastUpdated = now,
                                // reset Job specific state when reloading
                                relatedJobs = emptyList(),
                                company = it.company?.takeIf { info -> info.id == jobDto.companyId }
                            )
                        }
                        loadSupplementaryData(jobDto, now)
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = throwable.message ?: DEFAULT_ERROR_MESSAGE
                        )
                    }
                }
            )
        }
    }

    private fun loadSupplementaryData(jobDto: JobDto, snapshotTime: Instant) {
        viewModelScope.launch {
            coroutineScope {
                val companyDeferred = async {
                    companyRepository.getCompanyById(jobDto.companyId)
                }
                val relatedDeferred = async {
                    jobDto.categoryId?.toIntOrNull()?.let { categoryId ->
                        jobRepository.searchJobs(categoryId = categoryId, limit = RELATED_JOBS_LIMIT)
                    }
                }

                companyDeferred.await().fold(
                    onSuccess = { response ->
                        if (response.success && response.data != null) {
                            val companyInfo = response.data.toJobCompanyInfo()
                            _uiState.update { it.copy(company = companyInfo) }
                        }
                    },
                    onFailure = { /* ignore company errors */ }
                )

                relatedDeferred.await()?.fold(
                    onSuccess = { response ->
                        if (response.success && response.data != null) {
                            val related = response.data
                                .filter { it.id != jobDto.id }
                                .map { it.toJobListCardData(snapshotTime, zoneId) }
                                .take(RELATED_JOBS_LIMIT)
                            _uiState.update { it.copy(relatedJobs = related) }
                        }
                    },
                    onFailure = { /* ignore related errors */ }
                )
            }
        }
    }

    /**
     * Check if current user has already applied to this job
     */
    private fun checkApplicationStatus() {
        viewModelScope.launch {
            try {
                val userResult = authRepository.getCurrentUser()
                userResult.fold(
                    onSuccess = { userResponse ->
                        if (userResponse.success && userResponse.data != null) {
                            // Check if user has applied to this job
                            // This would require an API endpoint to check application status
                            // For now, we'll leave it as false by default
                            // You might want to add this endpoint to your backend
                        }
                    },
                    onFailure = { /* Ignore errors for application status check */ }
                )
            } catch (e: Exception) {
                // Ignore errors for application status check
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        private const val RELATED_JOBS_LIMIT = 6
    }
}

/**
 * Enhanced UI State with comprehensive error handling and application state
 */
data class JobDetailsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val job: JobDetailsPageData? = null,
    val company: JobCompanyInfo? = null,
    val relatedJobs: List<JobListCardData> = emptyList(),
    val isSaved: Boolean = false,
    val isApplicationDialogVisible: Boolean = false,
    val isApplying: Boolean = false,
    val hasApplied: Boolean = false,
    val lastUpdated: Instant? = null
)
