package com.example.ptitjob.data.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Đại diện cho thông tin chi tiết về một công ty.
 *
 * @param id ID duy nhất của công ty.
 * @param name Tên công ty.
 * @param logo URL đến logo của công ty.
 * @param industry Lĩnh vực hoạt động.
 * @param size Quy mô công ty (tùy chọn).
 * @param address Địa chỉ công ty (tùy chọn).
 * @param jobCount Số lượng công việc đang tuyển (tùy chọn).
 * @param jobs Danh sách các công việc của công ty (tùy chọn).
 */
data class Company(
    val id: String,
    val name: String,
    val logo: String,
    val industry: String,
    val size: String? = null,
    val address: String? = null,
    val jobCount: Int? = null,
    val jobs: List<Job> = emptyList()
)

/**
 * Đại diện cho một thẻ (tag) đặc biệt của công việc.
 * Được chuyển đổi từ kiểu ('TIN MỚI' | 'NỔI BẬT') trong TypeScript.
 */
enum class JobTag(val displayName: String) {
    NEW("TIN MỚI"),
    FEATURED("NỔI BẬT")
}

/**
 * Đại diện cho thông tin đầy đủ của một công việc.
 */
data class Job(
    val id: String,
    val title: String,
    val company: Company,
    val salary: String,
    val location: String,
    val experience: String,
    val deadline: String,
    val tags: List<JobTag>? = null, // Sử dụng enum để đảm bảo an toàn kiểu
    val category: String,
    val description: List<String>,
    val requirements: List<String>,
    val benefits: List<String>,
    val workLocation: String,
    val level: String,
    val education: String,
    val quantity: String,
    val format: String
)

/**
 * Đại diện cho phiên bản tóm tắt của một công việc, thường dùng trong danh sách.
 * Tương đương với Pick<Job, ...> trong TypeScript.
 */
data class JobSummary(
    val id: String,
    val title: String,
    val company: Company,
    val salary: String,
    val location: String,
    val tags: List<JobTag>? = null
)

/**
 * Đại diện cho một danh mục ngành nghề.
 *
 * Trường icon được chia thành hai:
 * @param iconUrl Dành cho icon tải từ URL.
 * @param iconVector Dành cho icon nội bộ từ Jetpack Compose (Icons.Default.*).
 */
data class Category(
    val name: String,
    val jobCount: Int,
    val iconUrl: String? = null,
    val iconVector: ImageVector? = null // Tương đương SvgIconComponent
)

/**
 * Đại diện cho các thông tin chung của một tin tuyển dụng.
 */
data class GeneralInfo(
    val level: String,
    val education: String,
    val quantity: String,
    val format: String
)

/**
 * Đại diện cho một công việc liên quan.
 */
data class RelatedJob(
    val title: String,
    val company: String,
    val salary: String,
    val location: String
)