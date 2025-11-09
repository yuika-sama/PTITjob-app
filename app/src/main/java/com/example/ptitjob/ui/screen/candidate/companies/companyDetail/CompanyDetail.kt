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
                brush = Brush.verticalGradient(
                    colors = listOf(PTITBackgroundLight, Color.White)
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
                ) {
                    CompanyDetailTabs(
                        activeTab = activeTab,
                        tabs = tabs,
                        onTabChange = { activeTab = it }
                    )

                    Spacer(Modifier.height(PTITSpacing.lg))

                    Box(
                        modifier = Modifier.padding(
                            horizontal = PTITSpacing.lg,
                            vertical = if (activeTab != 1) PTITSpacing.md else 0.dp
                        )
                    ) {
                        when (activeTab) {
                            0 -> CompanyOverviewTab(company = company)
                            1 -> CompanyJobsTab(jobs = company.jobs ?: emptyList())
                            2 -> CompanyReviewsTab()
                            3 -> CompanyContactTab(company = company)
                        }
                    }

                    Spacer(Modifier.height(PTITSpacing.xl))
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Text(
                text = "Chi tiết công ty",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }

        Spacer(Modifier.height(PTITSpacing.lg))

        // Company Info Card - Logo trái, tên phải
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.lg,
            color = Color.White,
            shadowElevation = PTITElevation.md
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                // Logo và tên công ty thẳng hàng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Company Logo - Bên trái
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = PTITCornerRadius.md,
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
                                    modifier = Modifier.fillMaxSize().padding(PTITSpacing.sm),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = "Company logo",
                                    modifier = Modifier.size(40.dp),
                                    tint = PTITTextSecondary
                                )
                            }
                        }
                    }

                    // Company Name - Bên phải
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Action Buttons - Hai nút ngang hàng
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { /* TODO: Follow company */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITError
                        ),
                        shape = PTITCornerRadius.sm,
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(PTITSpacing.xs))
                        Text(
                            "Theo dõi",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { /* TODO: Share company */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PTITInfo
                        ),
                        border = BorderStroke(1.dp, PTITInfo),
                        shape = PTITCornerRadius.sm,
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(PTITSpacing.xs))
                        Text(
                            "Chia sẻ",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
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
    TabRow(
        selectedTabIndex = activeTab,
        containerColor = PTITBackgroundLight,
        contentColor = PTITError,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                color = PTITError,
                height = 2.dp
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = activeTab == index,
                onClick = { onTabChange(index) },
                text = {
                    Text(
                        title,
                        fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                },
                selectedContentColor = PTITError,
                unselectedContentColor = PTITTextSecondary,
                modifier = Modifier.padding(vertical = PTITSpacing.md)
            )
        }
    }
}

@Composable
private fun CompanyOverviewTab(company: Company) {
    Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)) {
        // Giới thiệu công ty
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.md,
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PTITInfo,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Giới thiệu công ty",
                        style = MaterialTheme.typography.titleMedium.copy(
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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary,
                        lineHeight = 22.sp
                    )
                )
            }
        }

        // Thông tin chi tiết
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.md,
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = PTITInfo,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Thông tin chi tiết",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                    CompanyInfoRow("Ngành nghề", company.industry, Icons.Default.Category)
                    HorizontalDivider(color = PTITNeutral100, thickness = 0.5.dp)
                    CompanyInfoRow("Quy mô", company.size ?: "Chưa cập nhật", Icons.Default.People)
                    HorizontalDivider(color = PTITNeutral100, thickness = 0.5.dp)
                    CompanyInfoRow("Địa chỉ", company.address ?: "Chưa cập nhật", Icons.Default.LocationOn)
                }
            }
        }
    }
}

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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PTITTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary,
                    fontWeight = FontWeight.Normal
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun CompanyJobsTab(jobs: List<Job>) {
    if (jobs.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
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

@Composable
private fun CompanyJobCard(job: Job) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg)
            .clickable { /* TODO: navigate to job detail */ },
        shape = PTITCornerRadius.md,
        color = Color.White,
        shadowElevation = 1.dp
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
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                ) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    val preview = job.description
                        ?.toString()
                        ?.trim()
                        .orEmpty()
                        .ifBlank { "Mô tả đang cập nhật." }

                    Text(
                        text = preview,
                        style = MaterialTheme.typography.bodySmall.copy(color = PTITTextSecondary),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = PTITCornerRadius.xs,
                    color = PTITSuccess.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = job.salary,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITSuccess
                        ),
                        modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = 4.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                JobDetailChip(icon = Icons.Default.LocationOn, text = job.location, color = PTITInfo)
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
                    colors = ButtonDefaults.buttonColors(containerColor = PTITError),
                    shape = PTITCornerRadius.xs,
                    contentPadding = PaddingValues(horizontal = PTITSpacing.lg, vertical = 8.dp)
                ) {
                    Text("Ứng tuyển", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun JobDetailChip(icon: ImageVector, text: String, color: Color) {
    Surface(shape = PTITCornerRadius.xs, color = color.copy(alpha = 0.1f)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = 4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall.copy(color = color, fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun EmptyJobsState() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.md,
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Icon(Icons.Default.WorkOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = PTITTextSecondary)
            Text(
                text = "Chưa có vị trí tuyển dụng",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PTITTextPrimary),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Công ty hiện tại chưa có vị trí tuyển dụng nào. Hãy theo dõi để cập nhật thông tin mới nhất.",
                style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompanyReviewsTab() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(64.dp), tint = PTITTextSecondary)
            Text(
                text = "Đánh giá công ty",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PTITTextPrimary),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tính năng đánh giá công ty đang được phát triển. Vui lòng quay lại sau.",
                style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ContactInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
        Icon(imageVector = icon, contentDescription = null, tint = PTITTextSecondary, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = PTITTextSecondary, fontWeight = FontWeight.Normal))
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextPrimary, fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun CompanyContactTab(company: Company) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(PTITSpacing.lg), verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)) {
            Row(horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ContactPhone, contentDescription = null, tint = PTITInfo, modifier = Modifier.size(20.dp))
                Text(text = "Thông tin liên hệ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PTITTextPrimary))
            }

            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                ContactInfoRow(Icons.Default.LocationOn, "Địa chỉ", company.address ?: "Chưa cập nhật")
                HorizontalDivider(color = PTITNeutral100, thickness = 0.5.dp)
                ContactInfoRow(Icons.Default.People, "Quy mô", company.size ?: "Chưa cập nhật")
                HorizontalDivider(color = PTITNeutral100, thickness = 0.5.dp)
                ContactInfoRow(Icons.Default.Work, "Số lượng việc làm", "${company.jobCount ?: 0} vị trí")
                HorizontalDivider(color = PTITNeutral100, thickness = 0.5.dp)
                ContactInfoRow(Icons.Default.Category, "Lĩnh vực", company.industry)
            }

            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PTITError),
                    shape = PTITCornerRadius.sm,
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(PTITSpacing.sm))
                    Text("Liên hệ công ty", fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PTITInfo),
                    border = BorderStroke(1.dp, PTITInfo),
                    shape = PTITCornerRadius.sm,
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(20.dp))
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