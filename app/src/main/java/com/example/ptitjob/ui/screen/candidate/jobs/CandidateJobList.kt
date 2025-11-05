package com.example.ptitjob.ui.screen.candidate.jobs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                    startY = 0f,
                    endY = 800f
                )
            )
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
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Icon
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(PTITSize.avatarXl)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.WorkOutline,
                        contentDescription = null,
                        tint = PTITTextLight,
                        modifier = Modifier.size(PTITSize.iconXl)
                    )
                }
            }

            Spacer(Modifier.height(PTITSpacing.lg))
            
            Text(
                text = "💼 Danh sách việc làm",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextLight,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(PTITSpacing.sm))
            
            Text(
                text = "Khám phá hàng nghìn cơ hội việc làm từ các công ty hàng đầu",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = PTITTextLight.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PTITSpacing.lg)
            )
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
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.lg
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { 
                    Text(
                        "Tìm kiếm việc làm, công ty...", 
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
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = PTITTextSecondary
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.md,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    unfocusedBorderColor = PTITNeutral200
                )
            )
            
            // Filters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
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
                            selected = true,
                            enabled = true,
                            borderColor = PTITNeutral200,
                            selectedBorderColor = PTITPrimary
                        )
                    )
                }
            }

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
                    "Tìm kiếm việc làm",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
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
        shape = PTITCornerRadius.lg,
        color = Color.White.copy(alpha = 0.9f),
        tonalElevation = PTITElevation.md
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Work,
                value = "$totalJobs+",
                label = "Việc làm",
                color = PTITPrimary
            )
            StatItem(
                icon = Icons.Default.NewReleases,
                value = "$newJobsToday",
                label = "Mới hôm nay",
                color = PTITSuccess
            )
            StatItem(
                icon = Icons.Default.Business,
                value = "$companiesHiring+",
                label = "Công ty",
                color = PTITInfo
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(PTITSize.avatarMd)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextPrimary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = PTITTextSecondary
            )
        )
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        Surface(
            shape = CircleShape,
            color = PTITPrimary.copy(alpha = 0.1f),
            modifier = Modifier.size(PTITSize.avatarMd)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PTITPrimary,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
            }
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextLight
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextLight.copy(alpha = 0.8f)
                )
            )
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
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.md,
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick(job) }
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    shape = PTITCornerRadius.md,
                    color = PTITNeutral50,
                    modifier = Modifier.size(PTITSize.avatarLg)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            tint = PTITTextSecondary,
                            modifier = Modifier.size(PTITSize.iconLg)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
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
                        text = job.companyName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                IconButton(
                    onClick = { /* TODO: Save job */ },
                    modifier = Modifier.size(PTITSize.iconLg)
                ) {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = PTITTextSecondary
                    )
                }
            }
            
            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = job.salary,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITSuccess
                        )
                    )
                    Text(
                        text = job.location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PTITTextSecondary
                        )
                    )
                }
                
                Button(
                    onClick = { /* TODO: Apply */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PTITPrimary
                    ),
                    shape = PTITCornerRadius.md,
                    contentPadding = PaddingValues(
                        horizontal = PTITSpacing.lg,
                        vertical = PTITSpacing.sm
                    )
                ) {
                    Text(
                        "Ứng tuyển",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
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
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.sm,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCompanySelected(company) }
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Surface(
                shape = PTITCornerRadius.md,
                color = PTITNeutral50,
                modifier = Modifier.size(PTITSize.avatarLg)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = PTITTextSecondary,
                        modifier = Modifier.size(PTITSize.iconLg)
                    )
                }
            }
            
            Text(
                text = company.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = company.category,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITSecondary
                ),
                textAlign = TextAlign.Center
            )
            
            Surface(
                shape = PTITCornerRadius.sm,
                color = PTITInfo.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "${company.jobs} việc làm",
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
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.md,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCompanySelected(company) }
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = PTITCornerRadius.lg,
                color = PTITNeutral50,
                modifier = Modifier.size(PTITSize.avatarXl)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = PTITTextSecondary,
                        modifier = Modifier.size(PTITSize.iconXl)
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
            ) {
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
                Text(
                    text = company.companySize,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITSecondary
                    )
                )
                Text(
                    text = company.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Surface(
                        shape = PTITCornerRadius.sm,
                        color = PTITSuccess.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${company.jobCount} việc làm",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PTITSuccess,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(
                                horizontal = PTITSpacing.sm,
                                vertical = PTITSpacing.xs
                            )
                        )
                    }
                    Surface(
                        shape = PTITCornerRadius.sm,
                        color = PTITInfo.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = company.address,
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
            
            IconButton(
                onClick = { /* TODO: View company */ }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View company",
                    tint = PTITPrimary
                )
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
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.sm,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategorySelected(category) }
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Surface(
                shape = CircleShape,
                color = getIndustryColor(category.id).copy(alpha = 0.1f),
                modifier = Modifier.size(PTITSize.avatarLg)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        getIndustryIcon(category.id),
                        contentDescription = null,
                        tint = getIndustryColor(category.id),
                        modifier = Modifier.size(PTITSize.iconLg)
                    )
                }
            }
            
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "${category.jobs} việc làm",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary
                )
            )
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
                CircularProgressIndicator(color = PTITPrimary)
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
                        .padding(PTITSpacing.xl),
                    shape = PTITCornerRadius.lg,
                    color = Color.White,
                    shadowElevation = PTITElevation.lg
                ) {
                    Column(
                        modifier = Modifier.padding(PTITSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                    ) {
                        Text(
                            text = "Không thể tải dữ liệu",
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
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary),
                            shape = PTITCornerRadius.md
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(PTITSpacing.sm))
                            Text("Thử lại", fontWeight = FontWeight.SemiBold)
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
                shape = PTITCornerRadius.md,
                color = Color.White,
                shadowElevation = PTITElevation.sm
            ) {
                Row(
                    modifier = Modifier.padding(PTITSpacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Không thể làm mới dữ liệu",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITError
                            )
                        )
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall.copy(color = PTITTextSecondary)
                        )
                    }
                    TextButton(onClick = onRetry) {
                        Text("Thử lại", fontWeight = FontWeight.SemiBold)
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
@Preview(showBackground = true, device = "spec:width=393dp,height=851dp,dpi=420")
@Composable
fun CandidateJobListScreenPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
                    CandidateJobListScreen(
                        state = CandidateJobListUiState(
                            jobs = getSampleJobCardsForPreview(),
                            recommendedJobs = getSampleRecommendedJobsForPreview(),
                            companies = getSampleCompaniesForPreview(),
                            featuredCompanies = getSampleFeaturedCompaniesForPreview(),
                            categories = getSampleCategoriesForPreview(),
                            totalJobs = 1240,
                            newJobsToday = 32,
                            companiesHiring = 85
                        ),
                        onSearchChange = {},
                        onSearch = {},
                        onFilterChange = {},
                        onRefresh = {},
                        onJobSelected = {},
                        onApply = {},
                        onSave = {},
                        onRecommendedSelected = {},
                        onCompanySelected = {},
                        onFeaturedCompanySelected = {},
                        onCategorySelected = {}
                    )
        }
    }
}

        private fun getSampleJobCardsForPreview(): List<JobListCardData> {
    return List(6) { i ->
                JobListCardData(
                    id = i,
                    backendId = "job_$i",
                    title = "Lập trình viên Senior Android $i",
                    company = "Tech Solutions Inc.",
                    companyLogo = "https://example.logo/tech.png",
                    salary = "25 - 40 triệu",
                    location = "Quận 1, TP.HCM",
                    experience = "3+ năm",
                    postedTime = "Đăng ${i + 1} ngày trước",
                    deadline = "${5 - (i % 3)} ngày",
                    isUrgent = i % 2 == 0,
                    isVerified = i % 3 == 0,
                    tags = listOf("Android", "Kotlin", "Senior")
        )
    }
}

private fun getSampleRecommendedJobsForPreview(): List<RecommendedJob> {
    return List(4) { i ->
        RecommendedJob(
            id = "rec_$i",
            title = "Data Analyst (Fresher) $i",
            companyName = "Data Insights",
            logoUrl = "https://example.logo/data.png",
            salary = "15 - 20 triệu",
            location = "Đà Nẵng",
            jobType = "Full-time",
            description = "Phân tích dữ liệu, xây dựng báo cáo và dashboard để hỗ trợ các quyết định kinh doanh cho công ty.",
            dateRange = "01/11/2025 - 30/11/2025"
        )
    }
}

private fun getSampleCompaniesForPreview(): List<CompanyItem> {
    return List(8) { i ->
        CompanyItem(
            id = "comp_$i",
            name = "Innovatech Global $i",
            category = "Công nghệ cao",
            jobs = 15 + i,
            logo = "https://example.logo/innovate.png",
            location = "Khu Công nghệ cao Hòa Lạc, Hà Nội",
            region = "Miền Bắc",
            uniqueKey = "key_comp_$i"
        )
    }
}

private fun getSampleFeaturedCompaniesForPreview(): List<FeaturedCompany> {
    return listOf(
        FeaturedCompany(
            id = "feat_1",
            name = "FPT Software",
            logoUrl = "logo_url",
            companySize = "1000+ nhân viên",
            description = "Là công ty phần mềm hàng đầu Việt Nam, cung cấp dịch vụ cho khách hàng toàn cầu với các giải pháp công nghệ tiên tiến.",
            jobCount = 150,
            address = "Hà Nội",
            website = "https://fpt-software.com"
        ),
        FeaturedCompany(
            id = "feat_2",
            name = "VNG Corporation",
            logoUrl = "logo_url",
            companySize = "500-1000 nhân viên",
            description = "Kỳ lân công nghệ của Việt Nam với các sản phẩm Zalo, ZaloPay, và game được hàng triệu người sử dụng.",
            jobCount = 85,
            address = "TP.HCM",
            website = "https://vng.com.vn"
        ),
        FeaturedCompany(
            id = "feat_3",
            name = "Viettel Group",
            logoUrl = "logo_url",
            companySize = "5000+ nhân viên",
            description = "Tập đoàn Viễn thông và Công nghệ Quân đội, hoạt động đa quốc gia với mạng lưới rộng khắp.",
            jobCount = 210,
            address = "Toàn quốc",
            website = "https://viettel.com.vn"
        )
    )
}

private fun getSampleCategoriesForPreview(): List<IndustryItem> {
    return listOf(
        IndustryItem(1, "Công nghệ thông tin", 1250, null),
        IndustryItem(2, "Marketing & Truyền thông", 830, null),
        IndustryItem(3, "Kinh doanh & Bán hàng", 940, null),
        IndustryItem(4, "Thiết kế & Sáng tạo", 450, null),
        IndustryItem(5, "Nhân sự & Tuyển dụng", 320, null),
        IndustryItem(6, "Tài chính & Ngân hàng", 670, null),
    )
}