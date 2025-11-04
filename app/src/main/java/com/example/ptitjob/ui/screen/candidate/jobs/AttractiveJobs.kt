package com.example.ptitjob.ui.screen.candidate.jobs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.ptitjob.ui.component.JobListCard
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITInfo
import com.example.ptitjob.ui.theme.PTITNeutral100
import com.example.ptitjob.ui.theme.PTITNeutral200
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITSecondary
import com.example.ptitjob.ui.theme.PTITSize
import com.example.ptitjob.ui.theme.PTITSpacing
import com.example.ptitjob.ui.theme.PTITSuccess
import com.example.ptitjob.ui.theme.PTITTextLight
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITTextSecondary

@Composable
fun AttractiveJobsScreen() {
    val mockAttractiveJobs = getSampleAttractiveJobs()
    var searchQuery by remember { mutableStateOf("") }
    var locationQuery by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                    startY = 0f,
                    endY = 1000f
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Header Banner
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                ) {
                    AttractiveJobsHeader()
                }
            }

            // Search Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                ) {
                    AttractiveJobsSearchSection(
                        searchQuery = searchQuery,
                        locationQuery = locationQuery,
                        onSearchChange = { searchQuery = it },
                        onLocationChange = { locationQuery = it },
                        onSearch = { /* TODO: Implement search */ }
                    )
                }
            }

            // Quick Filters
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
                ) {
                    QuickFiltersSection()
                }
            }

            // Results Summary
            item {
                ResultsSummary(totalJobs = mockAttractiveJobs.size)
            }

            // Job List
            items(mockAttractiveJobs) { job ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = PTITSpacing.lg),
                        shape = PTITCornerRadius.lg,
                        color = Color.White,
                        shadowElevation = PTITElevation.md
                    ) {
                        JobListCard(
                            job = job,
                            onApply = { /* TODO */ },
                            onSave = { /* TODO */ }
                        )
                    }
                }
            }

            // Pagination
            item {
                PaginationControls(
                    currentPage = 1,
                    totalPages = 18,
                    onPageChange = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
private fun AttractiveJobsHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with gradient text effect
            Text(
                text = "Việc làm hấp dẫn",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextLight
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(PTITSpacing.md))
            
            Text(
                text = "Khám phá những cơ hội việc làm hấp dẫn nhất với mức lương và phúc lợi vượt trội",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = PTITTextLight.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PTITSpacing.lg)
            )

            Spacer(Modifier.height(PTITSpacing.xl))
            
            // Stats Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xl),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatCard(
                    title = "642+",
                    subtitle = "Việc làm hấp dẫn",
                    icon = Icons.Default.Work,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "98%",
                    subtitle = "Lương cạnh tranh",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "24/7",
                    subtitle = "Hỗ trợ ứng viên",
                    icon = Icons.Default.Support,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = PTITCornerRadius.lg,
        color = Color.White.copy(alpha = 0.2f), // Tăng alpha để dễ nhìn hơn
        tonalElevation = PTITElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md) // Tăng spacing
        ) {
            Surface(
                shape = CircleShape,
                color = PTITSuccess.copy(alpha = 0.2f),
                modifier = Modifier.size(PTITSize.iconXl)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PTITTextLight,
                        modifier = Modifier.size(PTITSize.iconLg)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextLight
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextLight.copy(alpha = 0.95f), // Tăng contrast cho dễ đọc
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp // Tăng line height
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttractiveJobsSearchSection(
    searchQuery: String,
    locationQuery: String,
    onSearchChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.lg
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Text(
                text = "Tìm kiếm việc làm hấp dẫn",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { 
                    Text(
                        "Nhập tên công việc, vị trí...", 
                        color = PTITTextSecondary
                    ) 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = null,
                        tint = PTITPrimary
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.md,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    unfocusedBorderColor = PTITNeutral200
                )
            )
            
            // Location Input
            OutlinedTextField(
                value = locationQuery,
                onValueChange = onLocationChange,
                placeholder = { 
                    Text(
                        "Chọn tỉnh/thành phố", 
                        color = PTITTextSecondary
                    ) 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.LocationOn, 
                        contentDescription = null,
                        tint = PTITSecondary
                    ) 
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = PTITTextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.md,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    unfocusedBorderColor = PTITNeutral200
                )
            )
            
            // Search Button
            Button(
                onClick = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PTITSize.buttonMd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITPrimary
                ),
                shape = PTITCornerRadius.md
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Spacer(Modifier.width(PTITSpacing.sm))
                Text(
                    "Tìm việc làm hấp dẫn",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun QuickFiltersSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg) // Tăng spacing
    ) {
        Text(
            text = "🔍 Bộ lọc nhanh", // Thêm emoji để thân thiện
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextLight
            )
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
            contentPadding = PaddingValues(end = PTITSpacing.lg)
        ) {
            items(getQuickFilters()) { filter ->
                FilterChip(
                    onClick = { /* TODO */ },
                    label = { 
                        Text(
                            filter.label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        ) 
                    },
                    selected = false,
                    leadingIcon = {
                        Icon(
                            filter.icon,
                            contentDescription = null,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White.copy(alpha = 0.9f),
                        labelColor = PTITTextPrimary,
                        iconColor = PTITPrimary,
                        selectedContainerColor = PTITPrimary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        selected = true,
                        enabled = true,
                        borderColor = PTITNeutral200,
                        selectedBorderColor = PTITPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun ResultsSummary(totalJobs: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.md,
        color = Color.White.copy(alpha = 0.9f),
        tonalElevation = PTITElevation.sm
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Surface(
                shape = CircleShape,
                color = PTITInfo.copy(alpha = 0.1f),
                modifier = Modifier.size(PTITSize.avatarMd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        tint = PTITInfo,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                }
            }
            
            Text(
                text = buildAnnotatedString {
                    append("Tìm thấy ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = PTITPrimary)) {
                        append("$totalJobs việc làm")
                    }
                    append(" hấp dẫn phù hợp với yêu cầu của bạn.")
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun PaginationControls(
    currentPage: Int, 
    totalPages: Int, 
    onPageChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.md
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPageChange(currentPage - 1) }, 
                enabled = currentPage > 1,
                modifier = Modifier
                    .background(
                        if (currentPage > 1) PTITPrimary.copy(alpha = 0.1f) else PTITNeutral100,
                        CircleShape
                    )
                    .size(PTITSize.buttonMd)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Trang trước",
                    tint = if (currentPage > 1) PTITPrimary else PTITTextSecondary
                )
            }

            Spacer(Modifier.width(PTITSpacing.lg))
            
            Text(
                text = "Trang $currentPage / $totalPages",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PTITTextPrimary
                )
            )

            Spacer(Modifier.width(PTITSpacing.lg))
            
            IconButton(
                onClick = { onPageChange(currentPage + 1) }, 
                enabled = currentPage < totalPages,
                modifier = Modifier
                    .background(
                        if (currentPage < totalPages) PTITPrimary.copy(alpha = 0.1f) else PTITNeutral100,
                        CircleShape
                    )
                    .size(PTITSize.buttonMd)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Trang sau",
                    tint = if (currentPage < totalPages) PTITPrimary else PTITTextSecondary
                )
            }
        }
    }
}

// Data classes for filters
data class QuickFilter(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private fun getQuickFilters(): List<QuickFilter> {
    return listOf(
        QuickFilter("Lương cao", Icons.AutoMirrored.Filled.TrendingUp),
        QuickFilter("Remote", Icons.Default.Home),
        QuickFilter("Part-time", Icons.Default.Schedule),
        QuickFilter("Full-time", Icons.Default.Work),
        QuickFilter("Startup", Icons.Default.Rocket),
        QuickFilter("Công nghệ", Icons.Default.Computer),
        QuickFilter("Marketing", Icons.Default.Campaign),
        QuickFilter("Thiết kế", Icons.Default.Palette)
    )
}

@Preview(showBackground = true, device = "spec:width=393dp,height=851dp,dpi=420")
@Composable
fun AttractiveJobsScreenPreview() {
    MaterialTheme {
        AttractiveJobsScreen()
    }
}

private fun getSampleAttractiveJobs(): List<JobListCardData> {
    return listOf(
        JobListCardData(
            id = 1,
            title = "Senior Frontend Developer - React/Next.js",
            company = "CÔNG TY CÔNG NGHỆ DIGITEQ",
            companyLogo = null,
            salary = "25 - 40 triệu",
            location = "Hà Nội, TP.HCM",
            experience = null,
            deadline = "15 ngày",
            postedTime = "1 giờ trước",
            isUrgent = true,
            isVerified = true,
            tags = listOf("Hot Job", "Remote", "Tech")
        ),
        JobListCardData(
            id = 2,
            title = "Marketing Manager - Thương Hiệu Quốc Tế",
            company = "UNILEVER VIETNAM",
            companyLogo = null,
            salary = "Từ 30 triệu",
            location = "TP.HCM",
            experience = null,
            deadline = "20 ngày",
            postedTime = "2 giờ trước",
            isUrgent = false,
            isVerified = true,
            tags = listOf("Thương hiệu lớn", "Marketing")
        ),
        JobListCardData(
            id = 3,
            title = "DevOps Engineer - Startup Fintech",
            company = "MOMO E-WALLET",
            companyLogo = null,
            salary = "35 - 55 triệu",
            location = "TP.HCM",
            experience = null,
            deadline = "25 ngày",
            postedTime = "30 phút trước",
            isUrgent = true,
            isVerified = true,
            tags = listOf("Fintech", "Startup", "DevOps")
        ),
        JobListCardData(
            id = 4,
            title = "UI/UX Designer - App Mobile",
            company = "VIETCOMBANK",
            companyLogo = null,
            salary = "20 - 35 triệu",
            location = "Hà Nội",
            experience = null,
            deadline = "10 ngày",
            postedTime = "45 phút trước",
            isUrgent = true,
            isVerified = false,
            tags = listOf("Design", "Mobile", "Banking")
        ),
        JobListCardData(
            id = 5,
            title = "Data Scientist - AI/ML",
            company = "FPT SOFTWARE",
            companyLogo = null,
            salary = "40 - 60 triệu",
            location = "Đà Nẵng, TP.HCM",
            experience = null,
            deadline = "30 ngày",
            postedTime = "3 giờ trước",
            isUrgent = false,
            isVerified = true,
            tags = listOf("AI/ML", "Data Science", "Remote")
        )
    )
}