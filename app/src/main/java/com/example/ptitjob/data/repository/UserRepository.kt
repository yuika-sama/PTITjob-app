package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.auth.UserApi
import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.UserDto
import com.example.ptitjob.data.api.dto.UserStatsDto
import com.example.ptitjob.data.api.request.CreateUserRequest
import com.example.ptitjob.data.api.request.UpdateUserRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User Repository
 * Handles all user-related operations
 */
@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    
    suspend fun getAllUsers(): Result<ApiResponse<List<UserDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getAllUsers()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy danh sách người dùng thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getUserById(id: String): Result<ApiResponse<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getUserById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thông tin người dùng thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createUser(request: CreateUserRequest): Result<ApiResponse<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.createUser(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tạo người dùng thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateUser(id: String, request: UpdateUserRequest): Result<ApiResponse<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.updateUser(id, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Cập nhật người dùng thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteUser(id: String): Result<ApiResponse<Unit>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.deleteUser(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Xóa người dùng thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun searchUsers(
        email: String? = null,
        fullName: String? = null,
        role: String? = null,
        companyId: Int? = null
    ): Result<ApiResponse<List<UserDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.searchUsers(email, fullName, role, companyId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tìm kiếm người dùng thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getCurrentUser(): Result<ApiResponse<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getCurrentUser()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thông tin người dùng hiện tại thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateCurrentUser(request: UpdateUserRequest): Result<ApiResponse<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.updateCurrentUser(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Cập nhật hồ sơ thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getUserStats(): Result<ApiResponse<UserStatsDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getUserStats()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thống kê thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun toggleUserStatus(id: String): Result<ApiResponse<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.toggleUserStatus(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Thay đổi trạng thái thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
