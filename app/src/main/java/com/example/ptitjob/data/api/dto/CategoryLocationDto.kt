package com.example.ptitjob.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Job Category Data Transfer Object
 */
data class JobCategoryDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("slug")
    val slug: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("is_active")
    val isActive: Boolean = true,
    
    @SerializedName("job_count")
    val jobCount: Int? = null,
    
    @SerializedName("icon_url")
    val iconUrl: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String
)

/**
 * Location Data Transfer Object
 */
data class LocationDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("slug")
    val slug: String? = null,
    
    @SerializedName("job_count")
    val jobCount: Int? = null
)
