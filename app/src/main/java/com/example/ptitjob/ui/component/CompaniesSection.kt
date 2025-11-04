package com.example.ptitjob.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// --- Data Classes giả lập ---
data class CompanyItem(
    val id: String,
    val name: String,
    val category: String,
    val jobs: Int,
    val logo: String?,
    val location: String,
    val region: String,
    val uniqueKey: String,
)

val COMPANY_REGIONS = listOf("Tất cả", "Miền Bắc", "Miền Trung", "Miền Nam")

// --- Component Chính ---
@Composable
fun CompaniesSection(
    companies: List<CompanyItem>,
    isLoading: Boolean = false,
) {
    var regionFilter by remember { mutableStateOf("Tất cả") }
    var page by remember { mutableStateOf(0) }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            // --- Header Banner ---
            CompaniesHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // --- Filter và Pagination ---
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                RegionFilterChips(
                    selectedRegion = regionFilter,
                    onRegionSelected = { regionFilter = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PaginationControls(
                    page = page,
                    totalPages = (companies.size / 8) + 1, // Logic giả
                    onPrevClick = { /* TODO */ },
                    onNextClick = { /* TODO */ },
                    onViewAllClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Grid các công ty ---
            CompaniesGrid(companies = companies)
        }
    }
}


// --- Các Composable con ---

@Composable
private fun CompaniesHeader() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box {
            // Lớp gradient phủ lên
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.8f),
                                secondaryColor.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Vòng tròn trang trí
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .size(120.dp)
                    .background(
                        color = secondaryColor.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )

            // Nội dung văn bản
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Thương hiệu lớn tiêu biểu",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Hàng trăm thương hiệu lớn tiêu biểu đang tuyển dụng nổi bật tại PTIT Job.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionFilterChips(
    selectedRegion: String,
    onRegionSelected: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        COMPANY_REGIONS.forEach { region ->
            val isSelected = region == selectedRegion
            FilterChip(
                selected = isSelected,
                onClick = { onRegionSelected(region) },
                label = { Text("$region (10)") }, // Số lượng giả
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                // A selected filter chip has a filled background and no border.
                // When unselected, it has an outlined style.
                border = if (isSelected) null else FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = false,
                    borderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
private fun PaginationControls(
    page: Int,
    totalPages: Int,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onViewAllClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onViewAllClick) {
            Text("Xem tất cả")
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = "Trang ${page + 1} / $totalPages",
            style = MaterialTheme.typography.bodySmall
        )
        IconButton(onClick = onPrevClick, enabled = page > 0) {
            Icon(Icons.Default.ArrowBack, "Trang trước")
        }
        IconButton(onClick = onNextClick, enabled = page < totalPages - 1) {
            Icon(Icons.Default.ArrowForward, "Trang sau")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompaniesGrid(companies: List<CompanyItem>, onCompanyClick: (CompanyItem) -> Unit = {}) {
    FlowRow(
        maxItemsInEachRow = 3, // số cột bạn muốn
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        companies.forEach { company ->
            // mỗi card chiếm ~1/3 hàng
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {
                CompanyItemCard(company = company)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyItemCard(company: CompanyItem) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val elevation by animateDpAsState(targetValue = if (isHovered) 8.dp else 2.dp, label = "elevation")
    val translationY by animateFloatAsState(targetValue = if (isHovered) -2f else 0f, label = "translationY")
    val borderColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .graphicsLayer { this.translationY = translationY } // Hiệu ứng nhấc lên
            .hoverable(interactionSource = interactionSource)
            .clickable { /* TODO: Open company details */ },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- Logo và Tên Công ty ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(model = company.logo),
                    contentDescription = "${company.name} logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(2.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(company.name) } },
                        state = rememberTooltipState()
                    ) {
                        Text(
                            text = company.name,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = company.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Spacer để đẩy 2 mục dưới cùng xuống
            Spacer(modifier = Modifier.weight(1f))

            // --- Địa điểm ---
            AssistChip(
                onClick = { /* Do nothing */ },
                label = { Text(company.location, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            )

            // --- Số lượng việc làm ---
            Text(
                text = "🗂 ${company.jobs} việc làm",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 1200)
@Composable
fun CompaniesSectionPreview() {
    val sampleCompanies = List(8) { i ->
        CompanyItem(
            id = "$i",
            name = "Công ty Cổ phần FPT chi nhánh $i",
            category = "Công nghệ thông tin",
            jobs = 25 + i,
            logo = "https://cdn.worldvectorlogo.com/logos/fpt-corporation.svg",
            location = "Khu Công nghệ cao Hòa Lạc, Hà Nội",
            region = "Miền Bắc",
            uniqueKey = "key_$i"
        )
    }
    MaterialTheme {
        CompaniesSection(companies = sampleCompanies)
    }
}