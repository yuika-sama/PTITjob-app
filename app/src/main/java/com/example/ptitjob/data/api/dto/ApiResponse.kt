package com.example.ptitjob.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Generic API Response wrapper
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("count")
    val count: Int? = null
)

/**
 * Paginated API Response
 */
data class PaginatedResponse<T>(
    @SerializedName("data")
    val data: List<T>,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("limit")
    val limit: Int,
    
    @SerializedName("totalPages")
    val totalPages: Int
)

/**
 * API Error Response
 */
data class ApiError(
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("errors")
    val errors: List<String>? = null,
    
    @SerializedName("timestamp")
    val timestamp: String
)
