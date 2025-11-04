package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.auth.AuthApi
import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.AuthResponse
import com.example.ptitjob.data.api.dto.RefreshTokenResponse
import com.example.ptitjob.data.api.dto.UserDto
import com.example.ptitjob.data.api.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authentication Repository
 * Handles all authentication-related operations
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {
    
    suspend fun login(email: String, password: String): Result<ApiResponse<AuthResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                // Validation
                if (email.isBlank() || password.isBlank()) {
                    return@withContext Result.failure(Exception("Email và mật khẩu là bắt buộc"))
                }
                if (password.length < 6) {
                    return@withContext Result.failure(Exception("Mật khẩu phải có ít nhất 6 ký tự"))
                }
                
                val response = authApi.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Đăng nhập thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String? = null,
        role: String = "candidate",
        companyId: String? = null
    ): Result<ApiResponse<AuthResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                // Validation
                if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
                    return@withContext Result.failure(Exception("Email, mật khẩu và họ tên là bắt buộc"))
                }
                
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    companyId = companyId
                )
                
                val response = authApi.register(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Đăng ký thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun logout(refreshToken: String): Result<ApiResponse<Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.logout(LogoutRequest(refreshToken))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Đăng xuất thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun refreshToken(refreshToken: String): Result<ApiResponse<RefreshTokenResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Làm mới token thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getCurrentUser(): Result<ApiResponse<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.getCurrentUser()
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
    
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<ApiResponse<Any>> {
        return withContext(Dispatchers.IO) {
            try {
                if (oldPassword.isBlank() || newPassword.isBlank()) {
                    return@withContext Result.failure(Exception("Mật khẩu cũ và mật khẩu mới là bắt buộc"))
                }
                if (newPassword.length < 6) {
                    return@withContext Result.failure(Exception("Mật khẩu mới phải có ít nhất 6 ký tự"))
                }
                
                val response = authApi.changePassword(ChangePasswordRequest(oldPassword, newPassword))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Đổi mật khẩu thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun forgotPassword(email: String): Result<ApiResponse<Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.forgotPassword(ForgotPasswordRequest(email))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Gửi email thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun verifyResetToken(token: String): Result<ApiResponse<Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.verifyResetToken(VerifyResetTokenRequest(token))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Xác thực token thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun resetPassword(token: String, newPassword: String): Result<ApiResponse<Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.resetPassword(ResetPasswordRequest(token, newPassword))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Đặt lại mật khẩu thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
