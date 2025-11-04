package com.example.ptitjob.data.model

data class JobDetailsPageData(
    val id: String,
    val title: String,
    val companyName: String,
    val companyId: String,
    val logoUrl: String?,
    val salary: String,
    val jobType: String,
    val locationName: String,
    val categoryName: String,
    val experience: String,
    val createdAt: String,
    val deadline: String,
    val expiryDate: String?,
    val description: String?,
    val requirements: String?,
    val benefits: String?
)