package com.example.ptitjob.data.api.request

import com.example.ptitjob.data.api.dto.UserRole
import com.google.gson.annotations.SerializedName

/**
 * Login Request
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Register Request
 */
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("full_name")
    val fullName: String,
    
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    
    @SerializedName("role")
    val role: UserRole = UserRole.CANDIDATE,
    
    @SerializedName("company_id")
    val companyId: String? = null
)

/**
 * Logout Request
 */
data class LogoutRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)

/**
 * Refresh Token Request
 */
data class RefreshTokenRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)

/**
 * Change Password Request
 */
data class ChangePasswordRequest(
    @SerializedName("oldPassword")
    val oldPassword: String,
    
    @SerializedName("newPassword")
    val newPassword: String
)

/**
 * Forgot Password Request
 */
data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

/**
 * Verify Reset Token Request
 */
data class VerifyResetTokenRequest(
    @SerializedName("token")
    val token: String
)

/**
 * Reset Password Request
 */
data class ResetPasswordRequest(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("newPassword")
    val newPassword: String
)
