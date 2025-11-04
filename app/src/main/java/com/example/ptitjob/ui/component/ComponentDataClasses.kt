package com.example.ptitjob.ui.component

import com.example.ptitjob.data.model.CompanySidebarInfo

// --- Data Models (Chuyển đổi từ TypeScript interfaces) ---

data class CompanyBasicInfo(
    val id: Int,
    val name: String,
    val logo: String,
    val industry: String
)

data class GeneralJobInfo(
    val level: String,
    val education: String,
    val quantity: String,
    val format: String
)

data class RelatedJobData(
    val title: String,
    val company: String,
    val salary: String,
    val location: String
)

// Data class chính chứa toàn bộ thông tin cho trang chi tiết
data class JobDetailData(
    val title: String,
    val company: CompanyBasicInfo,
    val salary: String,
    val location: String,
    val experience: String,
    val deadline: String,
    val category: String,
    val description: List<String>,
    val requirements: List<String>,
    val benefits: List<String>,
    val workLocation: String,
    val companyInfo: CompanySidebarInfo,
    val generalInfo: GeneralJobInfo
)
