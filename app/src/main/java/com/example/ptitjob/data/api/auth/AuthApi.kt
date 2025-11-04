package com.example.ptitjob.data.api.auth

import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.AuthResponse
import com.example.ptitjob.data.api.dto.RefreshTokenResponse
import com.example.ptitjob.data.api.dto.UserDto
import com.example.ptitjob.data.api.request.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Authentication API Interface
 */
interface AuthApi {
    
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>
    
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponse>>
    
    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<ApiResponse<Any>>
    
    @POST("auth/refresh-token")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<RefreshTokenResponse>>
    
    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>
    
    @PUT("auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<Any>>
    
    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ApiResponse<Any>>
    
    @POST("auth/verify-reset-token")
    suspend fun verifyResetToken(
        @Body request: VerifyResetTokenRequest
    ): Response<ApiResponse<Any>>
    
    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ApiResponse<Any>>
}
