package com.example.ptitjob.data.api.request

import com.example.ptitjob.data.api.dto.ApplicationStatus
import com.google.gson.annotations.SerializedName

/**
 * Create Job Application Request
 */
data class CreateJobApplicationRequest(
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("job_id")
    val jobId: String,
    
    @SerializedName("resume_id")
    val resumeId: String? = null,
    
    @SerializedName("cover_letter")
    val coverLetter: String? = null
)

/**
 * Update Application Status Request
 */
data class UpdateApplicationStatusRequest(
    @SerializedName("status")
    val status: ApplicationStatus,
    
    @SerializedName("note")
    val note: String? = null
)

/**
 * Bulk Update Status Request
 */
data class BulkUpdateStatusRequest(
    @SerializedName("applicationIds")
    val applicationIds: List<String>,
    
    @SerializedName("status")
    val status: ApplicationStatus,
    
    @SerializedName("note")
    val note: String? = null
)
