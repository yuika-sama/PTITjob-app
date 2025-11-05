package com.example.ptitjob.ui.screen.candidate.jobs.jobDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.api.dto.JobDto
import com.example.ptitjob.data.model.JobDetailsPageData
import com.example.ptitjob.data.repository.CompanyRepository
import com.example.ptitjob.data.repository.JobRepository
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.screen.candidate.jobs.toJobDetailsPageData
import com.example.ptitjob.ui.screen.candidate.jobs.toJobListCardData
import com.example.ptitjob.ui.screen.candidate.jobs.toJobCompanyInfo
import dagger.hilt.android.lifecycle.HiltViewModel
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

private const val DEFAULT_ERROR_MESSAGE = "Không thể tải thông tin việc làm"

@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
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
        }
    }

    fun refresh() {
        loadJobDetails()
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
                    jobDto.categoryId.toIntOrNull()?.let { categoryId ->
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

    companion object {
        private const val RELATED_JOBS_LIMIT = 6
    }
}

data class JobDetailsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val job: JobDetailsPageData? = null,
    val company: JobCompanyInfo? = null,
    val relatedJobs: List<JobListCardData> = emptyList(),
    val isSaved: Boolean = false,
    val isApplicationDialogVisible: Boolean = false,
    val lastUpdated: Instant? = null
)
