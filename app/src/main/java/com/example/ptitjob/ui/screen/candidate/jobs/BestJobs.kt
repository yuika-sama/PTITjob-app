package com.example.ptitjob.ui.screen.candidate.jobs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.component.JobListCard
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.theme.*

@Composable
fun BestJobsScreen() {
    val mockBestJobs = getSampleBestJobs()
    var searchQuery by remember { mutableStateOf("") }
    var locationQuery by remember { mutableStateOf("") }
    var fieldQuery by remember { mutableStateOf("") }
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
                    BestJobsHeader()
                }
            }

            // Advanced Search Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                ) {
                    BestJobsAdvancedSearch(
                        searchQuery = searchQuery,
                        locationQuery = locationQuery,
                        fieldQuery = fieldQuery,
                        onSearchChange = { searchQuery = it },
                        onLocationChange = { locationQuery = it },
                        onFieldChange = { fieldQuery = it },
                        onSearch = { /* TODO: Implement search */ }
                    )
                }
            }

            // Featured Categories
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
                ) {
                    FeaturedCategoriesSection()
                }
            }

            // Results Summary
            item {
                ResultsSummary(totalJobs = mockBestJobs.size)
            }

            // Job List
            items(mockBestJobs) { job ->
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
private fun BestJobsHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PTITSuccess.copy(alpha = 0.2f),
                    modifier = Modifier.size(PTITSize.avatarLg)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = PTITSuccess,
                            modifier = Modifier.size(PTITSize.iconXl)
                        )
                    }
                }
                
                Text(
                    text = "Việc làm tốt nhất",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextLight
                    )
                )
            }

            Spacer(Modifier.height(PTITSpacing.md))
            
            Text(
                text = "Tìm kiếm công việc mơ ước từ những cơ hội việc làm tốt nhất với lương cao và phúc lợi hấp dẫn",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = PTITTextLight.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PTITSpacing.lg)
            )

            Spacer(Modifier.height(PTITSpacing.xl))
            
            // Features Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                contentPadding = PaddingValues(horizontal = PTITSpacing.lg)
            ) {
                items(getBestJobFeatures()) { feature ->
                    FeatureChip(
                        text = feature.title,
                        icon = feature.icon
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = PTITCornerRadius.md,
        color = Color.White.copy(alpha = 0.2f), // Tăng visibility
        modifier = Modifier.clip(PTITCornerRadius.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
            modifier = Modifier.padding(horizontal = PTITSpacing.lg, vertical = PTITSpacing.md)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PTITTextLight,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PTITTextLight
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BestJobsAdvancedSearch(
    searchQuery: String,
    locationQuery: String,
    fieldQuery: String,
    onSearchChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onFieldChange: (String) -> Unit,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = PTITSuccess,
                    modifier = Modifier.size(PTITSize.iconLg)
                )
                Text(
                    text = "Tìm kiếm việc làm tốt nhất",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            
            // Job Title Search
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
                        Icons.Default.Work, 
                        contentDescription = null,
                        tint = PTITSuccess
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.md,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITSuccess,
                    unfocusedBorderColor = PTITNeutral200
                )
            )
            
            // Location and Field in Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                OutlinedTextField(
                    value = locationQuery,
                    onValueChange = onLocationChange,
                    placeholder = { 
                        Text(
                            "Tỉnh/thành phố", 
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
                    modifier = Modifier.weight(1f),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITSuccess,
                        unfocusedBorderColor = PTITNeutral200
                    )
                )
                
                OutlinedTextField(
                    value = fieldQuery,
                    onValueChange = onFieldChange,
                    placeholder = { 
                        Text(
                            "Lĩnh vực", 
                            color = PTITTextSecondary
                        ) 
                    },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.Category, 
                            contentDescription = null,
                            tint = PTITInfo
                        ) 
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = PTITTextSecondary
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITSuccess,
                        unfocusedBorderColor = PTITNeutral200
                    )
                )
            }
            
            // Search Button
            Button(
                onClick = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PTITSize.buttonMd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITSuccess
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
                    "Tìm việc làm tốt nhất",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun FeaturedCategoriesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        Text(
            text = "📂 Danh mục phổ biến", // Thêm icon cho thân thiện
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextLight
            )
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
            contentPadding = PaddingValues(end = PTITSpacing.lg)
        ) {
            items(getFeaturedCategories()) { category ->
                CategoryCard(category = category)
            }
        }
    }
}

@Composable
private fun CategoryCard(category: JobCategory) {
    Surface(
        shape = PTITCornerRadius.lg,
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = PTITElevation.sm,
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Surface(
                shape = CircleShape,
                color = category.color.copy(alpha = 0.1f),
                modifier = Modifier.size(PTITSize.avatarMd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = category.color,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                }
            }
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PTITTextPrimary
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${category.jobCount} việc làm",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary
                )
            )
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
                color = PTITSuccess.copy(alpha = 0.1f),
                modifier = Modifier.size(PTITSize.avatarMd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PTITSuccess,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                }
            }
            
            Text(
                text = buildAnnotatedString {
                    append("Tìm thấy ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = PTITSuccess)) {
                        append("$totalJobs việc làm")
                    }
                    append(" tốt nhất phù hợp với yêu cầu của bạn.")
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
                        if (currentPage > 1) PTITSuccess.copy(alpha = 0.1f) else PTITNeutral100,
                        CircleShape
                    )
                    .size(PTITSize.buttonMd)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Trang trước",
                    tint = if (currentPage > 1) PTITSuccess else PTITTextSecondary
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
                        if (currentPage < totalPages) PTITSuccess.copy(alpha = 0.1f) else PTITNeutral100,
                        CircleShape
                    )
                    .size(PTITSize.buttonMd)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Trang sau",
                    tint = if (currentPage < totalPages) PTITSuccess else PTITTextSecondary
                )
            }
        }
    }
}

// Data classes
data class BestJobFeature(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class JobCategory(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val jobCount: Int
)

private fun getBestJobFeatures(): List<BestJobFeature> {
    return listOf(
        BestJobFeature("Lương cao", Icons.Default.AttachMoney),
        BestJobFeature("Phúc lợi hấp dẫn", Icons.Default.CardGiftcard),
        BestJobFeature("Môi trường chuyên nghiệp", Icons.Default.Business),
        BestJobFeature("Cơ hội thăng tiến", Icons.AutoMirrored.Filled.TrendingUp),
        BestJobFeature("Làm việc linh hoạt", Icons.Default.Schedule)
    )
}

private fun getFeaturedCategories(): List<JobCategory> {
    return listOf(
        JobCategory("Công nghệ", Icons.Default.Computer, PTITInfo, 245),
        JobCategory("Marketing", Icons.Default.Campaign, PTITWarning, 128),
        JobCategory("Tài chính", Icons.Default.AccountBalance, PTITSuccess, 89),
        JobCategory("Thiết kế", Icons.Default.Palette, PTITSecondary, 156),
        JobCategory("Kinh doanh", Icons.Default.Business, PTITPrimary, 203),
        JobCategory("Giáo dục", Icons.Default.School, PTITInfo, 67)
    )
}

@Preview(showBackground = true, device = "spec:width=393dp,height=851dp,dpi=420")
@Composable
fun BestJobsScreenPreview() {
    MaterialTheme {
        BestJobsScreen()
    }
}

private fun getSampleBestJobs(): List<JobListCardData> {
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