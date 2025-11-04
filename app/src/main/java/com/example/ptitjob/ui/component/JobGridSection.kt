package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// --- Data Classes giả lập ---
data class JobItem(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val tags: List<String>,
    val isTop: Boolean,
    val logo: String?,
    val region: String,
)

val REGIONS = listOf("Tất cả", "Miền Bắc", "Miền Trung", "Miền Nam")

// --- Component Chính ---
@Composable
fun JobsGridSection(
    jobs: List<JobItem>,
    isLoading: Boolean = false
) {
    // --- State Management (Tương đương useState) ---
    var regionFilter by remember { mutableStateOf("Tất cả") }
    var page by remember { mutableStateOf(0) }

    val themeColors = MaterialTheme.colorScheme

    // --- UI ---
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = themeColors.surface
        )
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                // --- Header Row ---
                JobsGridHeader(
                    page = page,
                    totalPages = (jobs.size / 12) + 1, // Logic giả
                    onPrevClick = { /* TODO: Logic */ },
                    onNextClick = { /* TODO: Logic */ }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- Region Chips ---
                RegionFilterChips(
                    selectedRegion = regionFilter,
                    onRegionSelected = { region -> regionFilter = region }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- Suggestion Bar ---
                SuggestionBar(
                    itemCount = jobs.size, // Logic giả
                    totalCount = jobs.size,
                    regionFilter = regionFilter
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Jobs Grid ---
                JobsGrid(jobs = jobs)
            }
        }
    }
}


// --- Các Composable con ---

@Composable
private fun JobsGridHeader(
    page: Int,
    totalPages: Int,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        // Tiêu đề với thanh trang trí bên trái
        Text(
            text = "Việc làm tốt nhất",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = primaryColor,
            modifier = Modifier.drawBehind {
                drawLine(
                    color = primaryColor,
                    start = Offset(-12.dp.toPx(), 0f),
                    end = Offset(-12.dp.toPx(), size.height),
                    strokeWidth = 4.dp.toPx()
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f)) // Tương đương flexGrow: 1

        Text(
            text = "Trang ${page + 1} / $totalPages",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onPrevClick, enabled = page > 0) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Trang trước")
        }

        IconButton(onClick = onNextClick, enabled = page < totalPages - 1) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Trang sau")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionFilterChips(
    selectedRegion: String,
    onRegionSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        REGIONS.forEach { region ->
            val isSelected = region == selectedRegion
            FilterChip(
                selected = isSelected,
                onClick = { onRegionSelected(region) },
                label = { Text("$region (20)") }, // Số lượng jobs giả
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Selected",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}


@Composable
private fun SuggestionBar(itemCount: Int, totalCount: Int, regionFilter: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Filter icon",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        val regionText = if (regionFilter != "Tất cả") "tại $regionFilter" else "trên toàn quốc"
        Text(
            text = "Hiển thị $itemCount / $totalCount việc làm $regionText.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JobsGrid(jobs: List<JobItem>) {
    FlowRow(
        maxItemsInEachRow = 2, // số cột bạn muốn
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        jobs.forEach { job ->
            // gợi ý: mỗi card nên chiếm nửa hàng
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {
                JobItemCard(job = job)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JobItemCard(job: JobItem) {
    Card(
        onClick = { /* TODO: Xử lý sự kiện click để điều hướng */ },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box { // Dùng Box để xếp chồng Chip "TOP" lên trên
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // --- Logo & Title ---
                Row(verticalAlignment = Alignment.Top) {
                    // Logo
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = job.logo,
                        ),
                        contentDescription = "${job.company} logo",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // Title và Company
                    Column {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = { PlainTooltip { Text(job.title) } },
                            state = rememberTooltipState()
                        ) {
                            Text(
                                text = job.title,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }

                        Text(
                            text = job.company,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // --- Salary & Tags ---
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { /* Do nothing */ },
                        label = { Text(job.salary) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        )
                    )
                    job.tags.forEach { tag ->
                        AssistChip(
                            onClick = { /* Do nothing */ },
                            label = { Text(tag) },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        )
                    }
                }
            }

            // --- TOP Chip (nếu có) ---
            if (job.isTop) {
                SuggestionChip(
                    onClick = { /* Do nothing */ },
                    label = { Text("TOP") },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFF4CAF50) // Green
                    )
                )
            }
        }
    }
}


// --- Preview để xem trước giao diện ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun JobsGridSectionPreview() {
    val sampleJobs = List(12) { i ->
        JobItem(
            id = "$i",
            title = "Lập trình viên ReactJS (Fresher/Junior) $i",
            company = "FPT Software",
            location = "Hà Nội",
            salary = "Thỏa thuận",
            tags = listOf("Hà Nội"),
            isTop = i % 4 == 0,
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/React-icon.svg/1200px-React-icon.svg.png",
            region = "Miền Bắc"
        )
    }

    MaterialTheme {
        JobsGridSection(jobs = sampleJobs, isLoading = false)
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun JobsGridSectionLoadingPreview() {
    MaterialTheme {
        JobsGridSection(jobs = emptyList(), isLoading = true)
    }
}