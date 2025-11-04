package com.example.ptitjob.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Resume Data Transfer Object
 */
data class ResumeDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("file_path")
    val filePath: String,
    
    @SerializedName("file_name")
    val fileName: String,
    
    @SerializedName("file_size")
    val fileSize: Long,
    
    @SerializedName("uploaded_date")
    val uploadedDate: String
)
