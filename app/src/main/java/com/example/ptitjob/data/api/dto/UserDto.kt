package com.example.ptitjob.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * User Data Transfer Object
 */
data class UserDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password_hash")
    val passwordHash: String? = null,
    
    @SerializedName("full_name")
    val fullName: String,
    
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    
    @SerializedName("role")
    val role: UserRole,
    
    @SerializedName("company_id")
    val companyId: String? = null,
    
    @SerializedName("company_name")
    val companyName: String? = null,
    
    @SerializedName("is_active")
    val isActive: Boolean = true,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String,
    
    @SerializedName("refresh_token")
    val refreshToken: String? = null
)

/**
 * User Role Enum
 */
enum class UserRole {
    @SerializedName("candidate")
    CANDIDATE,
    
    @SerializedName("employer")
    EMPLOYER,
    
    @SerializedName("admin")
    ADMIN
}

/**
 * Authentication Response
 */
data class AuthResponse(
    @SerializedName("user")
    val user: UserDto,
    
    @SerializedName("accessToken")
    val accessToken: String,
    
    @SerializedName("refreshToken")
    val refreshToken: String
)

/**
 * Refresh Token Response
 */
data class RefreshTokenResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    
    @SerializedName("refreshToken")
    val refreshToken: String
)

/**
 * User Stats Response
 */
data class UserStatsDto(
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("active")
    val active: Int,
    
    @SerializedName("byRole")
    val byRole: Map<String, Int>
)
