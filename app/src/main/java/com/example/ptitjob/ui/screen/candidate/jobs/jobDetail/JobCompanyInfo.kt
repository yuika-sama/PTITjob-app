package com.example.ptitjob.ui.screen.candidate.jobs.jobDetail

/**
 * Lightweight company model for the job details screen.
 */
data class JobCompanyInfo(
    val id: String,
    val name: String,
    val description: String?,
    val size: String?,
    val address: String?,
    val website: String?,
    val email: String?,
    val logoUrl: String?,
    val jobCount: Int?
)
