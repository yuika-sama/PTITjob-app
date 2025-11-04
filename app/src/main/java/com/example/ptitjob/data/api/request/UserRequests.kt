package com.example.ptitjob.data.api.request

import com.example.ptitjob.data.api.dto.UserRole
import com.google.gson.annotations.SerializedName

/**
 * Create User Request
 */
data class CreateUserRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password_hash")
    val passwordHash: String,
    
    @SerializedName("full_name")
    val fullName: String,
    
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    
    @SerializedName("role")
    val role: UserRole,
    
    @SerializedName("company_id")
    val companyId: String? = null,
    
    @SerializedName("is_active")
    val isActive: Boolean = true
)

/**
 * Update User Request
 */
data class UpdateUserRequest(
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("password_hash")
    val passwordHash: String? = null,
    
    @SerializedName("full_name")
    val fullName: String? = null,
    
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    
    @SerializedName("role")
    val role: UserRole? = null,
    
    @SerializedName("company_id")
    val companyId: String? = null,
    
    @SerializedName("is_active")
    val isActive: Boolean? = null
)
