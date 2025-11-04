package com.example.ptitjob.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Company Data Transfer Object
 */
data class CompanyDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("website")
    val website: String? = null,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("company_size")
    val companySize: String? = null,
    
    @SerializedName("address")
    val address: String? = null,
    
    @SerializedName("logo_url")
    val logoUrl: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String,
    
    @SerializedName("job_count")
    val jobCount: Int? = null,
    
    @SerializedName("jobs_count")
    val jobsCount: Int? = null,
    
    @SerializedName("jobs")
    val jobs: List<JobDto>? = null
)

/**
 * Company Stats Response
 */
data class CompanyStatsDto(
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("bySize")
    val bySize: Map<String, Int>,
    
    @SerializedName("withWebsite")
    val withWebsite: Int,
    
    @SerializedName("withLogo")
    val withLogo: Int
)
