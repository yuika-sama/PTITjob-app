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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ptitjob.ui.navigation.DashboardSearchPayload
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITError
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITInfo
import com.example.ptitjob.ui.theme.PTITNeutral100
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

/* ---------------------- DATA MODELS (UI-only) ---------------------- */

data class UiCompany(
    val id: Int,
    val backendId: String,
    val name: String,
    val logo: String,
    val industry: String,
    val size: String? = null,
    val address: String? = null,
    val jobCount: Int? = null
)

enum class UiJobTag {
    REACT, VUE, JAVA, SPRING, SQL, PYTHON, FIGMA, KUBERNETES, AWS, CI_CD, MARKETING, ANALYTICS
}

data class UiJob(
    val id: Int,
    val backendId: String,
    val title: String,
    val company: UiCompany,
    val salary: String,
    val location: String,
    val experience: String,
    val deadline: String,
    val tags: List<UiJobTag>? = null,
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

/* ---------------------- SCREEN ---------------------- */

@Composable
fun JobSearchRoute(
    presetPayload: DashboardSearchPayload?,
    onBack: () -> Unit,
    onNavigateToJob: (String) -> Unit,
    onNavigateToCompany: (String) -> Unit,
    onApplyToJob: (String) -> Unit = onNavigateToJob,
    onSaveJobRequest: (String) -> Unit = {},
    viewModel: JobSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(presetPayload) {
        viewModel.applyPayload(presetPayload)
    }

    JobSearchScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onSearch = { viewModel.performSearch(page = 1) },
        onQuickSuggestion = viewModel::applyQuickSuggestion,
        onRemoveFilter = viewModel::removeFilter,
        onClearFilters = viewModel::clearFilters,
        onPageChange = viewModel::changePage,
        onRetry = viewModel::retryLastSearch,
        onBack = onBack,
        onJobSelected = { job -> onNavigateToJob(job.backendId) },
        onApplyJob = { job -> onApplyToJob(job.backendId) },
        onSaveJob = { job -> onSaveJobRequest(job.backendId) },
        onCompanySelected = { company -> onNavigateToCompany(company.backendId) }
    )
}

@Composable
fun JobSearchScreen(
    uiState: JobSearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onQuickSuggestion: (String) -> Unit,
    onRemoveFilter: (String) -> Unit,
    onClearFilters: () -> Unit,
    onPageChange: (Int) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onJobSelected: (UiJob) -> Unit,
    onApplyJob: (UiJob) -> Unit,
    onSaveJob: (UiJob) -> Unit,
    onCompanySelected: (UiCompany) -> Unit
) {
    var showFilters by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) { isVisible = true }

    // Use PTITScreenContainer for consistent layout and background
    com.example.ptitjob.ui.component.PTITScreenContainer(
        hasGradientBackground = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Header
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
            ) {
                JobSearchHeader(onBack = onBack)
            }

            // Advanced Search
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
            ) {
                AdvancedSearchSection(
                    searchQuery = uiState.searchQuery,
                    onSearchChange = onSearchQueryChange,
                    onSearch = onSearch
                )
            }

            // Quick Suggestions
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
            ) {
                QuickSearchSuggestions(onSuggestionSelected = onQuickSuggestion)
            }

            // Filters
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn()
            ) {
                FiltersSection(
                    showFilters = showFilters,
                    onToggleFilters = { showFilters = !showFilters },
                    currentFilters = uiState.activeFilters,
                    onRemoveFilter = onRemoveFilter,
                    onClearAll = onClearFilters
                )
            }

            // Results
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it / 5 }) + fadeIn()
            ) {
                SearchResultsSection(
                    isLoading = uiState.isLoading,
                    error = uiState.errorMessage,
                    jobs = uiState.jobs,
                    displayedCount = uiState.jobs.size,
                    totalJobs = uiState.totalJobs,
                    currentPage = uiState.currentPage,
                    totalPages = uiState.totalPages,
                    onPageChange = onPageChange,
                    onRetry = onRetry,
                    onClearFilters = onClearFilters,
                    onJobSelected = onJobSelected,
                    onApplyJob = onApplyJob,
                    onSaveJob = onSaveJob,
                    onCompanySelected = onCompanySelected
                )
            }
        }
    }
}

@Composable
private fun JobSearchHeader(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = PTITSpacing.lg,
                vertical = PTITSpacing.xl
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button with improved styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = PTITTextLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(PTITSpacing.lg))

            // Enhanced logo with gradient background
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier.size(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PTITPrimary.copy(alpha = 0.3f),
                                    PTITSecondary.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = PTITTextLight,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(Modifier.height(PTITSpacing.lg))

            // Improved title with responsive text size
            Text(
                text = "Tìm kiếm việc làm",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = PTITTextLight,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(PTITSpacing.sm))

            // Enhanced subtitle with better line height
            Text(
                text = "Khám phá hàng nghìn cơ hội việc làm phù hợp với kỹ năng và kinh nghiệm của bạn",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextLight.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PTITSpacing.md)
            )

            Spacer(Modifier.height(PTITSpacing.xl))

            // Responsive stats cards
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchStatCard(
                    title = "10K+",
                    subtitle = "Việc làm",
                    icon = Icons.Default.Work,
                    modifier = Modifier.weight(1f)
                )
                SearchStatCard(
                    title = "500+",
                    subtitle = "Công ty",
                    icon = Icons.Default.Business,
                    modifier = Modifier.weight(1f)
                )
                SearchStatCard(
                    title = "95%",
                    subtitle = "Hài lòng",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SearchStatCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = PTITCornerRadius.xl,
        color = Color.White.copy(alpha = 0.18f),
        tonalElevation = PTITElevation.sm,
        shadowElevation = PTITElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = PTITSpacing.md,
                vertical = PTITSpacing.lg
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            // Enhanced icon with gradient background
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PTITPrimary.copy(alpha = 0.2f),
                                    PTITSecondary.copy(alpha = 0.15f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PTITTextLight,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Responsive title with better typography
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextLight,
                    fontSize = 20.sp
                )
            )
            
            // Enhanced subtitle
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextLight.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedSearchSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit
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
            // Enhanced header with gradient accent
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PTITPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "Tìm kiếm nâng cao",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PTITTextPrimary
                    )
                )
            }

            // Enhanced search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        "Nhập từ khóa: tên công việc, công ty, kỹ năng...",
                        color = PTITTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        tint = PTITPrimary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Surface(
                            onClick = { onSearchChange("") },
                            shape = CircleShape,
                            color = PTITError.copy(alpha = 0.1f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Xóa",
                                    tint = PTITError,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.lg,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    unfocusedBorderColor = PTITNeutral200,
                    focusedContainerColor = PTITPrimary.copy(alpha = 0.02f),
                    unfocusedContainerColor = PTITNeutral50
                )
            )

            // Enhanced filter buttons with responsive layout
            Column(
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EnhancedFilterButton(
                        icon = Icons.Default.LocationOn,
                        text = "Địa điểm",
                        color = PTITSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = { /* TODO: Location filter */ }
                    )
                    EnhancedFilterButton(
                        icon = Icons.Default.AttachMoney,
                        text = "Mức lương",
                        color = PTITSuccess,
                        modifier = Modifier.weight(1f),
                        onClick = { /* TODO: Salary filter */ }
                    )
                }
                
                EnhancedFilterButton(
                    icon = Icons.Default.WorkHistory,
                    text = "Kinh nghiệm",
                    color = PTITInfo,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* TODO: Experience filter */ }
                )
            }

            // Enhanced search button with gradient
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
                    pressedElevation = PTITElevation.lg
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Tìm kiếm việc làm",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedFilterButton(
    icon: ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
            containerColor = color.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.3f)),
        shape = PTITCornerRadius.lg
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun QuickSearchSuggestions(
    onSuggestionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // Enhanced section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Surface(
                shape = CircleShape,
                color = PTITWarning.copy(alpha = 0.2f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "🔥",
                        fontSize = 16.sp
                    )
                }
            }
            Text(
                text = "Tìm kiếm phổ biến",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextLight
                )
            )
        }

        // Enhanced suggestions with better styling
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
            contentPadding = PaddingValues(end = PTITSpacing.lg)
        ) {
            items(getQuickSearchSuggestions()) { suggestion ->
                Surface(
                    onClick = { onSuggestionSelected(suggestion.text) },
                    shape = PTITCornerRadius.lg,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = PTITElevation.sm,
                    tonalElevation = PTITElevation.xs
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = PTITSpacing.md,
                            vertical = PTITSpacing.sm
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = suggestion.color.copy(alpha = 0.1f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    suggestion.icon,
                                    contentDescription = null,
                                    tint = suggestion.color,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            suggestion.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = PTITTextPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FiltersSection(
    showFilters: Boolean,
    onToggleFilters: () -> Unit,
    currentFilters: List<String>,
    onRemoveFilter: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.xl,
        color = Color.White,
        shadowElevation = PTITElevation.md,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Enhanced header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PTITPrimary.copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = null,
                                tint = PTITPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Bộ lọc tìm kiếm",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PTITTextPrimary
                        )
                    )
                    if (currentFilters.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = PTITPrimary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentFilters.size.toString(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentFilters.isNotEmpty()) {
                        Surface(
                            onClick = onClearAll,
                            shape = PTITCornerRadius.md,
                            color = PTITError.copy(alpha = 0.1f)
                        ) {
                            Text(
                                "Xóa tất cả",
                                color = PTITError,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(
                                    horizontal = PTITSpacing.md,
                                    vertical = PTITSpacing.sm
                                )
                            )
                        }
                    }

                    Surface(
                        onClick = onToggleFilters,
                        shape = CircleShape,
                        color = PTITPrimary.copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (showFilters) "Ẩn bộ lọc" else "Hiện bộ lọc",
                                tint = PTITPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Enhanced applied filters
            if (currentFilters.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    currentFilters.forEach { filter ->
                        Surface(
                            onClick = { onRemoveFilter(filter) },
                            shape = PTITCornerRadius.lg,
                            color = PTITPrimary.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, PTITPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = PTITSpacing.md,
                                    vertical = PTITSpacing.sm
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                            ) {
                                Text(
                                    filter,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = PTITPrimary
                                    )
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = PTITPrimary,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Bỏ lọc $filter",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Enhanced filter options
            AnimatedVisibility(visible = showFilters) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)
                ) {
                    EnhancedFilterCategory(
                        title = "Mức lương",
                        icon = Icons.Default.AttachMoney,
                        color = PTITSuccess,
                        options = listOf("Dưới 10 triệu", "10-15 triệu", "15-25 triệu", "25-40 triệu", "Trên 40 triệu"),
                        selectedOptions = currentFilters.filter { it.contains("triệu") },
                        onOptionToggle = { /* TODO connect state */ }
                    )

                    EnhancedFilterCategory(
                        title = "Kinh nghiệm",
                        icon = Icons.Default.WorkHistory,
                        color = PTITInfo,
                        options = listOf("Fresher", "1-2 năm", "2-5 năm", "5+ năm"),
                        selectedOptions = currentFilters.filter { it.contains("năm") || it == "Fresher" },
                        onOptionToggle = { /* TODO connect state */ }
                    )

                    EnhancedFilterCategory(
                        title = "Hình thức làm việc",
                        icon = Icons.Default.Work,
                        color = PTITSecondary,
                        options = listOf("Full-time", "Part-time", "Remote", "Freelance"),
                        selectedOptions = currentFilters.filter { it in listOf("Full-time", "Part-time", "Remote", "Freelance") },
                        onOptionToggle = { /* TODO connect state */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterCategory(
    title: String,
    icon: ImageVector,
    options: List<String>,
    selectedOptions: List<String>,
    onOptionToggle: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PTITSecondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PTITTextPrimary
                )
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            options.forEach { option ->
                FilterChip(
                    onClick = { onOptionToggle(option) },
                    label = {
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    },
                    selected = option in selectedOptions,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = PTITNeutral50,
                        labelColor = PTITTextPrimary,
                        selectedContainerColor = PTITPrimary,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false,
                        borderColor = PTITNeutral200,
                        selectedBorderColor = PTITPrimary
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EnhancedFilterCategory(
    title: String,
    icon: ImageVector,
    color: Color,
    options: List<String>,
    selectedOptions: List<String>,
    onOptionToggle: (String) -> Unit
) {
    Surface(
        shape = PTITCornerRadius.lg,
        color = color.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.1f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                options.forEach { option ->
                    val isSelected = option in selectedOptions
                    Surface(
                        onClick = { onOptionToggle(option) },
                        shape = PTITCornerRadius.lg,
                        color = if (isSelected) color else Color.White,
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = if (isSelected) color else PTITNeutral200
                        )
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else PTITTextPrimary
                            ),
                            modifier = Modifier.padding(
                                horizontal = PTITSpacing.md,
                                vertical = PTITSpacing.sm
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsSection(
    isLoading: Boolean,
    error: String?,
    jobs: List<UiJob>,
    displayedCount: Int,
    totalJobs: Int,
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    onRetry: () -> Unit,
    onClearFilters: () -> Unit,
    onJobSelected: (UiJob) -> Unit,
    onApplyJob: (UiJob) -> Unit,
    onSaveJob: (UiJob) -> Unit,
    onCompanySelected: (UiCompany) -> Unit
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kết quả tìm kiếm",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                    Text(
                        text = "Hiển thị $displayedCount trong tổng $totalJobs việc làm",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Sắp xếp:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                    TextButton(
                        onClick = { /* TODO: Sort options */ },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = PTITPrimary
                        )
                    ) {
                        Text("Mới nhất", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(PTITSpacing.xs))
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                    }
                }
            }

            // Body
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                        ) {
                            CircularProgressIndicator(color = PTITPrimary)
                            Text(
                                "Đang tìm kiếm việc làm...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PTITTextSecondary
                                )
                            )
                        }
                    }
                }

                error != null -> {
                    ErrorState(message = error, onRetry = onRetry)
                }

                jobs.isEmpty() -> {
                    EmptySearchState(onClearFilters = onClearFilters)
                }

                else -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                    ) {
                        jobs.forEach { job ->
                            ModernJobCard(
                                job = job,
                                onViewDetail = onJobSelected,
                                onApply = onApplyJob,
                                onSave = onSaveJob,
                                onCompanySelected = onCompanySelected
                            )
                        }

                        if (totalPages > 1) {
                            ModernPaginationControls(
                                currentPage = currentPage,
                                totalPages = totalPages,
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
private fun ModernJobCard(
    job: UiJob,
    onViewDetail: (UiJob) -> Unit,
    onApply: (UiJob) -> Unit,
    onSave: (UiJob) -> Unit,
    onCompanySelected: (UiCompany) -> Unit
) {
    Surface(
        shape = PTITCornerRadius.xl,
        color = PTITNeutral50,
        tonalElevation = PTITElevation.sm,
        shadowElevation = PTITElevation.md,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail(job) }
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Enhanced header with company info
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                verticalAlignment = Alignment.Top
            ) {
                // Enhanced company logo
                Surface(
                    shape = PTITCornerRadius.lg,
                    color = Color.White,
                    shadowElevation = PTITElevation.xs,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        PTITPrimary.copy(alpha = 0.1f),
                                        PTITSecondary.copy(alpha = 0.05f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Enhanced job info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
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

                    Surface(
                        onClick = { onCompanySelected(job.company) },
                        shape = PTITCornerRadius.sm,
                        color = PTITSecondary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = job.company.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PTITSecondary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(
                                horizontal = PTITSpacing.sm,
                                vertical = PTITSpacing.xs
                            )
                        )
                    }

                    // Enhanced job details with better icons
                    Column(
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                        ) {
                            EnhancedJobInfoItem(
                                icon = Icons.Default.AttachMoney,
                                text = job.salary,
                                color = PTITSuccess
                            )
                            EnhancedJobInfoItem(
                                icon = Icons.Default.LocationOn,
                                text = job.location,
                                color = PTITInfo
                            )
                        }
                        EnhancedJobInfoItem(
                            icon = Icons.Default.AccessTime,
                            text = job.experience,
                            color = PTITWarning
                        )
                    }
                }

//                // Enhanced save button
//                Surface(
//                    onClick = { onSave(job) },
//                    shape = CircleShape,
//                    color = PTITNeutral100,
//                    modifier = Modifier.size(40.dp)
//                ) {
//                    Box(contentAlignment = Alignment.Center) {
//                        Icon(
//                            Icons.Default.BookmarkBorder,
//                            contentDescription = "Lưu việc làm",
//                            tint = PTITTextSecondary,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }
            }

            // Enhanced tags section
            if (!job.tags.isNullOrEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    items(job.tags.take(4)) { tag ->
                        Surface(
                            shape = PTITCornerRadius.md,
                            color = getTagColor(tag).copy(alpha = 0.1f),
                            border = BorderStroke(
                                width = 1.dp,
                                color = getTagColor(tag).copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = tag.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = getTagColor(tag),
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(
                                    horizontal = PTITSpacing.md,
                                    vertical = PTITSpacing.sm
                                )
                            )
                        }
                    }
                    if (job.tags.size > 4) {
                        item {
                            Surface(
                                shape = PTITCornerRadius.md,
                                color = PTITNeutral100
                            ) {
                                Text(
                                    text = "+${job.tags.size - 4}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PTITTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(
                                        horizontal = PTITSpacing.md,
                                        vertical = PTITSpacing.sm
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Enhanced action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onApply(job) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PTITPrimary
                    ),
                    shape = PTITCornerRadius.md,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = PTITElevation.sm,
                        pressedElevation = PTITElevation.md
                    )
                ) {
                    Text(
                        "Ứng tuyển",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                OutlinedButton(
                    onClick = { onViewDetail(job) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PTITPrimary,
                        containerColor = PTITPrimary.copy(alpha = 0.05f)
                    ),
                    border = BorderStroke(1.5.dp, PTITPrimary),
                    shape = PTITCornerRadius.md
                ) {
                    Text(
                        "Chi tiết",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedJobInfoItem(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = PTITTextSecondary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun JobInfoItem(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = PTITTextSecondary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ModernPaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Surface(
        shape = PTITCornerRadius.xl,
        color = PTITNeutral50,
        tonalElevation = PTITElevation.xs,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced previous button
            Surface(
                onClick = { onPageChange(currentPage - 1) },
                enabled = currentPage > 1,
                shape = CircleShape,
                color = if (currentPage > 1) PTITPrimary.copy(alpha = 0.1f) else PTITNeutral100,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Trang trước",
                        tint = if (currentPage > 1) PTITPrimary else PTITTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.width(PTITSpacing.xl))

            // Enhanced page indicator
            Surface(
                shape = PTITCornerRadius.lg,
                color = PTITPrimary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "Trang $currentPage / $totalPages",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITPrimary
                    ),
                    modifier = Modifier.padding(
                        horizontal = PTITSpacing.lg,
                        vertical = PTITSpacing.md
                    )
                )
            }

            Spacer(Modifier.width(PTITSpacing.xl))

            // Enhanced next button
            Surface(
                onClick = { onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages,
                shape = CircleShape,
                color = if (currentPage < totalPages) PTITPrimary.copy(alpha = 0.1f) else PTITNeutral100,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Trang sau",
                        tint = if (currentPage < totalPages) PTITPrimary else PTITTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState(onClearFilters: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PTITSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        // Enhanced empty state icon
        Surface(
            shape = CircleShape,
            color = PTITNeutral100,
            modifier = Modifier.size(120.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PTITInfo.copy(alpha = 0.1f),
                                PTITNeutral100
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = PTITTextSecondary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Text(
            text = "Không tìm thấy việc làm phù hợp 😕",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Surface(
            shape = PTITCornerRadius.lg,
            color = PTITNeutral50
        ) {
            Text(
                text = "Đừng lo lắng! Hãy thử:\n\n• Thay đổi từ khóa tìm kiếm\n• Xóa bớt bộ lọc\n• Mở rộng địa điểm tìm kiếm\n• Thử với từ khóa khác",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextSecondary,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(PTITSpacing.lg)
            )
        }

        Button(
            onClick = onClearFilters,
            colors = ButtonDefaults.buttonColors(
                containerColor = PTITPrimary
            ),
            shape = PTITCornerRadius.lg,
            modifier = Modifier.height(48.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = PTITElevation.sm
            )
        ) {
            Icon(
                Icons.Default.Clear,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(PTITSpacing.sm))
            Text(
                "Xóa bộ lọc",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PTITSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        // Enhanced error icon
        Surface(
            shape = CircleShape,
            color = PTITError.copy(alpha = 0.1f),
            modifier = Modifier.size(120.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PTITError.copy(alpha = 0.2f),
                                PTITError.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = PTITError,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Text(
            text = "Oops! Có lỗi xảy ra 😓",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Surface(
            shape = PTITCornerRadius.lg,
            color = PTITError.copy(alpha = 0.05f)
        ) {
            Text(
                text = "$message\n\nVui lòng thử lại sau ít phút nhé!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextSecondary,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(PTITSpacing.lg)
            )
        }

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = PTITError
            ),
            shape = PTITCornerRadius.lg,
            modifier = Modifier.height(48.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = PTITElevation.sm
            )
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(PTITSpacing.sm))
            Text(
                "Thử lại",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ---------------------- Quick Suggestions ---------------------- */

data class SearchSuggestion(
    val text: String,
    val icon: ImageVector,
    val color: Color
)

private fun getQuickSearchSuggestions(): List<SearchSuggestion> = listOf(
    SearchSuggestion("Frontend Developer", Icons.Default.Computer, PTITInfo),
    SearchSuggestion("Marketing Manager", Icons.Default.Campaign, PTITWarning),
    SearchSuggestion("Data Analyst", Icons.Default.Analytics, PTITSuccess),
    SearchSuggestion("UI/UX Designer", Icons.Default.Palette, PTITSecondary),
    SearchSuggestion("Product Manager", Icons.Default.ManageAccounts, PTITPrimary),
    SearchSuggestion("DevOps Engineer", Icons.Default.CloudSync, PTITInfo),
    SearchSuggestion("Full Stack", Icons.Default.DeveloperMode, PTITError),
    SearchSuggestion("Remote Jobs", Icons.Default.Home, PTITSuccess)
)

private fun getTagColor(tag: UiJobTag): Color = when (tag) {
    UiJobTag.REACT, UiJobTag.VUE -> PTITInfo
    UiJobTag.JAVA, UiJobTag.SPRING -> PTITWarning
    UiJobTag.SQL, UiJobTag.PYTHON -> PTITSuccess
    UiJobTag.FIGMA -> PTITSecondary
    UiJobTag.KUBERNETES, UiJobTag.AWS, UiJobTag.CI_CD -> PTITError
    UiJobTag.MARKETING, UiJobTag.ANALYTICS -> PTITPrimary
}

@Preview(showBackground = true, device = "spec:width=393dp,height=851dp,dpi=420")
@Composable
fun JobSearchScreenPreview() {
    MaterialTheme {
        Surface {
            JobSearchScreen(
                uiState = JobSearchUiState(
                    searchQuery = "Frontend",
                    selectedCategory = CategoryFilter(id = "it", name = "IT - Phần mềm"),
                    activeFilters = listOf("IT - Phần mềm"),
                    isLoading = false,
                    jobs = sampleJobs(),
                    totalJobs = 123,
                    currentPage = 1,
                    totalPages = 5
                ),
                onSearchQueryChange = {},
                onSearch = {},
                onQuickSuggestion = {},
                onRemoveFilter = {},
                onClearFilters = {},
                onPageChange = {},
                onRetry = {},
                onBack = {},
                onJobSelected = { _ -> },
                onApplyJob = { _ -> },
                onSaveJob = { _ -> },
                onCompanySelected = { _ -> }
            )
        }
    }
}

/* ---------------------- SAMPLE DATA ---------------------- */

private fun sampleJobs(): List<UiJob> {
    val c1 = UiCompany(
        id = 1,
        backendId = "company_1",
        name = "FPT Software",
        logo = "https://img.topdev.vn/unsafe/150x/https://static.topcv.vn/company_logos/OeWHmPgGYL7CqbjwzYfD.jpg",
        industry = "Công nghệ thông tin",
        size = "1000-5000 nhân viên",
        address = "Cầu Giấy, Hà Nội",
        jobCount = 120
    )
    val c2 = UiCompany(
        id = 2,
        backendId = "company_2",
        name = "VNG Corporation",
        logo = "https://img.topdev.vn/unsafe/150x/https://static.topcv.vn/company_logos/vng-corporation-6195fb1a4e59c.jpg",
        industry = "Game/Entertainment",
        size = "500-1000 nhân viên",
        address = "Quận 7, TP.HCM",
        jobCount = 80
    )
    val c3 = UiCompany(
        id = 3,
        backendId = "company_3",
        name = "Tiki Corporation",
        logo = "https://img.topdev.vn/unsafe/150x/https://static.topcv.vn/company_logos/tiki-corporation-6195fb39e0a1b.jpg",
        industry = "E-commerce",
        size = "200-500 nhân viên",
        address = "Quận 1, TP.HCM",
        jobCount = 45
    )

    return listOf(
        UiJob(
            id = 1,
            backendId = "job_1",
            title = "Frontend Developer (React/Vue)",
            company = c1,
            salary = "15 - 25 triệu VNĐ",
            location = "Hà Nội",
            experience = "1-3 năm",
            deadline = "31/12/2025",
            tags = listOf(UiJobTag.REACT, UiJobTag.VUE, UiJobTag.SQL),
            category = "IT - Phần mềm",
            description = listOf("Phát triển UI", "Tối ưu hiệu năng", "Code review"),
            requirements = listOf("Kinh nghiệm React/Vue", "Hiểu biết REST/JSON"),
            benefits = listOf("Bảo hiểm", "Lương tháng 13"),
            workLocation = "Trụ sở chính",
            level = "Junior/Mid",
            education = "Đại học",
            quantity = "3",
            format = "Toàn thời gian"
        ),
        UiJob(
            id = 2,
            backendId = "job_2",
            title = "Backend Developer (Java Spring)",
            company = c2,
            salary = "20 - 35 triệu VNĐ",
            location = "TP. Hồ Chí Minh",
            experience = "2-5 năm",
            deadline = "15/12/2025",
            tags = listOf(UiJobTag.JAVA, UiJobTag.SPRING, UiJobTag.SQL),
            category = "IT - Phần mềm",
            description = listOf("Xây dựng API", "Thiết kế DB", "Microservices"),
            requirements = listOf("Java, Spring Boot", "MySQL/Redis"),
            benefits = listOf("Bảo hiểm", "Khám sức khỏe"),
            workLocation = "Quận 7",
            level = "Mid/Senior",
            education = "Đại học",
            quantity = "2",
            format = "Toàn thời gian"
        ),
        UiJob(
            id = 3,
            backendId = "job_3",
            title = "UI/UX Designer",
            company = c3,
            salary = "12 - 18 triệu VNĐ",
            location = "TP. Hồ Chí Minh",
            experience = "1-2 năm",
            deadline = "30/11/2025",
            tags = listOf(UiJobTag.FIGMA),
            category = "Thiết kế",
            description = listOf("Thiết kế UI", "Wireframe", "Prototype"),
            requirements = listOf("Figma/Adobe XD", "Tư duy UX"),
            benefits = listOf("Laptop", "Giờ làm linh hoạt"),
            workLocation = "Quận 1",
            level = "Junior",
            education = "Đại học/Cao đẳng",
            quantity = "1",
            format = "Toàn thời gian"
        ),
        UiJob(
            id = 4,
            backendId = "job_4",
            title = "DevOps Engineer",
            company = c2,
            salary = "25 - 40 triệu VNĐ",
            location = "TP. Hồ Chí Minh",
            experience = "3-5 năm",
            deadline = "20/12/2025",
            tags = listOf(UiJobTag.KUBERNETES, UiJobTag.AWS, UiJobTag.CI_CD),
            category = "Hạ tầng",
            description = listOf("Quản lý hạ tầng", "Triển khai CI/CD", "Monitoring"),
            requirements = listOf("Docker, K8s", "AWS/GCP"),
            benefits = listOf("Stock option", "Bảo hiểm"),
            workLocation = "Quận 1",
            level = "Senior",
            education = "Đại học",
            quantity = "2",
            format = "Toàn thời gian"
        ),
        UiJob(
            id = 5,
            backendId = "job_5",
            title = "Marketing Specialist",
            company = c3,
            salary = "10 - 15 triệu VNĐ",
            location = "TP. Hồ Chí Minh",
            experience = "1-3 năm",
            deadline = "10/12/2025",
            tags = listOf(UiJobTag.MARKETING, UiJobTag.ANALYTICS),
            category = "Marketing",
            description = listOf("Lập kế hoạch", "Quản lý chiến dịch", "Phân tích dữ liệu"),
            requirements = listOf("Facebook/Google Ads", "Content"),
            benefits = listOf("Hoa hồng", "Đào tạo"),
            workLocation = "Quận 7",
            level = "Junior/Mid",
            education = "Đại học",
            quantity = "2",
            format = "Toàn thời gian"
        ),
        UiJob(
            id = 6,
            backendId = "job_6",
            title = "Data Analyst",
            company = c1,
            salary = "18 - 28 triệu VNĐ",
            location = "TP. Hồ Chí Minh",
            experience = "2-4 năm",
            deadline = "05/12/2025",
            tags = listOf(UiJobTag.SQL, UiJobTag.PYTHON),
            category = "Data",
            description = listOf("Phân tích dữ liệu", "Làm báo cáo", "Dashboard"),
            requirements = listOf("SQL, Python", "Tableau/Power BI"),
            benefits = listOf("Học phí khóa học", "Bảo hiểm"),
            workLocation = "Quận 3",
            level = "Mid",
            education = "Đại học",
            quantity = "1",
            format = "Toàn thời gian"
        )
    )
}
