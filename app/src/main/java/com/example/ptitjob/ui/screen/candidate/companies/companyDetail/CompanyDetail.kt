package com.example.ptitjob.ui.screen.candidate.companies.companyDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ptitjob.data.model.Company
import com.example.ptitjob.data.model.Job
import com.example.ptitjob.ui.theme.*
import androidx.compose.foundation.BorderStroke

@Composable
fun CompanyDetailScreen(
    company: Company,
    onBack: () -> Unit
) {
    var activeTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Tổng quan", "Việc làm", "Đánh giá", "Liên hệ")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                    startY = 0f,
                    endY = 600f
                )
            )
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                CompanyDetailHeader(company = company, onBack = onBack)
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(PTITCornerRadius.xl)
                        .background(PTITBackgroundLight)
                        .padding(PTITSpacing.lg)
                ) {
                    CompanyDetailTabs(
                        activeTab = activeTab,
                        tabs = tabs,
                        onTabChange = { activeTab = it }
                    )

                    Spacer(Modifier.height(PTITSpacing.xl))

                    Box(
                        modifier = Modifier.padding(
                            if (activeTab != 1) PTITSpacing.lg else 0.dp
                        )
                    ) {
                        when (activeTab) {
                            0 -> CompanyOverviewTab(company = company)
                            1 -> CompanyJobsTab(jobs = company.jobs ?: emptyList())
                            2 -> CompanyReviewsTab()
                            3 -> CompanyContactTab(company = company)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanyDetailHeader(
    company: Company,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PTITSpacing.lg)
    ) {
        // Back Button and Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PTITTextLight)
            }
            Text(
                text = "Chi tiết công ty",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextLight
                )
            )
        }

        Spacer(Modifier.height(PTITSpacing.xl))

        // Company Info Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.lg,
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = PTITElevation.lg
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Company Logo
                    Surface(
                        modifier = Modifier.size(PTITSize.avatarXl),
                        shape = PTITCornerRadius.lg,
                        color = PTITNeutral50,
                        tonalElevation = PTITElevation.xs
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (company.logo.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(company.logo)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "${company.name} logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = "Company logo",
                                    modifier = Modifier.size(PTITSize.iconXl),
                                    tint = PTITTextSecondary
                                )
                            }
                        }
                    }

                    // Company Details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        Text(
                            text = company.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITTextPrimary
                            )
                        )
                        Text(
                            text = company.industry,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = PTITSecondary
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                        ) {
                            company.size?.let { size ->
                                CompanyInfoChip(
                                    icon = Icons.Default.People,
                                    text = size,
                                    color = PTITInfo
                                )
                            }
                            company.address?.let { address ->
                                CompanyInfoChip(
                                    icon = Icons.Default.LocationOn,
                                    text = address,
                                    color = PTITSecondary
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { /* TODO: Follow company */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITPrimary
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(PTITSpacing.sm))
                        Text("Theo dõi", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { /* TODO: Share company */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PTITSecondary
                        ),
                        border = BorderStroke(1.dp, PTITSecondary),
                        shape = PTITCornerRadius.md
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(PTITSpacing.sm))
                        Text("Chia sẻ", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanyInfoChip(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Surface(
        shape = PTITCornerRadius.sm,
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
            modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyDetailTabs(
    activeTab: Int,
    tabs: List<String>,
    onTabChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.Transparent,
            contentColor = PTITPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = PTITPrimary,
                    height = 3.dp
                )
            },
            modifier = Modifier.padding(PTITSpacing.sm)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { onTabChange(index) },
                    text = {
                        Text(
                            title,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    selectedContentColor = PTITPrimary,
                    unselectedContentColor = PTITTextSecondary
                )
            }
        }
    }
}

@Composable
private fun CompanyOverviewTab(company: Company) {
    Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.lg,
            color = Color.White,
            tonalElevation = PTITElevation.sm
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PTITInfo,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                    Text(
                        text = "Giới thiệu công ty",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                }
                Text(
                    text = "Đây là một công ty hàng đầu trong lĩnh vực ${company.industry.lowercase()}, " +
                            "với đội ngũ ${company.size?.lowercase() ?: "chuyên nghiệp"} và văn hóa làm việc năng động. " +
                            "Chúng tôi cam kết mang đến môi trường làm việc tốt nhất cho nhân viên và " +
                            "đóng góp tích cực cho sự phát triển của ngành.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PTITTextSecondary,
                        lineHeight = 24.sp
                    )
                )
            }
        }

        CompanyInfoSection(company = company)
    }
}

@Composable
private fun CompanyInfoSection(company: Company) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = PTITSecondary,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Thông tin chi tiết",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                CompanyInfoRow("Ngành nghề", company.industry, Icons.Default.Category)
                CompanyInfoRow("Quy mô", company.size ?: "Chưa cập nhật", Icons.Default.People)
                CompanyInfoRow("Địa chỉ", company.address ?: "Chưa cập nhật", Icons.Default.LocationOn)
                CompanyInfoRow("Số lượng việc làm", "${company.jobCount ?: 0} vị trí", Icons.Default.Work)
            }
        }
    }
}

/* ========== FIXED FUNCTION ========== */
@Composable
private fun CompanyJobsTab(jobs: List<Job>) {
    if (jobs.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)) {
            Text(
                text = "Có ${jobs.size} vị trí đang tuyển dụng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                modifier = Modifier.padding(horizontal = PTITSpacing.lg)
            )
            jobs.forEach { job -> CompanyJobCard(job = job) }
        }
    } else {
        EmptyJobsState()
    }
}
/* ========== /FIXED FUNCTION ========== */

@Composable
private fun CompanyInfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        Surface(shape = CircleShape, color = PTITNeutral50, modifier = Modifier.size(40.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PTITTextSecondary,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun CompanyJobCard(job: Job) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: navigate to job detail */ },
        shape = PTITCornerRadius.xl,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // An toàn với mọi kiểu mô tả
                    val preview = job.description
                        ?.toString()
                        ?.trim()
                        .orEmpty()
                        .ifBlank { "Mô tả đang cập nhật." }

                    Text(
                        text = preview,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = PTITCornerRadius.sm,
                    color = PTITSuccess.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = job.salary,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITSuccess
                        ),
                        modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                JobDetailChip(icon = Icons.Default.LocationOn, text = job.location, color = PTITInfo)
                // Đổi WorkHistory -> Schedule để tránh unresolved symbol
                JobDetailChip(icon = Icons.Default.Schedule, text = job.experience, color = PTITSecondary)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hạn nộp: ${job.deadline}",
                    style = MaterialTheme.typography.bodySmall.copy(color = PTITTextSecondary)
                )

                Button(
                    onClick = { /* TODO: Apply for job */ },
                    colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary),
                    shape = PTITCornerRadius.md
                ) {
                    Text("Ứng tuyển", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun JobDetailChip(icon: ImageVector, text: String, color: Color) {
    Surface(shape = PTITCornerRadius.sm, color = color.copy(alpha = 0.1f)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
            modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(PTITSize.iconSm))
            Text(text = text, style = MaterialTheme.typography.bodySmall.copy(color = color, fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun EmptyJobsState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.xl,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Icon(Icons.Default.WorkOff, contentDescription = null, modifier = Modifier.size(PTITSize.iconXl), tint = PTITTextSecondary)
            Text(
                text = "Chưa có vị trí tuyển dụng",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PTITTextPrimary),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Công ty hiện tại chưa có vị trí tuyển dụng nào. Hãy theo dõi để cập nhật thông tin mới nhất.",
                style = MaterialTheme.typography.bodyLarge.copy(color = PTITTextSecondary),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompanyReviewsTab() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.xl,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(PTITSize.iconXl), tint = PTITTextSecondary)
            Text(
                text = "Đánh giá công ty",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PTITTextPrimary),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tính năng đánh giá công ty đang được phát triển. Vui lòng quay lại sau.",
                style = MaterialTheme.typography.bodyLarge.copy(color = PTITTextSecondary),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ContactInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
        Surface(shape = CircleShape, color = PTITNeutral50, modifier = Modifier.size(40.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = PTITTextSecondary, modifier = Modifier.size(PTITSize.iconSm))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary, fontWeight = FontWeight.Medium))
            Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(color = PTITTextPrimary, fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun CompanyContactTab(company: Company) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.xl,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(modifier = Modifier.padding(PTITSpacing.xl), verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)) {
            Row(horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ContactPhone, contentDescription = null, tint = PTITInfo, modifier = Modifier.size(PTITSize.iconMd))
                Text(text = "Thông tin liên hệ", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PTITTextPrimary))
            }

            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                ContactInfoRow(Icons.Default.LocationOn, "Địa chỉ", company.address ?: "Chưa cập nhật")
                ContactInfoRow(Icons.Default.People, "Quy mô", company.size ?: "Chưa cập nhật")
                ContactInfoRow(Icons.Default.Work, "Số lượng việc làm", "${company.jobCount ?: 0} vị trí")
                ContactInfoRow(Icons.Default.Category, "Lĩnh vực", company.industry)
            }

            HorizontalDivider(color = PTITNeutral300)

            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary),
                    shape = PTITCornerRadius.md
                ) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Spacer(Modifier.width(PTITSpacing.sm))
                    Text("Liên hệ công ty", fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PTITSecondary),
                    border = BorderStroke(1.dp, PTITSecondary),
                    shape = PTITCornerRadius.md
                ) {
                    Icon(Icons.Default.Language, contentDescription = null)
                    Spacer(Modifier.width(PTITSpacing.sm))
                    Text("Xem website", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CompanyDetailScreenPreview() {
    val sampleCompany = Company(
        id = "1",
        name = "FPT Software",
        logo = "https://fastly.picsum.photos/id/1/200/200.jpg",
        industry = "Công nghệ thông tin",
        size = "10000+ nhân viên",
        address = "FPT Tower, 10 Phạm Văn Bạch, Cầu Giấy, Hà Nội",
        jobCount = 150
    )

    MaterialTheme {
        CompanyDetailScreen(company = sampleCompany, onBack = {})
    }
}
