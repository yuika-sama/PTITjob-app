package com.example.ptitjob.ui.screen.candidate.companies

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ptitjob.data.model.Company
import com.example.ptitjob.data.model.Job
import com.example.ptitjob.ui.screen.candidate.companies.companyDetail.CompanyDetailScreen
import com.example.ptitjob.ui.theme.PTITBackgroundLight
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITNeutral300
import com.example.ptitjob.ui.theme.PTITNeutral50
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITSecondary
import com.example.ptitjob.ui.theme.PTITSize
import com.example.ptitjob.ui.theme.PTITSpacing
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import com.example.ptitjob.ui.theme.PTITTextLight
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITTextSecondary

/* =========================
 * Màn hình danh sách công ty
 * ========================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompaniesScreen(
    viewModel: CompaniesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }

    // Handle error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            // Show error snackbar or dialog
            // You can implement snackbar here if needed
        }
    }

    AnimatedVisibility(
        visible = selectedCompany == null,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        if (selectedCompany == null) {
            // Use PTITScreenContainer to handle layout properly with navbar
            com.example.ptitjob.ui.component.PTITScreenContainer(
                hasGradientBackground = true
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        CompaniesHeader(
                            searchTerm = searchQuery,
                            onSearchTermChange = viewModel::searchCompanies,
                            viewMode = viewMode,
                            onViewModeChange = { viewMode = it }
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                // ✅ Bo 2 góc trên đúng cách
                                .clip(
                                    PTITCornerRadius.xl
                                )
                                .background(PTITBackgroundLight)
                                .padding(PTITSpacing.lg)
                        ) {
                            when {
                                uiState.isLoading -> {
                                    LoadingSection()
                                }
                                uiState.errorMessage != null -> {
                                    ErrorState(message = uiState.errorMessage!!, onRetry = viewModel::refresh)
                                }
                                else -> {
                                    ResultsHeader(count = uiState.filteredCompanies.size, searchTerm = searchQuery)
                                    Spacer(Modifier.height(PTITSpacing.lg))

                                    if (uiState.filteredCompanies.isEmpty()) {
                                        EmptyState(searchTerm = searchQuery)
                                    } else {
                                        when (viewMode) {
                                            ViewMode.LIST -> {
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                                                ) {
                                                    uiState.filteredCompanies.forEach { company ->
                                                        CompanyCard(
                                                            company = company,
                                                            onClick = { selectedCompany = company }
                                                        )
                                                    }
                                                }
                                            }
                                            ViewMode.GRID -> {
                                                LazyVerticalGrid(
                                                    columns = GridCells.Adaptive(minSize = 160.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                                                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                                                    modifier = Modifier.height(800.dp)
                                                ) {
                                                    items(uiState.filteredCompanies, key = { it.id }) { company ->
                                                        CompanyGridCard(
                                                            company = company,
                                                            onClick = { selectedCompany = company }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = selectedCompany != null,
        enter = slideInHorizontally { it } + fadeIn(),
        exit = slideOutHorizontally { it } + fadeOut()
    ) {
        selectedCompany?.let { company ->
            CompanyDetailScreen(
                company = company,
                onBack = { selectedCompany = null }
            )
        }
    }
}

enum class ViewMode { LIST, GRID }

/* =========================
 * Header + Search + Toggle
 * ========================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompaniesHeader(
    searchTerm: String,
    onSearchTermChange: (String) -> Unit,
    viewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PTITSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = null,
                tint = PTITTextLight,
                modifier = Modifier.size(PTITSize.iconLg)
            )
            Text(
                text = "KHÁM PHÁ CÁC CÔNG TY HÀNG ĐẦU",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = PTITTextLight,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(PTITSpacing.sm))

        Text(
            text = "Tìm hiểu về những công ty uy tín và cơ hội việc làm hấp dẫn",
            color = PTITTextLight.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(PTITSpacing.xl))

        Surface(
            shape = PTITCornerRadius.lg,
            shadowElevation = PTITElevation.md,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                OutlinedTextField(
                    value = searchTerm,
                    onValueChange = onSearchTermChange,
                    placeholder = {
                        Text("Tìm kiếm công ty...", color = PTITTextSecondary)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = PTITPrimary)
                    },
                    trailingIcon = {
                        if (searchTerm.isNotEmpty()) {
                            IconButton(onClick = { onSearchTermChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = PTITTextSecondary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = PTITNeutral300,
                        focusedBorderColor = PTITPrimary
                    ),
                    singleLine = true,
                    shape = PTITCornerRadius.md
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chế độ xem:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = PTITTextSecondary
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)) {
                        androidx.compose.material3.FilterChip(
                            onClick = { onViewModeChange(ViewMode.LIST) },
                            label = { Text("Danh sách") },
                            selected = viewMode == ViewMode.LIST,
                            leadingIcon = if (viewMode == ViewMode.LIST) {
                                { Icon(Icons.Default.List, null, modifier = Modifier.size(PTITSize.iconSm)) }
                            } else null
                        )
                        androidx.compose.material3.FilterChip(
                            onClick = { onViewModeChange(ViewMode.GRID) },
                            label = { Text("Lưới") },
                            selected = viewMode == ViewMode.GRID,
                            leadingIcon = if (viewMode == ViewMode.GRID) {
                                { Icon(Icons.Default.GridView, null, modifier = Modifier.size(PTITSize.iconSm)) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

/* =========================
 * Khu vực kết quả / thẻ
 * ========================= */
@Composable
private fun ResultsHeader(count: Int, searchTerm: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = PTITPrimary,
                modifier = Modifier.size(PTITSize.iconMd)
            )
            Column {
                Text(
                    text = "Tìm thấy $count công ty",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
                if (searchTerm.isNotEmpty()) {
                    Text(
                        text = "Kết quả cho: \"$searchTerm\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CompanyCard(company: Company, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    modifier = Modifier.size(PTITSize.avatarLg),
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
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = "Company placeholder",
                                modifier = Modifier.size(PTITSize.iconLg),
                                tint = PTITTextSecondary
                            )
                        }
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
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = company.size ?: "Chưa cập nhật",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                }

                IconButton(onClick = { /* TODO: Add to favorites */ }) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = "Add to favorites",
                        tint = PTITTextSecondary
                    )
                }
            }

            Text(
                text = listOf(company.industry, company.address ?: "")
                    .filter { it.isNotBlank() }
                    .joinToString(" • "),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
                    company.address?.let { address ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = address,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(PTITSize.iconSm))
                            }
                        )
                    }
                    val jobCount = company.jobCount ?: 0
                    AssistChip(
                        onClick = {},
                        label = { Text("$jobCount việc làm") },
                        leadingIcon = {
                            Icon(Icons.Default.Work, null, modifier = Modifier.size(PTITSize.iconSm))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompanyGridCard(company: Company, onClick: () -> Unit) {
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
                modifier = Modifier.size(PTITSize.avatarXl),
                shape = CircleShape,
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
                            contentDescription = "Company placeholder",
                            modifier = Modifier.size(PTITSize.iconXl),
                            tint = PTITTextSecondary
                        )
                    }
                }
            }

            Text(
                text = company.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = company.industry,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            AssistChip(
                onClick = {},
                label = { Text("${company.jobCount ?: 0} việc làm") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/* =========================
 * Skeleton / Empty / Error
 * ========================= */
@Composable
private fun SkeletonCompanyCard() {
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Box(
                    modifier = Modifier
                        .size(PTITSize.avatarLg)
                        .clip(PTITCornerRadius.md)
                        .background(PTITNeutral300)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                ) {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(20.dp)
                            .clip(PTITCornerRadius.sm)
                            .background(PTITNeutral300)
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .clip(PTITCornerRadius.sm)
                            .background(PTITNeutral300)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(PTITCornerRadius.sm)
                    .background(PTITNeutral300)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(50))
                        .background(PTITNeutral300)
                )
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(50))
                        .background(PTITNeutral300)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(searchTerm: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconXl),
                tint = PTITTextSecondary
            )
            Text(
                text = if (searchTerm.isNotEmpty()) "Không tìm thấy công ty nào" else "Chưa có công ty nào",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = if (searchTerm.isNotEmpty())
                    "Thử tìm kiếm với từ khóa khác hoặc điều chỉnh bộ lọc"
                else
                    "Hãy quay lại sau để xem các công ty mới",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextSecondary
                ),
                textAlign = TextAlign.Center
            )
            if (searchTerm.isNotEmpty()) {
                Button(
                    onClick = { /* TODO: Clear search */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PTITPrimary
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(PTITSpacing.sm))
                    Text("Làm mới tìm kiếm")
                }
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconXl),
                tint = Color(0xFFB00020)
            )
            Text(
                text = "Có lỗi xảy ra",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB00020)
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextSecondary
                ),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB00020)
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(PTITSpacing.sm))
                Text("Thử lại")
            }
        }
    }
}

/* =========================
 * Company Detail header (local)
 * — dùng trong preview phụ
 * ========================= */
@Composable
private fun CompanyHeader(company: Company, onBack: () -> Unit) {
    Box(
        modifier = Modifier.background(
            Brush.linearGradient(listOf(Color(0xFFDE221A), Color(0xFFB01B14)))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White.copy(0.8f))
                    Text("Danh sách công ty", color = Color.White.copy(0.8f))
                }
                Text(">", color = Color.White.copy(0.8f))
                Text(
                    company.name,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White.copy(0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (company.logo.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = company.logo),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White.copy(0.85f),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        company.name,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        company.size?.let { InfoChip(icon = Icons.Default.Person, text = it) }
                        company.address?.let { InfoChip(icon = Icons.Default.LocationOn, text = it) }
                        company.jobCount?.takeIf { it > 0 }?.let {
                            InfoChip(icon = Icons.Default.Person, text = "$it việc làm")
                        }
                        InfoChip(icon = Icons.Default.Info, text = company.industry)
                    }
                }
            }
        }
    }
}

/* =========================
 * Các phần tử phụ trợ
 * ========================= */
@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Text(text, color = Color.White, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
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
                Icon(icon, null, tint = PTITPrimary, modifier = Modifier.size(PTITSize.iconMd))
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            content()
        }
    }
}

@Composable
private fun RowItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = PTITTextSecondary)
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = PTITTextPrimary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

/* =========================
 * Preview
 * ========================= */
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CompaniesScreenPreview() {
    MaterialTheme { CompaniesScreen() }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun CompanyDetailsScreenPreview() {
    MaterialTheme {
        Surface {
            CompanyDetailScreen(company = getSampleCompanies().first(), onBack = {})
        }
    }
}

/* =========================
 * Sample data
 * ========================= */
private fun getSampleCompanies() = listOf(
    Company(
        id = "1",
        name = "FPT Software",
        logo = "https://fastly.picsum.photos/id/1/200/200.jpg",
        industry = "Công nghệ thông tin",
        size = "10000+ nhân viên",
        address = "Hà Nội",
        jobCount = 150
    ),
    Company(
        id = "2",
        name = "VNG Corporation",
        logo = "https://fastly.picsum.photos/id/2/200/200.jpg",
        industry = "Công nghệ / Internet",
        size = "4000+ nhân viên",
        address = "TP. Hồ Chí Minh",
        jobCount = 85
    ),
    Company(
        id = "3",
        name = "Shopee Vietnam",
        logo = "https://fastly.picsum.photos/id/3/200/200.jpg",
        industry = "Thương mại điện tử",
        size = "2000+ nhân viên",
        address = "TP. Hồ Chí Minh",
        jobCount = 120
    ),
    Company(
        id = "",
        name = "Vietcombank",
        logo = "https://fastly.picsum.photos/id/4/200/200.jpg",
        industry = "Ngân hàng",
        size = "5000+ nhân viên",
        address = "Hà Nội",
        jobCount = 75
    ),
    Company(
        id = "",
        name = "Grab Vietnam",
        logo = "https://fastly.picsum.photos/id/5/200/200.jpg",
        industry = "Siêu ứng dụng / Vận chuyển",
        size = "1000+ nhân viên",
        address = "TP. Hồ Chí Minh",
        jobCount = 95
    ),
    Company(
        id = "",
        name = "Tiki Corporation",
        logo = "https://fastly.picsum.photos/id/6/200/200.jpg",
        industry = "Thương mại điện tử",
        size = "1500+ nhân viên",
        address = "TP. Hồ Chí Minh",
        jobCount = 60
    )
)

/**
 * Loading section with skeleton cards
 */
@Composable
private fun LoadingSection() {
    repeat(6) {
        SkeletonCompanyCard()
        Spacer(Modifier.height(PTITSpacing.md))
    }
}
