package com.example.ptitjob.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Job Data Transfer Object
 */
data class JobDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("requirements")
    val requirements: String,
    
    @SerializedName("benefits")
    val benefits: String,
    
    @SerializedName("salary_min")
    val salaryMin: Double,
    
    @SerializedName("salary_max")
    val salaryMax: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("job_type")
    val jobType: JobType? = null,
    
    @SerializedName("status")
    val status: JobStatus,
    
    @SerializedName("expiry_date")
    val expiryDate: String,
    
    @SerializedName("company_name")
    val companyName: String,
    
    @SerializedName("category_name")
    val categoryName: String,
    
    @SerializedName("location_name")
    val locationName: String,
    
    @SerializedName("logo_url")
    val logoUrl: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String,
    
    @SerializedName("company_id")
    val companyId: String,
    
    @SerializedName("category_id")
    val categoryId: String,
    
    @SerializedName("location_id")
    val locationId: String,
    
    @SerializedName("job_count")
    val jobCount: Int? = null
)

/**
 * Job Type Enum
 */
enum class JobType {
    @SerializedName("full_time")
    FULL_TIME,
    
    @SerializedName("part_time")
    PART_TIME,
    
    @SerializedName("contract")
    CONTRACT,
    
    @SerializedName("internship")
    INTERNSHIP,
    
    @SerializedName("freelance")
    FREELANCE
}

/**
 * Job Status Enum
 */
enum class JobStatus {
    @SerializedName("draft")
    DRAFT,
    
    @SerializedName("published")
    PUBLISHED,
    
    @SerializedName("expired")
    EXPIRED,
    
    @SerializedName("closed")
    CLOSED
}
