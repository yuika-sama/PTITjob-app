package com.example.ptitjob.data.api.request

import com.google.gson.annotations.SerializedName

/**
 * Create Job Category Request
 */
data class CreateJobCategoryRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("slug")
    val slug: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("is_active")
    val isActive: Boolean = true
)

/**
 * Update Job Category Request
 */
data class UpdateJobCategoryRequest(
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("slug")
    val slug: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("is_active")
    val isActive: Boolean? = null
)

/**
 * Create Location Request
 */
data class CreateLocationRequest(
    @SerializedName("city")
    val city: String,
    
    @SerializedName("slug")
    val slug: String? = null,
    
    @SerializedName("job_count")
    val jobCount: Int? = null
)

/**
 * Update Location Request
 */
data class UpdateLocationRequest(
    @SerializedName("city")
    val city: String? = null,
    
    @SerializedName("slug")
    val slug: String? = null,
    
    @SerializedName("job_count")
    val jobCount: Int? = null
)
