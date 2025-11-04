package com.example.ptitjob.data.api.request

import com.example.ptitjob.data.api.dto.JobStatus
import com.example.ptitjob.data.api.dto.JobType
import com.google.gson.annotations.SerializedName

/**
 * Create Job Request
 */
data class CreateJobRequest(
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
    val jobType: JobType,
    
    @SerializedName("status")
    val status: JobStatus,
    
    @SerializedName("expiry_date")
    val expiryDate: String,
    
    @SerializedName("company_name")
    val companyName: String,
    
    @SerializedName("category_name")
    val categoryName: String,
    
    @SerializedName("location_name")
    val locationName: String
)

/**
 * Update Job Request
 */
data class UpdateJobRequest(
    @SerializedName("title")
    val title: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("requirements")
    val requirements: String? = null,
    
    @SerializedName("benefits")
    val benefits: String? = null,
    
    @SerializedName("salary_min")
    val salaryMin: Double? = null,
    
    @SerializedName("salary_max")
    val salaryMax: Double? = null,
    
    @SerializedName("currency")
    val currency: String? = null,
    
    @SerializedName("job_type")
    val jobType: JobType? = null,
    
    @SerializedName("status")
    val status: JobStatus? = null,
    
    @SerializedName("expiry_date")
    val expiryDate: String? = null,
    
    @SerializedName("company_name")
    val companyName: String? = null,
    
    @SerializedName("category_name")
    val categoryName: String? = null,
    
    @SerializedName("location_name")
    val locationName: String? = null
)
