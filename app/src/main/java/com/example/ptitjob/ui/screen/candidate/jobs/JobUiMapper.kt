package com.example.ptitjob.ui.screen.candidate.jobs

import com.example.ptitjob.data.api.dto.CompanyDto
import com.example.ptitjob.data.api.dto.JobCategoryDto
import com.example.ptitjob.data.api.dto.JobDto
import com.example.ptitjob.data.api.dto.JobType
import com.example.ptitjob.data.api.dto.LocationDto
import com.example.ptitjob.data.model.JobDetailsPageData
import com.example.ptitjob.ui.component.CompanyItem
import com.example.ptitjob.ui.component.FeaturedCompany
import com.example.ptitjob.ui.component.IndustryItem
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.component.RecommendedJob
import com.example.ptitjob.ui.screen.candidate.jobs.jobDetail.JobCompanyInfo
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val VI_LOCALE = Locale("vi", "VN")
private val DISPLAY_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", VI_LOCALE)
private val FALLBACK_LOCAL_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)

/**
 * Convert a [JobDto] returned from backend API to the UI model used by job listing cards.
 */
fun JobDto.toJobListCardData(
    now: Instant = Instant.now(),
    zoneId: ZoneId = ZoneId.systemDefault()
): JobListCardData {
    val expiryInstant = expiryDate.parseToInstant(zoneId)
    val createdInstant = createdAt.parseToInstant(zoneId)
    val postedLabel = createdInstant?.let { relativePostedLabel(it, now, zoneId) } ?: "Mới đăng"
    val remainingLabel = expiryInstant?.let { remainingTimeLabel(it, now, zoneId) }

    return JobListCardData(
        id = id.safeIntId(),
        backendId = id,
        title = title,
        company = companyName,
        companyLogo = logoUrl,
        salary = formatSalary(salaryMin, salaryMax, currency),
        location = locationName.ifBlank { "Đang cập nhật" },
        experience = jobTypeLabel(jobType),
        postedTime = postedLabel,
        deadline = remainingLabel,
        isUrgent = expiryInstant?.let { Duration.between(now, it).toDays() in 0..3 } ?: false,
        isVerified = jobCount != null && jobCount > 0,
        tags = buildList {
            jobTypeLabel(jobType)?.let { add(it) }
            if (categoryName.isNotBlank()) add(categoryName)
        }
    )
}

fun JobDto.toJobDetailsPageData(
    zoneId: ZoneId = ZoneId.systemDefault()
): JobDetailsPageData {
    val createdInstant = createdAt.parseToInstant(zoneId)
    val expiryInstant = expiryDate.parseToInstant(zoneId)

    return JobDetailsPageData(
        id = id,
        title = title,
        companyName = companyName,
        companyId = companyId,
        logoUrl = logoUrl,
        salary = formatSalary(salaryMin, salaryMax, currency),
        jobType = jobTypeLabel(jobType) ?: "Đang cập nhật",
        locationName = locationName.ifBlank { "Đang cập nhật" },
        categoryName = categoryName.ifBlank { "Đang cập nhật" },
        experience = deriveExperience(requirements),
        createdAt = createdInstant?.formatAsDisplayDate(zoneId) ?: "Đang cập nhật",
        deadline = expiryInstant?.formatAsDisplayDate(zoneId) ?: "Đang cập nhật",
        expiryDate = expiryInstant?.toString(),
        description = description,
        requirements = requirements,
        benefits = benefits
    )
}

/**
 * Convert [JobDto] to the compact job recommendation card data.
 */
fun JobDto.toRecommendedJob(
    zoneId: ZoneId = ZoneId.systemDefault()
): RecommendedJob {
    val createdInstant = createdAt.parseToInstant(zoneId)
    val expiryInstant = expiryDate.parseToInstant(zoneId)
    val dateRange = when {
        createdInstant != null && expiryInstant != null -> buildString {
            append(DISPLAY_DATE_FORMATTER.withZone(zoneId).format(createdInstant))
            append(" - ")
            append(DISPLAY_DATE_FORMATTER.withZone(zoneId).format(expiryInstant))
        }
        expiryInstant != null -> DISPLAY_DATE_FORMATTER.withZone(zoneId).format(expiryInstant)
        createdInstant != null -> DISPLAY_DATE_FORMATTER.withZone(zoneId).format(createdInstant)
        else -> "Đang cập nhật"
    }

    return RecommendedJob(
        id = id,
        title = title,
        companyName = companyName,
        logoUrl = logoUrl,
        salary = formatSalary(salaryMin, salaryMax, currency),
        location = locationName.ifBlank { "Đang cập nhật" },
        jobType = jobTypeLabel(jobType) ?: "Không xác định",
        description = description.trim().lineSequence().firstOrNull()?.takeIf { it.isNotBlank() }
            ?: "Khám phá vị trí hấp dẫn tại $companyName.",
        dateRange = dateRange
    )
}

fun CompanyDto.toCompanyItem(): CompanyItem {
    return CompanyItem(
        id = id,
        name = name,
        category = (description ?: "Đang cập nhật").take(60),
        jobs = (jobCount ?: jobsCount ?: 0).coerceAtLeast(0),
        logo = logoUrl,
        location = address ?: "Đang cập nhật",
        region = resolveRegion(address),
        uniqueKey = id
    )
}

fun CompanyDto.toFeaturedCompany(): FeaturedCompany {
    return FeaturedCompany(
        id = id,
        name = name,
        logoUrl = logoUrl,
        companySize = companySize ?: "Đang cập nhật",
        description = description ?: "Nhà tuyển dụng đang cập nhật mô tả.",
        jobCount = (jobCount ?: jobsCount ?: 0).coerceAtLeast(0),
        address = address ?: "Đang cập nhật",
        website = website
    )
}

fun CompanyDto.toJobCompanyInfo(): JobCompanyInfo {
    return JobCompanyInfo(
        id = id,
        name = name,
        description = description,
        size = companySize,
        address = address,
        website = website,
        email = email,
        logoUrl = logoUrl,
        jobCount = jobCount ?: jobsCount
    )
}

fun JobCategoryDto.toIndustryItem(): IndustryItem {
    return IndustryItem(
        id = id.safeIntId(),
        name = name,
        jobs = jobCount ?: 0,
        iconUrl = iconUrl,
        backendId = id
    )
}

fun LocationDto.matches(query: String): Boolean {
    if (query.isBlank()) return false
    val normalizedQuery = query.normalize()
    return city.normalize().contains(normalizedQuery) || (slug?.normalize()?.contains(normalizedQuery) == true)
}

private fun String?.normalize(): String = this?.lowercase(Locale.getDefault())?.trim() ?: ""

private fun String?.parseToInstant(zoneId: ZoneId): Instant? {
    if (this.isNullOrBlank()) return null
    val trimmed = this.trim()
    return parseInstantStrict(trimmed)
        ?: runCatching { OffsetDateTime.parse(trimmed).toInstant() }.getOrNull()
        ?: runCatching { LocalDateTime.parse(trimmed, FALLBACK_LOCAL_FORMATTER).atZone(zoneId).toInstant() }.getOrNull()
}

private fun parseInstantStrict(value: String): Instant? {
    return runCatching { Instant.parse(value) }.getOrNull()
}

private fun String.safeIntId(): Int = this.toIntOrNull() ?: this.hashCode()

private fun jobTypeLabel(type: JobType?): String? = when (type) {
    JobType.FULL_TIME -> "Toàn thời gian"
    JobType.PART_TIME -> "Bán thời gian"
    JobType.CONTRACT -> "Hợp đồng"
    JobType.INTERNSHIP -> "Thực tập"
    JobType.FREELANCE -> "Freelance"
    null -> null
}

private fun formatSalary(min: Double, max: Double, currency: String): String {
    if (min <= 0 && max <= 0) return "Thỏa thuận"
    val formatter = NumberFormat.getNumberInstance(VI_LOCALE)
    val minFormatted = min.takeIf { it > 0 }?.let { formatter.format(it) }
    val maxFormatted = max.takeIf { it > 0 }?.let { formatter.format(it) }
    val currencyLabel = if (currency.equals("USD", ignoreCase = true)) "USD" else "VNĐ"
    return when {
        minFormatted != null && maxFormatted != null -> "$minFormatted - $maxFormatted $currencyLabel"
        minFormatted != null -> "Từ $minFormatted $currencyLabel"
        maxFormatted != null -> "Đến $maxFormatted $currencyLabel"
        else -> "Thỏa thuận"
    }
}

private fun remainingTimeLabel(expiry: Instant, now: Instant, zoneId: ZoneId): String {
    val duration = Duration.between(now, expiry)
    return when {
        duration.isNegative -> "đã hết hạn"
        duration.toDays() > 30 -> DISPLAY_DATE_FORMATTER.withZone(zoneId).format(expiry)
        duration.toDays() >= 1 -> "${duration.toDays()} ngày"
        duration.toHours() >= 1 -> "${duration.toHours()} giờ"
        duration.toMinutes() >= 1 -> "${duration.toMinutes()} phút"
        else -> "ít phút"
    }
}

private fun Instant.formatAsDisplayDate(zoneId: ZoneId): String =
    DISPLAY_DATE_FORMATTER.withZone(zoneId).format(this)

private fun deriveExperience(requirements: String?): String {
    if (requirements.isNullOrBlank()) return "Đang cập nhật"
    val candidates = requirements
        .lineSequence()
        .map { it.trim('-','•','–',' ') }
        .filter { it.isNotEmpty() }
    return candidates.firstOrNull { it.contains("năm", ignoreCase = true) }
        ?: candidates.firstOrNull()?.takeIf { it.length <= 50 }
        ?: "Đang cập nhật"
}

private fun relativePostedLabel(created: Instant, now: Instant, zoneId: ZoneId): String {
    val duration = Duration.between(created, now)
    return when {
        duration.toDays() >= 1 -> "Đăng ${duration.toDays()} ngày trước"
        duration.toHours() >= 1 -> "Đăng ${duration.toHours()} giờ trước"
        duration.toMinutes() >= 1 -> "Đăng ${duration.toMinutes()} phút trước"
        else -> DISPLAY_DATE_FORMATTER.withZone(zoneId).format(created)
    }
}

private fun resolveRegion(address: String?): String {
    val normalized = address?.lowercase(Locale.getDefault()) ?: return "Toàn quốc"
    return when {
        normalized.contains("hà nội") || normalized.contains("ha noi") -> "Miền Bắc"
        normalized.contains("hải phòng") -> "Miền Bắc"
        normalized.contains("đà nẵng") || normalized.contains("da nang") -> "Miền Trung"
        normalized.contains("huế") || normalized.contains("hue") -> "Miền Trung"
        normalized.contains("tp.hcm") || normalized.contains("hồ chí minh") || normalized.contains("ho chi minh") -> "Miền Nam"
        normalized.contains("cần thơ") -> "Miền Nam"
        else -> "Toàn quốc"
    }
}

internal fun JobDto.createdInstant(zoneId: ZoneId = ZoneId.systemDefault()): Instant? =
    createdAt.parseToInstant(zoneId)

internal fun JobDto.expiryInstant(zoneId: ZoneId = ZoneId.systemDefault()): Instant? =
    expiryDate.parseToInstant(zoneId)
