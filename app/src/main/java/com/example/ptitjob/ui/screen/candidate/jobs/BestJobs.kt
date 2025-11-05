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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.ptitjob.ui.theme.PTITWarning

@Composable
fun BestJobsRoute(
    onBack: () -> Unit,
    onJobSelected: (String) -> Unit,
    onSaveJob: (JobListCardData) -> Unit = {},
    viewModel: BestJobsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BestJobsScreen(
        state = state,
        onBack = onBack,
        onSearchChange = viewModel::updateSearchQuery,
        onLocationChange = viewModel::updateLocationQuery,
        onFieldChange = viewModel::updateFieldQuery,
        onSearch = viewModel::submitSearch,
        onRefresh = viewModel::refresh,
        onPageChange = viewModel::changePage,
        onJobSelected = { data -> onJobSelected(data.backendId) },
        onSave = onSaveJob
    )
}

@Composable
fun BestJobsScreen(
    state: BestJobsUiState,
    onBack: () -> Unit,
    onSearchChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onFieldChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onPageChange: (Int) -> Unit,
    onJobSelected: (JobListCardData) -> Unit,
    onSave: (JobListCardData) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

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
        when {
            state.isLoading && state.jobs.isEmpty() -> BestJobsLoadingState()
            state.errorMessage != null && state.jobs.isEmpty() -> {
                BestJobsErrorState(message = state.errorMessage, onRetry = onRefresh, onBack = onBack)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = PTITSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                ) {
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                        ) {
                            BestJobsHeader(onBack = onBack)
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                        ) {
                            BestJobsAdvancedSearch(
                                searchQuery = state.searchQuery,
                                locationQuery = state.locationQuery,
                                fieldQuery = state.fieldQuery,
                                onSearchChange = onSearchChange,
                                onLocationChange = onLocationChange,
                                onFieldChange = onFieldChange,
                                onSearch = onSearch
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
                        ) {
                            FeaturedCategoriesSection()
                        }
                    }

                    item {
                        ResultsSummary(totalJobs = state.totalJobs)
                    }

                    if (state.jobs.isEmpty()) {
                        item {
                            EmptyJobsPlaceholder(onRetry = onRefresh)
                        }
                    } else {
                        items(state.jobs) { job ->
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
                                        onApply = onJobSelected,
                                        onSave = onSave,
                                        onCardClick = onJobSelected
                                    )
                                }
                            }
                        }
                    }

                    if (state.errorMessage != null && state.jobs.isNotEmpty()) {
                        item {
                            InlineError(message = state.errorMessage, onRetry = onRefresh)
                        }
                    }

                    if (state.totalPages > 1) {
                        item {
                            PaginationControls(
                                currentPage = state.currentPage,
                                totalPages = state.totalPages,
                                onPageChange = onPageChange
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BestJobsHeader(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(horizontal = PTITSpacing.lg, vertical = PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = PTITTextLight
                    )
                }
                Text(
                    text = "Việc làm tốt nhất",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextLight
                    )
                )
                Spacer(modifier = Modifier.size(PTITSize.iconLg))
            }

            Text(
                text = "Tìm kiếm công việc mơ ước từ những cơ hội việc làm tốt nhất với lương cao và phúc lợi hấp dẫn",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = PTITTextLight.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PTITSpacing.lg)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                contentPadding = PaddingValues(horizontal = PTITSpacing.lg)
            ) {
                items(getBestJobFeatures()) { feature ->
                    FeatureChip(text = feature.title, icon = feature.icon)
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
        color = Color.White.copy(alpha = 0.2f),
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
                        Icons.Default.Business,
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
            text = "📂 Danh mục phổ biến",
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

@Composable
private fun BestJobsLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator(color = PTITSuccess)
    }
}

@Composable
private fun BestJobsErrorState(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Surface(
            shape = PTITCornerRadius.lg,
            color = Color.White,
            shadowElevation = PTITElevation.lg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PTITSpacing.xl)
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Text(
                    text = "Không thể tải danh sách việc làm",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
                    textAlign = TextAlign.Center
                )
                Row(horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                    TextButton(onClick = onBack) {
                        Text("Quay lại")
                    }
                    ElevatedButton(onClick = onRetry) {
                        Text("Thử lại")
                    }
                }
            }
        }
    }
}

@Composable
private fun InlineError(message: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.md,
        color = Color.White,
        shadowElevation = PTITElevation.sm
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Không thể làm mới dữ liệu",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITWarning
                    )
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(color = PTITTextSecondary)
                )
            }
            TextButton(onClick = onRetry) {
                Text("Thử lại")
            }
        }
    }
}

@Composable
private fun EmptyJobsPlaceholder(onRetry: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Text(
                text = "Chưa có việc làm phù hợp",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            Text(
                text = "Hãy thử thay đổi bộ lọc hoặc tìm kiếm với từ khóa khác.",
                style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text("Làm mới")
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
        JobCategory("Thiết kế", Icons.Default.Category, PTITSecondary, 156),
        JobCategory("Kinh doanh", Icons.Default.Business, PTITPrimary, 203),
        JobCategory("Giáo dục", Icons.Default.Schedule, PTITInfo, 67)
    )
}

@Preview(showBackground = true, device = "spec:width=393dp,height=851dp,dpi=420")
@Composable
fun BestJobsScreenPreview() {
    MaterialTheme {
        BestJobsScreen(
            state = sampleBestJobsState(),
            onBack = {},
            onSearchChange = {},
            onLocationChange = {},
            onFieldChange = {},
            onSearch = {},
            onRefresh = {},
            onPageChange = {},
            onJobSelected = {},
            onSave = {}
        )
    }
}

private fun sampleBestJobsState(): BestJobsUiState {
    return BestJobsUiState(
        jobs = getSampleBestJobs(),
        totalJobs = 128,
        totalPages = 5
    )
}

private fun getSampleBestJobs(): List<JobListCardData> {
    return listOf(
        JobListCardData(
            id = 1,
            backendId = "1",
            title = "Senior Frontend Developer - React/Next.js",
            company = "CÔNG TY CÔNG NGHỆ DIGITEQ",
            companyLogo = null,
            salary = "25 - 40 triệu",
            location = "Hà Nội, TP.HCM",
            experience = "5 năm",
            deadline = "15 ngày",
            postedTime = "1 giờ trước",
            isUrgent = true,
            isVerified = true,
            tags = listOf("Hot Job", "Remote", "Tech")
        ),
        JobListCardData(
            id = 2,
            backendId = "2",
            title = "Marketing Manager - Thương Hiệu Quốc Tế",
            company = "UNILEVER VIETNAM",
            companyLogo = null,
            salary = "Từ 30 triệu",
            location = "TP.HCM",
            experience = "3 năm",
            deadline = "20 ngày",
            postedTime = "2 giờ trước",
            isUrgent = false,
            isVerified = true,
            tags = listOf("Thương hiệu lớn", "Marketing")
        )
    )
}
