package com.example.ptitjob.ui.screen.candidate.jobs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ptitjob.ui.component.CompanyItem
import com.example.ptitjob.ui.component.FeaturedCompany
import com.example.ptitjob.ui.component.IndustryItem
import com.example.ptitjob.ui.component.JobItem
import com.example.ptitjob.ui.component.JobListCard
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.component.RecommendedJob
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ptitjob.ui.component.PTITScreenContainer
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITError
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITInfo
import com.example.ptitjob.ui.theme.PTITNeutral200
import com.example.ptitjob.ui.theme.PTITNeutral50
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
fun CandidateJobListRoute(
    onJobSelected: (String) -> Unit,
    onCompanySelected: (String) -> Unit,
    onCategorySelected: (IndustryItem) -> Unit,
    viewModel: CandidateJobListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CandidateJobListScreen(
        state = state,
        onSearchChange = viewModel::updateSearchQuery,
        onSearch = viewModel::performSearch,
    onFilterChange = viewModel::selectFilter,
        onRefresh = viewModel::refreshAll,
        onJobSelected = { job -> onJobSelected(job.backendId) },
        onApply = { job -> onJobSelected(job.backendId) },
        onSave = { _ -> },
        onRecommendedSelected = { recommendation -> onJobSelected(recommendation.id) },
        onCompanySelected = { company -> onCompanySelected(company.id) },
        onFeaturedCompanySelected = { company -> onCompanySelected(company.id) },
        onCategorySelected = onCategorySelected
    )
}

@Composable
fun CandidateJobListScreen(
    state: CandidateJobListUiState,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFilterChange: (CandidateJobFilter) -> Unit,
    onRefresh: () -> Unit,
    onJobSelected: (JobListCardData) -> Unit,
    onApply: (JobListCardData) -> Unit,
    onSave: (JobListCardData) -> Unit,
    onRecommendedSelected: (RecommendedJob) -> Unit,
    onCompanySelected: (CompanyItem) -> Unit,
    onFeaturedCompanySelected: (FeaturedCompany) -> Unit,
    onCategorySelected: (IndustryItem) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Use PTITScreenContainer for consistent layout
    PTITScreenContainer(
        hasGradientBackground = true
    ) {
        when {
            state.isLoading && state.jobs.isEmpty() -> {
                CandidateJobsLoadingState()
            }
            state.errorMessage != null && state.jobs.isEmpty() -> {
                CandidateJobsErrorState(message = state.errorMessage, onRetry = onRefresh)
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
                            CandidateJobListHeader()
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                        ) {
                            SearchAndFiltersSection(
                                searchQuery = state.searchQuery,
                                onSearchChange = onSearchChange,
                                selectedFilter = state.selectedFilter,
                                filters = state.availableFilters,
                                onFilterChange = onFilterChange,
                                onSearch = onSearch
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
                        ) {
                            JobStatsOverview(
                                totalJobs = state.totalJobs,
                                newJobsToday = state.newJobsToday,
                                companiesHiring = state.companiesHiring
                            )
                        }
                    }

                    if (state.jobs.isNotEmpty()) {
                        item {
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn()
                            ) {
                                FeaturedJobsSection(
                                    jobs = state.jobs,
                                    onJobSelected = onJobSelected,
                                    onApply = onApply,
                                    onSave = onSave
                                )
                            }
                        }
                    }

                    if (state.recommendedJobs.isNotEmpty()) {
                        item {
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(initialOffsetY = { it / 5 }) + fadeIn()
                            ) {
                                JobRecommendationsModernSection(
                                    jobs = state.recommendedJobs,
                                    onJobSelected = onRecommendedSelected
                                )
                            }
                        }
                    }

                    if (state.companies.isNotEmpty()) {
                        item {
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(initialOffsetY = { it / 6 }) + fadeIn()
                            ) {
                                CompaniesModernSection(
                                    companies = state.companies,
                                    onCompanySelected = onCompanySelected
                                )
                            }
                        }
                    }

                    if (state.featuredCompanies.isNotEmpty()) {
                        item {
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(initialOffsetY = { it / 7 }) + fadeIn()
                            ) {
                                FeaturedEmployersModernSection(
                                    companies = state.featuredCompanies,
                                    onCompanySelected = onFeaturedCompanySelected
                                )
                            }
                        }
                    }

                    if (state.categories.isNotEmpty()) {
                        item {
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(initialOffsetY = { it / 8 }) + fadeIn()
                            ) {
                                IndustriesModernSection(
                                    categories = state.categories,
                                    onCategorySelected = onCategorySelected
                                )
                            }
                        }
                    }

                    if (state.errorMessage != null && state.jobs.isNotEmpty()) {
                        item {
                            CandidateJobsInlineError(message = state.errorMessage, onRetry = onRefresh)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CandidateJobListHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = PTITSpacing.xl,
                vertical = PTITSpacing.xxl
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Enhanced Header Icon with PTIT gradient
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier
                    .size(PTITSize.iconXxxl + PTITSpacing.xl)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 120f
                        ),
                        shape = CircleShape
                    )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = PTITPrimary.copy(alpha = 0.9f),
                        modifier = Modifier.size(PTITSize.iconXxxl)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.WorkOutline,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(PTITSize.iconXl)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(PTITSpacing.xl))
            
            Text(
                text = "Danh sách việc làm",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(PTITSpacing.md))
            
            Text(
                text = "Khám phá hàng nghìn cơ hội việc làm từ các công ty hàng đầu",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 28.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PTITSpacing.xl)
            )

            Spacer(Modifier.height(PTITSpacing.sm))
            
            // Stats row for quick info
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xl),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderStatChip(
                    icon = Icons.Default.Work,
                    text = "1000+",
                    label = "Việc làm"
                )
                HeaderStatChip(
                    icon = Icons.Default.Business,
                    text = "100+", 
                    label = "Công ty"
                )
                HeaderStatChip(
                    icon = Icons.Default.NewReleases,
                    text = "Mới",
                    label = "Cập nhật"
                )
            }
        }
    }
}

@Composable
private fun HeaderStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    label: String
) {
    Surface(
        shape = PTITCornerRadius.lg,
        color = Color.White.copy(alpha = 0.15f),
        modifier = Modifier.padding(vertical = PTITSpacing.xs)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = PTITSpacing.md,
                vertical = PTITSpacing.sm
            ),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFiltersSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: CandidateJobFilter,
    filters: List<CandidateJobFilter>,
    onFilterChange: (CandidateJobFilter) -> Unit,
    onSearch: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.xl,
        color = Color.White,
        shadowElevation = PTITElevation.xl,
        tonalElevation = PTITElevation.md
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PTITPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(PTITSize.iconXl)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Tìm kiếm việc làm",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                    Text(
                        text = "Khám phá cơ hội nghề nghiệp phù hợp",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                }
            }
            
            // Enhanced Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { 
                    Text(
                        "Nhập từ khóa: vị trí, công ty, kỹ năng...", 
                        color = PTITTextSecondary
                    ) 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = null,
                        tint = PTITPrimary,
                        modifier = Modifier.size(PTITSize.iconMd)
                    ) 
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = PTITTextSecondary.copy(alpha = 0.1f),
                            modifier = Modifier.size(PTITSize.iconLg)
                        ) {
                            IconButton(
                                onClick = { onSearchChange("") },
                                modifier = Modifier.size(PTITSize.iconLg)
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = PTITTextSecondary,
                                    modifier = Modifier.size(PTITSize.iconSm)
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = PTITCornerRadius.lg,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    unfocusedBorderColor = PTITNeutral200,
                    focusedContainerColor = PTITPrimary.copy(alpha = 0.02f),
                    unfocusedContainerColor = PTITNeutral50
                )
            )
            
            // Enhanced Filters with better spacing and design
            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                Text(
                    text = "Bộ lọc nhanh",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    contentPadding = PaddingValues(end = PTITSpacing.lg)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            onClick = { onFilterChange(filter) },
                            label = { 
                                Text(
                                    filter.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                ) 
                            },
                            selected = selectedFilter == filter,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = PTITNeutral50,
                                labelColor = PTITTextPrimary,
                                selectedContainerColor = PTITPrimary,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                selected = selectedFilter == filter,
                                enabled = true,
                                borderColor = PTITNeutral200,
                                selectedBorderColor = PTITPrimary,
                                borderWidth = 1.5.dp
                            ),
                            shape = PTITCornerRadius.lg
                        )
                    }
                }
            }

            // Enhanced Search Button with gradient effect
            Button(
                onClick = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITPrimary
                ),
                shape = PTITCornerRadius.lg,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = PTITElevation.md,
                    pressedElevation = PTITElevation.sm
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(PTITSize.iconMd),
                        tint = Color.White
                    )
                    Text(
                        "Tìm kiếm việc làm ngay",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun JobStatsOverview(
    totalJobs: Int,
    newJobsToday: Int,
    companiesHiring: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.xl,
        color = Color.White,
        shadowElevation = PTITElevation.lg,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PTITInfo.copy(alpha = 0.1f),
                    modifier = Modifier.size(PTITSize.iconXl)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = PTITInfo,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Thống kê việc làm",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                    Text(
                        text = "Cập nhật mới nhất",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                }
            }
            
            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EnhancedStatItem(
                    icon = Icons.Default.Work,
                    value = "$totalJobs+",
                    label = "Việc làm",
                    color = PTITPrimary,
                    modifier = Modifier.weight(1f)
                )
                EnhancedStatItem(
                    icon = Icons.Default.NewReleases,
                    value = "$newJobsToday",
                    label = "Mới hôm nay",
                    color = PTITSuccess,
                    modifier = Modifier.weight(1f)
                )
                EnhancedStatItem(
                    icon = Icons.Default.Business,
                    value = "$companiesHiring+",
                    label = "Công ty",
                    color = PTITInfo,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EnhancedStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(PTITSpacing.sm),
        shape = PTITCornerRadius.lg,
        color = color.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(PTITSize.avatarLg)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(PTITSize.iconLg)
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeaturedJobsSection(
    jobs: List<JobListCardData>,
    onJobSelected: (JobListCardData) -> Unit,
    onApply: (JobListCardData) -> Unit,
    onSave: (JobListCardData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        SectionHeader(
            title = "Việc làm nổi bật",
            subtitle = "Những cơ hội việc làm hấp dẫn nhất",
            icon = Icons.Default.Star
        )
        Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
            jobs.take(6).forEach { job ->
                JobListCard(
                    job = job,
                    onApply = onApply,
                    onSave = onSave,
                    onCardClick = onJobSelected
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(PTITSize.avatarLg)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(PTITSize.iconLg)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.size(PTITSize.iconXl)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                }
            }
        }
    }
}

@Composable
private fun JobCard(job: JobItem) {
    Surface(
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.sm,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
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
            Text(
                text = job.company,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PTITTextSecondary,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = job.location,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PTITTextSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = PTITSuccess,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = job.salary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PTITSuccess,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            
            // Job Tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
            ) {
                items(job.tags) { tag ->
                    Surface(
                        shape = PTITCornerRadius.sm,
                        color = PTITInfo.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PTITInfo,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(
                                horizontal = PTITSpacing.sm,
                                vertical = PTITSpacing.xs
                            )
                        )
                    }
                }
            }
        }
    }
}

// Modern sections to replace legacy components
@Composable
private fun JobRecommendationsModernSection(
    jobs: List<RecommendedJob>,
    onJobSelected: (RecommendedJob) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        SectionHeader(
            title = "Gợi ý việc làm",
            subtitle = "Phù hợp với hồ sơ của bạn",
            icon = Icons.Default.Recommend
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
            contentPadding = PaddingValues(end = PTITSpacing.lg)
        ) {
            items(jobs) { job ->
                RecommendedJobCard(job = job, onClick = onJobSelected)
            }
        }
    }
}

@Composable
private fun RecommendedJobCard(
    job: RecommendedJob,
    onClick: (RecommendedJob) -> Unit
) {
    Surface(
        shape = PTITCornerRadius.xl,
        color = Color.White,
        shadowElevation = PTITElevation.xl,
        tonalElevation = PTITElevation.md,
        modifier = Modifier
            .width(320.dp)
            .clickable { onClick(job) }
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Header with company info
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                Surface(
                    shape = PTITCornerRadius.lg,
                    color = PTITPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(PTITSize.avatarXl)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(PTITSize.iconXl)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary,
                            lineHeight = 24.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(PTITSpacing.xs))
                    Text(
                        text = job.companyName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = PTITSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = PTITWarning.copy(alpha = 0.1f),
                    modifier = Modifier.size(PTITSize.iconXl)
                ) {
                    IconButton(
                        onClick = { /* TODO: Save job */ },
                        modifier = Modifier.size(PTITSize.iconXl)
                    ) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = PTITWarning,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                    }
                }
            }
            
            // Job Description
            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextSecondary,
                    lineHeight = 22.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            // Job details with enhanced design
            Surface(
                shape = PTITCornerRadius.md,
                color = PTITNeutral50,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = PTITSuccess,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = job.salary,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PTITInfo,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = job.location,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            
            // Apply Button
            Button(
                onClick = { /* TODO: Apply */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITPrimary
                ),
                shape = PTITCornerRadius.lg,
                contentPadding = PaddingValues(
                    horizontal = PTITSpacing.xl,
                    vertical = PTITSpacing.lg
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = PTITElevation.md
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Ứng tuyển ngay",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                }
            }
        }
    }
}

@Composable
private fun CompaniesModernSection(
    companies: List<CompanyItem>,
    onCompanySelected: (CompanyItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        SectionHeader(
            title = "Công ty nổi bật",
            subtitle = "Những nhà tuyển dụng hàng đầu",
            icon = Icons.Default.Business
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md),
            modifier = Modifier.height(300.dp)
        ) {
            items(companies) { company ->
                CompanyCard(company = company, onCompanySelected = onCompanySelected)
            }
        }
    }
}

@Composable
private fun CompanyCard(
    company: CompanyItem,
    onCompanySelected: (CompanyItem) -> Unit
) {
    Surface(
        shape = PTITCornerRadius.xl,
        color = Color.White,
        shadowElevation = PTITElevation.lg,
        tonalElevation = PTITElevation.sm,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCompanySelected(company) }
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Company Logo with gradient background
            Surface(
                shape = PTITCornerRadius.lg,
                color = Color.Transparent,
                modifier = Modifier
                    .size(PTITSize.avatarXxl)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PTITPrimary.copy(alpha = 0.1f),
                                PTITSecondary.copy(alpha = 0.1f)
                            )
                        ),
                        shape = PTITCornerRadius.lg
                    )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = PTITPrimary,
                        modifier = Modifier.size(PTITSize.iconXxl)
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = company.category,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PTITSecondary,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
            
            // Enhanced job count badge
            Surface(
                shape = PTITCornerRadius.lg,
                color = PTITSuccess.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, PTITSuccess.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = PTITSpacing.lg,
                        vertical = PTITSpacing.md
                    ),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        tint = PTITSuccess,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                    Text(
                        text = "${company.jobs} việc làm",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = PTITSuccess,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedEmployersModernSection(
    companies: List<FeaturedCompany>,
    onCompanySelected: (FeaturedCompany) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        SectionHeader(
            title = "Nhà tuyển dụng tiêu biểu",
            subtitle = "Các công ty uy tín và chất lượng",
            icon = Icons.Default.StarBorder
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            companies.forEach { company ->
                FeaturedEmployerCard(company = company, onCompanySelected = onCompanySelected)
            }
        }
    }
}

@Composable
private fun FeaturedEmployerCard(
    company: FeaturedCompany,
    onCompanySelected: (FeaturedCompany) -> Unit
) {
    Surface(
        shape = PTITCornerRadius.xl,
        color = Color.White,
        shadowElevation = PTITElevation.xl,
        tonalElevation = PTITElevation.md,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCompanySelected(company) }
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg),
                verticalAlignment = Alignment.Top
            ) {
                // Enhanced company logo
                Surface(
                    shape = PTITCornerRadius.xl,
                    color = Color.Transparent,
                    modifier = Modifier
                        .size(PTITSize.avatarXxl)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    PTITPrimary.copy(alpha = 0.1f),
                                    PTITSecondary.copy(alpha = 0.1f)
                                )
                            ),
                            shape = PTITCornerRadius.xl
                        )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(PTITSize.iconXxl)
                        )
                    }
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PTITTextPrimary
                        )
                    )
                    Text(
                        text = company.companySize,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = PTITSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = company.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PTITTextSecondary,
                            lineHeight = 20.sp
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Surface(
                    shape = CircleShape,
                    color = PTITPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(PTITSize.iconXl)
                ) {
                    IconButton(
                        onClick = { onCompanySelected(company) }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View company",
                            tint = PTITPrimary,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                    }
                }
            }
            
            // Enhanced info badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = PTITCornerRadius.lg,
                    color = PTITSuccess.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, PTITSuccess.copy(alpha = 0.2f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(PTITSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Work,
                            contentDescription = null,
                            tint = PTITSuccess,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = "${company.jobCount} việc làm",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Surface(
                    shape = PTITCornerRadius.lg,
                    color = PTITInfo.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, PTITInfo.copy(alpha = 0.2f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(PTITSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PTITInfo,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = company.address,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITInfo,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IndustriesModernSection(
    categories: List<IndustryItem>,
    onCategorySelected: (IndustryItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        SectionHeader(
            title = "Ngành nghề phổ biến",
            subtitle = "Khám phá các lĩnh vực việc làm",
            icon = Icons.Default.Category
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md),
            modifier = Modifier.height(300.dp)
        ) {
            items(categories) { category ->
                IndustryCard(category = category, onCategorySelected = onCategorySelected)
            }
        }
    }
}

@Composable
private fun IndustryCard(
    category: IndustryItem,
    onCategorySelected: (IndustryItem) -> Unit
) {
    Surface(
        shape = PTITCornerRadius.xl,
        color = Color.White,
        shadowElevation = PTITElevation.lg,
        tonalElevation = PTITElevation.sm,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategorySelected(category) }
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Enhanced industry icon with gradient background
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier
                    .size(PTITSize.avatarXl + PTITSpacing.md)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                getIndustryColor(category.id).copy(alpha = 0.15f),
                                getIndustryColor(category.id).copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            radius = 60f
                        ),
                        shape = CircleShape
                    )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = getIndustryColor(category.id).copy(alpha = 0.1f),
                        modifier = Modifier.size(PTITSize.avatarXl)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                getIndustryIcon(category.id),
                                contentDescription = null,
                                tint = getIndustryColor(category.id),
                                modifier = Modifier.size(PTITSize.iconXl)
                            )
                        }
                    }
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Enhanced job count with background
                Surface(
                    shape = PTITCornerRadius.lg,
                    color = getIndustryColor(category.id).copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, getIndustryColor(category.id).copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = PTITSpacing.lg,
                            vertical = PTITSpacing.md
                        ),
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Work,
                            contentDescription = null,
                            tint = getIndustryColor(category.id),
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = "${category.jobs} việc làm",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = getIndustryColor(category.id),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

// Helper functions
private fun getIndustryColor(id: Int): Color {
    return when (id % 6) {
        0 -> PTITPrimary
        1 -> PTITSecondary
        2 -> PTITSuccess
        3 -> PTITInfo
        4 -> PTITWarning
        else -> PTITError
    }
}
@Composable
private fun CandidateJobsLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.xxl),
            shape = PTITCornerRadius.xl,
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = PTITElevation.xl
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PTITPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(PTITSize.iconXxxl + PTITSpacing.lg)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = PTITPrimary,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(PTITSize.iconXxxl)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Text(
                        text = "Đang tải dữ liệu",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                    Text(
                        text = "Vui lòng chờ trong giây lát...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PTITTextSecondary
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CandidateJobsErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.xxl),
            shape = PTITCornerRadius.xl,
            color = Color.White,
            shadowElevation = PTITElevation.xl
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PTITError.copy(alpha = 0.1f),
                    modifier = Modifier.size(PTITSize.iconXxxl + PTITSpacing.lg)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            tint = PTITError,
                            modifier = Modifier.size(PTITSize.iconXxxl)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Text(
                        text = "Không thể tải dữ liệu",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PTITTextSecondary,
                            lineHeight = 22.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary),
                    shape = PTITCornerRadius.lg,
                    contentPadding = PaddingValues(vertical = PTITSpacing.lg),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = PTITElevation.md
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            "Thử lại",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CandidateJobsInlineError(message: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.md,
        border = BorderStroke(1.dp, PTITError.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = PTITError.copy(alpha = 0.1f),
                modifier = Modifier.size(PTITSize.iconXl)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = PTITError,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
            ) {
                Text(
                    text = "Không thể làm mới dữ liệu",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITError
                    )
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary
                    )
                )
            }

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITError
                ),
                shape = PTITCornerRadius.md,
                contentPadding = PaddingValues(
                    horizontal = PTITSpacing.lg,
                    vertical = PTITSpacing.md
                )
            ) {
                Text(
                    "Thử lại",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

private fun getIndustryIcon(id: Int): androidx.compose.ui.graphics.vector.ImageVector {
    return when (id % 6) {
        0 -> Icons.Default.Computer
        1 -> Icons.Default.Campaign
        2 -> Icons.AutoMirrored.Filled.TrendingUp
        3 -> Icons.Default.Palette
        4 -> Icons.Default.People
        else -> Icons.Default.AccountBalance
    }
}
