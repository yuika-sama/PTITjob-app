package com.example.ptitjob.ui.screen.candidate.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptitjob.ui.component.CompanyItem
import com.example.ptitjob.ui.component.IndustryItem
import com.example.ptitjob.ui.component.JobCategory
import com.example.ptitjob.ui.component.JobItem
import com.example.ptitjob.ui.component.Location
import com.example.ptitjob.ui.navigation.DashboardSearchPayload
import com.example.ptitjob.ui.theme.*

// ---------------------- Screen ----------------------
@Composable
fun CandidateDashboardRoute(
    onNavigateToJobSearch: (DashboardSearchPayload?) -> Unit,
    onNavigateToJobDetail: (String) -> Unit,
    onNavigateToCompanyDetail: (String) -> Unit,
    onNavigateToCompanies: () -> Unit,
    onNavigateToIndustries: () -> Unit,
    onNavigateToTool: (QuickToolType) -> Unit,
    viewModel: CandidateDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    CandidateDashboardScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::refreshDashboard,
    onSearchRequest = onNavigateToJobSearch,
        onJobSelected = onNavigateToJobDetail,
        onViewAllJobs = { onNavigateToJobSearch(null) },
        onCompanySelected = onNavigateToCompanyDetail,
        onViewAllCompanies = onNavigateToCompanies,
    onCategorySelected = onNavigateToJobSearch,
        onViewAllCategories = onNavigateToIndustries,
        onToolSelected = onNavigateToTool
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidateDashboardScreen(
    uiState: CandidateDashboardUiState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onSearchRequest: (DashboardSearchPayload?) -> Unit,
    onJobSelected: (String) -> Unit,
    onViewAllJobs: () -> Unit,
    onCompanySelected: (String) -> Unit,
    onViewAllCompanies: () -> Unit,
    onCategorySelected: (DashboardSearchPayload?) -> Unit,
    onViewAllCategories: () -> Unit,
    onToolSelected: (QuickToolType) -> Unit
) {
    var selectedTimeFilter by rememberSaveable { mutableStateOf("Tất cả") }
    val timeFilters = listOf("Tất cả", "Hôm nay", "Tuần này", "Tháng này")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                        startY = 0f,
                        endY = 800f
                    )
                )
        ) {
            if (uiState.isLoading && uiState.featuredJobs.isEmpty() &&
                uiState.topCompanies.isEmpty() && uiState.hotIndustries.isEmpty()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PTITPrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        DashboardHeader(
                            categories = uiState.searchCategories,
                            locations = uiState.searchLocations,
                            onSearchRequest = onSearchRequest,
                            onCategorySelected = onCategorySelected,
                            onRefresh = onRefresh
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    PTITBackgroundLight,
                                    PTITCornerRadius.xl,
                                )
                                .padding(PTITSpacing.lg)
                        ) {
                            DashboardStatsSection(uiState.stats)

                            Spacer(Modifier.height(PTITSpacing.xl))

                            TimeFilterSection(
                                selectedFilter = selectedTimeFilter,
                                filters = timeFilters,
                                onFilterChange = { selectedTimeFilter = it }
                            )

                            Spacer(Modifier.height(PTITSpacing.lg))

                            FeaturedJobsSection(
                                jobs = uiState.featuredJobs,
                                isLoading = uiState.isLoading,
                                onJobClick = onJobSelected,
                                onViewAll = onViewAllJobs
                            )

                            Spacer(Modifier.height(PTITSpacing.xl))

                            TopCompaniesSection(
                                companies = uiState.topCompanies,
                                isLoading = uiState.isLoading,
                                onCompanyClick = onCompanySelected,
                                onViewAll = onViewAllCompanies
                            )

                            Spacer(Modifier.height(PTITSpacing.xl))

                            HotIndustriesSection(
                                categories = uiState.hotIndustries,
                                isLoading = uiState.isLoading,
                                onCategoryClick = { industry, index ->
                                    val category = uiState.searchCategories.getOrNull(index)
                                    val targetId = category?.id ?: industry.id.toString()
                                    onCategorySelected(
                                        DashboardSearchPayload(
                                            categoryId = targetId,
                                            categoryName = category?.name ?: industry.name
                                        )
                                    )
                                },
                                onViewAll = onViewAllCategories
                            )

                            Spacer(Modifier.height(PTITSpacing.xl))

                            QuickToolsSection(onToolSelected = onToolSelected)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- Dashboard Components ----------------------

@Composable
private fun DashboardHeader(
    categories: List<JobCategory>,
    locations: List<Location>,
    onSearchRequest: (DashboardSearchPayload?) -> Unit,
    onCategorySelected: (DashboardSearchPayload?) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PTITSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Chào mừng trở lại! 👋",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextLight,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Tìm kiếm cơ hội việc làm tốt nhất",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PTITTextLight.copy(alpha = 0.9f)
                    )
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                IconButton(onClick = onRefresh) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Làm mới",
                        tint = PTITTextLight
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Thông báo",
                            tint = PTITTextLight,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                    }
                }
            }
        }
        
        PTITSpacing.xl.let { Spacer(Modifier.height(it)) }
        
        // Enhanced Search Bar
        DashboardSearchBar(
            categories = categories,
            locations = locations,
            onSearchRequest = onSearchRequest,
            onCategorySelected = onCategorySelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardSearchBar(
    categories: List<JobCategory>,
    locations: List<Location>,
    onSearchRequest: (DashboardSearchPayload?) -> Unit,
    onCategorySelected: (DashboardSearchPayload?) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val displayCategories = if (categories.isEmpty()) {
        listOf(
            JobCategory(id = "it", name = "IT"),
            JobCategory(id = "marketing", name = "Marketing"),
            JobCategory(id = "finance", name = "Tài chính"),
            JobCategory(id = "accounting", name = "Kế toán"),
            JobCategory(id = "hr", name = "Nhân sự")
        )
    } else {
        categories.take(6)
    }
    val quickLocations = locations.take(6)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        shadowElevation = PTITElevation.md,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Tìm kiếm công việc, công ty...",
                        color = PTITTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Tìm kiếm",
                        tint = PTITPrimary
                    )
                },
                trailingIcon = {
                    Surface(
                        shape = PTITCornerRadius.sm,
                        color = PTITPrimary,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                val query = searchQuery.trim()
                                onSearchRequest(
                                    query.takeIf { it.isNotEmpty() }
                                        ?.let { DashboardSearchPayload(keyword = it) }
                                )
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Thực hiện tìm kiếm",
                                tint = PTITTextLight,
                                modifier = Modifier.size(PTITSize.iconSm)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = PTITNeutral300,
                    focusedBorderColor = PTITPrimary
                ),
                shape = PTITCornerRadius.md,
                singleLine = true
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                items(displayCategories) { category ->
                    FilterChip(
                        onClick = {
                            onCategorySelected(
                                DashboardSearchPayload(
                                    categoryId = category.id,
                                    categoryName = category.name
                                )
                            )
                        },
                        label = { Text(category.name) },
                        selected = false
                    )
                }
            }

            if (quickLocations.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    items(quickLocations) { location ->
                        AssistChip(
                            onClick = {
                                onSearchRequest(
                                    DashboardSearchPayload(keyword = location.name)
                                )
                            },
                            label = { Text(location.name) },
                            border = BorderStroke(
                                width = 1.dp,
                                color = PTITPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardStatsSection(stats: DashboardStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                icon = Icons.Default.Work,
                value = stats.totalJobs.takeIf { it > 0 }?.toString() ?: "Đang cập nhật",
                label = "Việc làm",
                color = PTITSuccess
            )
            StatCard(
                icon = Icons.Default.Business,
                value = stats.totalCompanies.takeIf { it > 0 }?.toString() ?: "Đang cập nhật",
                label = "Công ty",
                color = PTITInfo
            )
            StatCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                value = "${stats.highlightRate}%",
                label = "Tỷ lệ nổi bật",
                color = PTITWarning
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
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
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimeFilterSection(
    selectedFilter: String,
    filters: List<String>,
    onFilterChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Lọc theo thời gian",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextPrimary
            )
        )
        
        PTITSpacing.md.let { Spacer(Modifier.height(it)) }
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            items(filters) { filter ->
                FilterChip(
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter) },
                    selected = selectedFilter == filter,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PTITPrimary,
                        selectedLabelColor = PTITTextLight
                    )
                )
            }
        }
    }
}

@Composable
private fun FeaturedJobsSection(
    jobs: List<JobItem>,
    isLoading: Boolean,
    onJobClick: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥 Việc làm nổi bật",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            TextButton(onClick = onViewAll) {
                Text(
                    "Xem tất cả",
                    color = PTITPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        PTITSpacing.md.let { Spacer(Modifier.height(it)) }

        when {
            isLoading && jobs.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PTITPrimary)
                }
            }

            jobs.isEmpty() -> {
                Text(
                    text = "Chưa có việc làm nổi bật",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary)
                )
            }

            else -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    items(jobs.take(6)) { job ->
                        FeaturedJobCard(
                            job = job,
                            onClick = { onJobClick(job.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedJobCard(
    job: JobItem,
    onClick: () -> Unit,
    onBookmark: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        shape = PTITCornerRadius.lg,
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
//                Surface(
//                    shape = PTITCornerRadius.sm,
//                    color = PTITSuccess.copy(alpha = 0.1f)
//                ) {
//                    Text(
//                        text = if (job.isUrgent) "⚡ GẤP" else "📋 MỚI",
//                        style = MaterialTheme.typography.labelSmall.copy(
//                            fontWeight = FontWeight.Bold,
//                            color = if (job.isUrgent) PTITError else PTITSuccess
//                        ),
//                        modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs)
//                    )
//                }
                
                IconButton(
                    onClick = onBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = "Save job",
                        tint = PTITTextSecondary,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                }
            }
            
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Icon(
                    Icons.Default.Business,
                    contentDescription = null,
                    tint = PTITTextSecondary,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = job.company,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary
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
                    tint = PTITTextSecondary,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = job.location,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary
                    )
                )
            }
            
            HorizontalDivider(color = PTITNeutral300)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.salary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITSuccess
                    )
                )
                
                Button(
                    onClick = { /* TODO: Apply */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PTITPrimary
                    ),
                    shape = PTITCornerRadius.sm,
                    contentPadding = PaddingValues(horizontal = PTITSpacing.md, vertical = PTITSpacing.sm)
                ) {
                    Text(
                        "Ứng tuyển",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TopCompaniesSection(
    companies: List<CompanyItem>,
    isLoading: Boolean,
    onCompanyClick: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🏢 Top công ty",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            TextButton(onClick = onViewAll) {
                Text(
                    "Xem tất cả",
                    color = PTITPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        PTITSpacing.md.let { Spacer(Modifier.height(it)) }

        when {
            isLoading && companies.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PTITPrimary)
                }
            }

            companies.isEmpty() -> {
                Text(
                    text = "Chưa có công ty nổi bật",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary)
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(companies.take(4)) { company ->
                        TopCompanyCard(
                            company = company,
                            onClick = { onCompanyClick(company.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopCompanyCard(
    company: CompanyItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Surface(
                shape = CircleShape,
                color = PTITNeutral50,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = "Company logo",
                        tint = PTITTextSecondary,
                        modifier = Modifier.size(PTITSize.iconLg)
                    )
                }
            }
            
            Text(
                text = company.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

//            Surface(
//                shape = PTITCornerRadius.sm,
//                color = PTITInfo.copy(alpha = 0.1f)
//            ) {
//                Text(
//                    text = "${company.jobCount} việc làm",
//                    style = MaterialTheme.typography.labelSmall.copy(
//                        fontWeight = FontWeight.Medium,
//                        color = PTITInfo
//                    ),
//                    modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs)
//                )
//            }
        }
    }
}

@Composable
private fun HotIndustriesSection(
    categories: List<IndustryItem>,
    isLoading: Boolean,
    onCategoryClick: (IndustryItem, Int) -> Unit,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💼 Ngành nghề hot",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            TextButton(onClick = onViewAll) {
                Text(
                    "Xem tất cả",
                    color = PTITPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        PTITSpacing.md.let { Spacer(Modifier.height(it)) }

        when {
            isLoading && categories.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PTITPrimary)
                }
            }

            categories.isEmpty() -> {
                Text(
                    text = "Chưa có dữ liệu ngành nghề",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary)
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    modifier = Modifier.height(240.dp)
                ) {
                    itemsIndexed(categories.take(4)) { index, category ->
                        IndustryCard(
                            category = category,
                            onClick = { onCategoryClick(category, index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IndustryCard(
    category: IndustryItem,
    onClick: () -> Unit
) {
    val colors = listOf(PTITPrimary, PTITSecondary, PTITSuccess, PTITWarning)
    val color = colors[category.id % colors.size]
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = PTITCornerRadius.lg,
        color = color.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                }
            }
            
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
//            Text(
//                text = "${category.jobCount} việc làm",
//                style = MaterialTheme.typography.bodySmall.copy(
//                    fontWeight = FontWeight.Medium,
//                    color = color
//                )
//            )
        }
    }
}

@Composable
private fun QuickToolsSection(onToolSelected: (QuickToolType) -> Unit) {
    Column {
        Text(
            text = "🛠️ Công cụ hữu ích",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextPrimary
            )
        )
        
        PTITSpacing.md.let { Spacer(Modifier.height(it)) }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md),
            modifier = Modifier.height(200.dp)
        ) {
            items(getQuickTools()) { tool ->
                QuickToolCard(
                    tool = tool,
                    onClick = { onToolSelected(tool.type) }
                )
            }
        }
    }
}

@Composable
private fun QuickToolCard(tool: QuickTool, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Surface(
                shape = CircleShape,
                color = tool.color.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        tool.icon,
                        contentDescription = null,
                        tint = tool.color,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                }
            }
            
            Text(
                text = tool.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Data class cho Quick Tools
data class QuickTool(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val type: QuickToolType
)

private fun getQuickTools(): List<QuickTool> {
    return listOf(
        QuickTool(
            "Tính lương",
            "Gross ⇄ Net",
            Icons.Default.Calculate,
            PTITSuccess,
            QuickToolType.SALARY_CALCULATOR
        ),
        QuickTool(
            "Bảo hiểm XH",
            "Tính BHXH",
            Icons.Default.Security,
            PTITInfo,
            QuickToolType.SOCIAL_INSURANCE
        ),
        QuickTool(
            "Thuế TNCN",
            "Tính thuế",
            Icons.Default.Receipt,
            PTITWarning,
            QuickToolType.PERSONAL_INCOME_TAX
        ),
        QuickTool(
            "Lãi kép",
            "Đầu tư tiết kiệm",
            Icons.AutoMirrored.Filled.TrendingUp,
            PTITPrimary,
            QuickToolType.COMPOUND_INTEREST
        )
    )
}

enum class QuickToolType {
    SALARY_CALCULATOR,
    SOCIAL_INSURANCE,
    PERSONAL_INCOME_TAX,
    COMPOUND_INTEREST
}

// ---------------------- Preview ----------------------
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CandidateDashboardScreenPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val previewState = CandidateDashboardUiState(
                isLoading = false,
                featuredJobs = getSampleJobsForPreview(),
                topCompanies = getSampleCompaniesForPreview(),
                hotIndustries = getSampleCategoriesForPreview(),
                searchCategories = getSampleCategoriesForPreview().map { category ->
                    JobCategory(id = category.id.toString(), name = category.name)
                },
                searchLocations = getSampleLocationsForPreview()
            )
            CandidateDashboardScreen(
                uiState = previewState,
                snackbarHostState = remember { SnackbarHostState() },
                onRefresh = {},
                onSearchRequest = { _ -> },
                onJobSelected = {},
                onViewAllJobs = {},
                onCompanySelected = {},
                onViewAllCompanies = {},
                onCategorySelected = { _ -> },
                onViewAllCategories = {},
                onToolSelected = {}
            )
        }
    }
}

// ---------------------- Sample data helpers ----------------------
private fun getSampleJobsForPreview(): List<JobItem> {
    return listOf(
        JobItem("1", "Android Developer", "FPT Software", "Hà Nội", "Thỏa thuận", listOf("Java", "Kotlin"), true, null, "Miền Bắc"),
        JobItem("2", "iOS Developer", "VNG Corporation", "TP. Hồ Chí Minh", "$1500 - $2500", listOf("Swift", "Objective-C"), true, null, "Miền Nam"),
        JobItem("3", "ReactJS Developer", "Tiki", "Đà Nẵng", "Trên 20 triệu", listOf("ReactJS", "JavaScript"), false, null, "Miền Trung"),
        JobItem("4", "Data Scientist", "Viettel", "Hà Nội", "Cạnh tranh", listOf("Python", "R", "SQL"), true, null, "Miền Bắc"),
        JobItem("5", "Backend Developer (Go)", "Grab", "TP. Hồ Chí Minh", "Thỏa thuận", listOf("Go", "Microservices"), false, null, "Miền Nam"),
        JobItem("6", "Fullstack Developer (.NET)", "NashTech", "Hà Nội", "Lên đến $3000", listOf(".NET", "Angular"), false, null, "Miền Bắc"),
        JobItem("7", "QA/QC Engineer", "ZaloPay", "TP. Hồ Chí Minh", "15 - 25 triệu", listOf("Automation Test", "Manual Test"), false, null, "Miền Nam"),
        JobItem("8", "Project Manager", "CMC Global", "Đà Nẵng", "Cạnh tranh", listOf("Agile", "Scrum"), true, null, "Miền Trung"),
    )
}

private fun getSampleCompaniesForPreview(): List<CompanyItem> {
    return listOf(
        CompanyItem("1", "FPT Software", "IT Outsourcing", 150, null, "Hà Nội", "Miền Bắc", "fpt-hn"),
        CompanyItem("2", "VNG Corporation", "Internet & E-commerce", 80, null, "TP. Hồ Chí Minh", "Miền Nam", "vng-hcm"),
        CompanyItem("3", "Tiki", "E-commerce", 50, null, "Đà Nẵng", "Miền Trung", "tiki-dn"),
        CompanyItem("4", "Viettel", "Telecommunication", 200, null, "Hà Nội", "Miền Bắc", "viettel-hn"),
        CompanyItem("5", "Grab", "Ride-hailing & Delivery", 120, null, "TP. Hồ Chí Minh", "Miền Nam", "grab-hcm"),
        CompanyItem("6", "NashTech", "IT Services", 90, null, "Hà Nội", "Miền Bắc", "nashtech-hn"),
        CompanyItem("7", "ZaloPay", "Fintech & Payments", 45, null, "TP. Hồ Chí Minh", "Miền Nam", "zalopay-hcm"),
        CompanyItem("8", "CMC Global", "IT Services", 110, null, "Đà Nẵng", "Miền Trung", "cmc-dn")
    )
}

private fun getSampleCategoriesForPreview(): List<IndustryItem> {
    return listOf(
        IndustryItem(1, "IT - Phần mềm", 1250, null),
        IndustryItem(2, "Hành chính - Văn phòng", 850, null),
        IndustryItem(3, "Marketing - Truyền thông", 920, null),
        IndustryItem(4, "Kế toán - Kiểm toán", 780, null),
        IndustryItem(5, "Ngân hàng - Tài chính", 650, null),
        IndustryItem(6, "Bán hàng - Kinh doanh", 1100, null),
        IndustryItem(7, "Sản xuất - Vận hành", 540, null),
        IndustryItem(8, "Giáo dục - Đào tạo", 430, null)
    )
}

private fun getSampleLocationsForPreview(): List<Location> {
    return listOf(
        Location(1, "Hà Nội"),
        Location(2, "TP. Hồ Chí Minh"),
        Location(3, "Đà Nẵng"),
        Location(4, "Hải Phòng"),
        Location(5, "Cần Thơ"),
        Location(6, "Bình Dương")
    )
}
