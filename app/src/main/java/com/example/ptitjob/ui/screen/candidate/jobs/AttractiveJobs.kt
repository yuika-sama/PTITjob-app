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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.material3.TextButton
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

@Composable
fun AttractiveJobsRoute(
    onBack: () -> Unit,
    onJobSelected: (String) -> Unit,
    onSaveJob: (JobListCardData) -> Unit = {},
    viewModel: AttractiveJobsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AttractiveJobsScreen(
        state = state,
        onBack = onBack,
        onSearchChange = viewModel::updateSearchQuery,
        onLocationChange = viewModel::updateLocationQuery,
        onSearch = viewModel::submitSearch,
        onRefresh = viewModel::refresh,
        onPageChange = viewModel::changePage,
        onFilterToggle = viewModel::toggleQuickFilter,
        onJobSelected = { data -> onJobSelected(data.backendId) },
        onSave = onSaveJob
    )
}

@Composable
fun AttractiveJobsScreen(
    state: AttractiveJobsUiState,
    onBack: () -> Unit,
    onSearchChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onPageChange: (Int) -> Unit,
    onFilterToggle: (AttractiveQuickFilter) -> Unit,
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
            state.isLoading && state.jobs.isEmpty() -> AttractiveJobsLoadingState()
            state.errorMessage != null && state.jobs.isEmpty() -> AttractiveJobsErrorState(
                message = state.errorMessage,
                onRetry = onRefresh,
                onBack = onBack
            )
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
                            AttractiveJobsHeader(onBack = onBack)
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                        ) {
                            AttractiveJobsSearchSection(
                                searchQuery = state.searchQuery,
                                locationQuery = state.locationQuery,
                                onSearchChange = onSearchChange,
                                onLocationChange = onLocationChange,
                                onSearch = onSearch
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
                        ) {
                            QuickFiltersSection(
                                selectedFilters = state.selectedFilters,
                                onFilterToggle = onFilterToggle
                            )
                        }
                    }

                    item {
                        ResultsSummary(totalJobs = state.totalJobs)
                    }

                    if (state.jobs.isEmpty()) {
                        item { EmptyAttractiveJobsPlaceholder(onRetry = onRefresh) }
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
                        item { InlineAttractiveError(message = state.errorMessage, onRetry = onRefresh) }
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
private fun AttractiveJobsHeader(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(horizontal = PTITSpacing.lg, vertical = PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    text = "Việc làm hấp dẫn",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextLight
                    )
                )
                Spacer(modifier = Modifier.size(PTITSize.iconLg))
            }

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
private fun QuickFiltersSection(
    selectedFilters: Set<AttractiveQuickFilter>,
    onFilterToggle: (AttractiveQuickFilter) -> Unit
) {
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
                val isSelected = filter.filter in selectedFilters
                FilterChip(
                    onClick = { onFilterToggle(filter.filter) },
                    label = {
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    selected = isSelected,
                    leadingIcon = {
                        Icon(
                            imageVector = filter.icon,
                            contentDescription = null,
                            tint = if (isSelected) PTITPrimary else PTITTextLight
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        selectedContainerColor = PTITPrimary.copy(alpha = 0.2f),
                        labelColor = PTITTextLight,
                        selectedLabelColor = PTITTextLight
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected
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

@Composable
private fun AttractiveJobsLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PTITPrimary)
    }
}

@Composable
private fun AttractiveJobsErrorState(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
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
                    text = "Không thể tải việc làm hấp dẫn",
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
                    TextButton(onClick = onBack) { Text("Quay lại") }
                    ElevatedButton(onClick = onRetry) { Text("Thử lại") }
                }
            }
        }
    }
}

@Composable
private fun InlineAttractiveError(message: String, onRetry: () -> Unit) {
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
                        color = PTITPrimary
                    )
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(color = PTITTextSecondary)
                )
            }
            TextButton(onClick = onRetry) { Text("Thử lại") }
        }
    }
}

@Composable
private fun EmptyAttractiveJobsPlaceholder(onRetry: () -> Unit) {
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
                text = "Hãy thử điều chỉnh bộ lọc hoặc tìm kiếm với từ khóa khác.",
                style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) { Text("Làm mới") }
        }
    }
}

// Data classes for filters
data class QuickFilter(
    val filter: AttractiveQuickFilter,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private fun getQuickFilters(): List<QuickFilter> {
    return listOf(
        QuickFilter(AttractiveQuickFilter.HIGH_SALARY, "Lương cao", Icons.AutoMirrored.Filled.TrendingUp),
        QuickFilter(AttractiveQuickFilter.REMOTE, "Remote", Icons.Default.Home),
        QuickFilter(AttractiveQuickFilter.PART_TIME, "Part-time", Icons.Default.Schedule),
        QuickFilter(AttractiveQuickFilter.FULL_TIME, "Full-time", Icons.Default.Work),
        QuickFilter(AttractiveQuickFilter.STARTUP, "Startup", Icons.Default.Rocket),
        QuickFilter(AttractiveQuickFilter.TECHNOLOGY, "Công nghệ", Icons.Default.Computer),
        QuickFilter(AttractiveQuickFilter.MARKETING, "Marketing", Icons.Default.Campaign),
        QuickFilter(AttractiveQuickFilter.DESIGN, "Thiết kế", Icons.Default.Palette)
    )
}

@Preview(showBackground = true, device = "spec:width=393dp,height=851dp,dpi=420")
@Composable
fun AttractiveJobsScreenPreview() {
    MaterialTheme {
        AttractiveJobsScreen(
            state = sampleAttractiveJobsState(),
            onBack = {},
            onSearchChange = {},
            onLocationChange = {},
            onSearch = {},
            onRefresh = {},
            onPageChange = {},
            onFilterToggle = {},
            onJobSelected = {},
            onSave = {}
        )
    }
}

private fun sampleAttractiveJobsState(): AttractiveJobsUiState {
    val jobs = getSampleAttractiveJobs()
    return AttractiveJobsUiState(
        jobs = jobs,
        totalJobs = jobs.size,
        totalPages = 3,
        selectedFilters = setOf(AttractiveQuickFilter.HIGH_SALARY)
    )
}

private fun getSampleAttractiveJobs(): List<JobListCardData> {
    return listOf(
        JobListCardData(
            id = 1,
            backendId = "1",
            title = "Senior Frontend Developer - React/Next.js",
            company = "CÔNG TY CÔNG NGHỆ DIGITEQ",
            companyLogo = null,
            salary = "25 - 40 triệu",
            location = "Hà Nội, TP.HCM",
            experience = "3+ năm",
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
            experience = "3+ năm",
            deadline = "20 ngày",
            postedTime = "2 giờ trước",
            isUrgent = false,
            isVerified = true,
            tags = listOf("Thương hiệu lớn", "Marketing")
        ),
        JobListCardData(
            id = 3,
            backendId = "3",
            title = "DevOps Engineer - Startup Fintech",
            company = "MOMO E-WALLET",
            companyLogo = null,
            salary = "35 - 55 triệu",
            location = "TP.HCM",
            experience = "2+ năm",
            deadline = "25 ngày",
            postedTime = "30 phút trước",
            isUrgent = true,
            isVerified = true,
            tags = listOf("Fintech", "Startup", "DevOps")
        ),
        JobListCardData(
            id = 4,
            backendId = "4",
            title = "UI/UX Designer - App Mobile",
            company = "VIETCOMBANK",
            companyLogo = null,
            salary = "20 - 35 triệu",
            location = "Hà Nội",
            experience = "2+ năm",
            deadline = "10 ngày",
            postedTime = "45 phút trước",
            isUrgent = true,
            isVerified = false,
            tags = listOf("Design", "Mobile", "Banking")
        ),
        JobListCardData(
            id = 5,
            backendId = "5",
            title = "Data Scientist - AI/ML",
            company = "FPT SOFTWARE",
            companyLogo = null,
            salary = "40 - 60 triệu",
            location = "Đà Nẵng, TP.HCM",
            experience = "3+ năm",
            deadline = "30 ngày",
            postedTime = "3 giờ trước",
            isUrgent = false,
            isVerified = true,
            tags = listOf("AI/ML", "Data Science", "Remote")
        )
    )
}