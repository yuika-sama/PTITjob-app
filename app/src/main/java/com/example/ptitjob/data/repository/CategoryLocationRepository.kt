package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.auth.JobCategoryApi
import com.example.ptitjob.data.api.auth.LocationApi
import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.JobCategoryDto
import com.example.ptitjob.data.api.dto.LocationDto
import com.example.ptitjob.data.api.request.CreateJobCategoryRequest
import com.example.ptitjob.data.api.request.CreateLocationRequest
import com.example.ptitjob.data.api.request.UpdateJobCategoryRequest
import com.example.ptitjob.data.api.request.UpdateLocationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Job Category Repository
 * Handles all job category-related operations
 */
@Singleton
class JobCategoryRepository @Inject constructor(
    private val jobCategoryApi: JobCategoryApi
) {
    
    suspend fun getAllCategories(
        page: Int? = null,
        limit: Int? = null,
        search: String? = null,
        status: String? = null
    ): Result<ApiResponse<List<JobCategoryDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobCategoryApi.getAllCategories(page, limit, search, status)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy danh sách danh mục thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getCategoryById(id: String): Result<ApiResponse<JobCategoryDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobCategoryApi.getCategoryById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thông tin danh mục thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createCategory(request: CreateJobCategoryRequest): Result<ApiResponse<JobCategoryDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobCategoryApi.createCategory(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tạo danh mục thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateCategory(id: String, request: UpdateJobCategoryRequest): Result<ApiResponse<JobCategoryDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobCategoryApi.updateCategory(id, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Cập nhật danh mục thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteCategory(id: String): Result<ApiResponse<Unit>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobCategoryApi.deleteCategory(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Xóa danh mục thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun toggleCategoryStatus(id: String): Result<ApiResponse<JobCategoryDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = jobCategoryApi.toggleCategoryStatus(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Thay đổi trạng thái danh mục thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

/**
 * Location Repository
 * Handles all location-related operations
 */
@Singleton
class LocationRepository @Inject constructor(
    private val locationApi: LocationApi
) {
    
    suspend fun getAllLocations(
        page: Int? = null,
        limit: Int? = null,
        search: String? = null,
        country: String? = null,
        status: String? = null
    ): Result<ApiResponse<List<LocationDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = locationApi.getAllLocations(page, limit, search, country, status)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy danh sách địa điểm thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getLocationById(id: String): Result<ApiResponse<LocationDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = locationApi.getLocationById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thông tin địa điểm thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createLocation(request: CreateLocationRequest): Result<ApiResponse<LocationDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = locationApi.createLocation(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tạo địa điểm thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateLocation(id: String, request: UpdateLocationRequest): Result<ApiResponse<LocationDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = locationApi.updateLocation(id, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Cập nhật địa điểm thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteLocation(id: String): Result<ApiResponse<Unit>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = locationApi.deleteLocation(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Xóa địa điểm thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun toggleLocationStatus(id: String): Result<ApiResponse<LocationDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = locationApi.toggleLocationStatus(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Thay đổi trạng thái địa điểm thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun searchLocations(query: String): Result<ApiResponse<List<LocationDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = locationApi.searchLocations(query)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tìm kiếm địa điểm thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
