package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.auth.JobApi
import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.JobDto
import com.example.ptitjob.data.api.request.CreateJobRequest
import com.example.ptitjob.data.api.request.UpdateJobRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Job Repository
 * Handles all job-related operations
 */
@Singleton
class JobRepository @Inject constructor(
    private val jobApi: JobApi
) {
    
    suspend fun getAllJobs(
        page: Int? = null,
        limit: Int? = null,
        search: String? = null,
        jobType: String? = null,
        location: String? = null,
        status: String? = null,
        experienceLevel: String? = null
    ): Result<ApiResponse<List<JobDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApi.getAllJobs(page, limit, search, jobType, location, status, experienceLevel)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy danh sách công việc thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getJobById(id: String): Result<ApiResponse<JobDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApi.getJobById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thông tin công việc thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createJob(request: CreateJobRequest): Result<ApiResponse<JobDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApi.createJob(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tạo công việc thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateJob(id: String, request: UpdateJobRequest): Result<ApiResponse<JobDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApi.updateJob(id, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Cập nhật công việc thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteJob(id: String): Result<ApiResponse<Unit>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApi.deleteJob(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Xóa công việc thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun searchJobs(
        title: String? = null,
        companyId: Int? = null,
        locationId: Int? = null,
        categoryId: Int? = null,
        jobType: String? = null,
        experienceLevel: String? = null,
        page: Int? = null,
        limit: Int? = null
    ): Result<ApiResponse<List<JobDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApi.searchJobs(title, companyId, locationId, categoryId, jobType, experienceLevel, page, limit)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tìm kiếm công việc thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getFeaturedJobs(): Result<ApiResponse<List<JobDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobApi.getFeaturedJobs()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy công việc nổi bật thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
