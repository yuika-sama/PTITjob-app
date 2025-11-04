package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.auth.JobApplicationApi
import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.JobApplicationDto
import com.example.ptitjob.data.api.request.BulkUpdateStatusRequest
import com.example.ptitjob.data.api.request.CreateJobApplicationRequest
import com.example.ptitjob.data.api.request.UpdateApplicationStatusRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Job Application Repository
 * Handles all job application-related operations
 */
@Singleton
class JobApplicationRepository @Inject constructor(
    private val jobApplicationApi: JobApplicationApi
) {
    
    suspend fun getAllApplications(
        page: Int? = null,
        limit: Int? = null,
        search: String? = null,
        status: String? = null,
        jobTitle: String? = null,
        company: String? = null
    ): Result<ApiResponse<List<JobApplicationDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApplicationApi.getAllApplications(page, limit, search, status, jobTitle, company)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy danh sách đơn ứng tuyển thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getApplicationById(id: String): Result<ApiResponse<JobApplicationDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApplicationApi.getApplicationById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thông tin đơn ứng tuyển thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createApplication(request: CreateJobApplicationRequest): Result<ApiResponse<JobApplicationDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApplicationApi.createApplication(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tạo đơn ứng tuyển thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateApplicationStatus(
        id: String,
        request: UpdateApplicationStatusRequest
    ): Result<ApiResponse<JobApplicationDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApplicationApi.updateApplicationStatus(id, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Cập nhật trạng thái thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteApplication(id: String): Result<ApiResponse<Unit>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApplicationApi.deleteApplication(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Xóa đơn ứng tuyển thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getApplicationsByUser(userId: String): Result<ApiResponse<List<JobApplicationDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApplicationApi.getApplicationsByUser(userId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy đơn ứng tuyển của người dùng thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getApplicationsByJob(jobId: String): Result<ApiResponse<List<JobApplicationDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApplicationApi.getApplicationsByJob(jobId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy đơn ứng tuyển của công việc thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun bulkUpdateStatus(request: BulkUpdateStatusRequest): Result<ApiResponse<Unit>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApplicationApi.bulkUpdateStatus(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Cập nhật trạng thái hàng loạt thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
