package com.example.ptitjob.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Job Application Data Transfer Object
 */
data class JobApplicationDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("job_id")
    val jobId: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("resume_id")
    val resumeId: String,
    
    @SerializedName("cover_letter")
    val coverLetter: String? = null,
    
    @SerializedName("status")
    val status: ApplicationStatus,
    
    @SerializedName("applied_at")
    val appliedAt: String,
    
    @SerializedName("user_name")
    val userName: String? = null,
    
    @SerializedName("user_email")
    val userEmail: String? = null,
    
    @SerializedName("job_name")
    val jobName: String? = null,
    
    @SerializedName("file_url")
    val fileUrl: String? = null
)

/**
 * Application Status Enum
 */
enum class ApplicationStatus {
    @SerializedName("pending")
    PENDING,
    
    @SerializedName("viewed")
    VIEWED,
    
    @SerializedName("shortlisted")
    SHORTLISTED,
    
    @SerializedName("rejected")
    REJECTED,
    
    @SerializedName("hired")
    HIRED
}
