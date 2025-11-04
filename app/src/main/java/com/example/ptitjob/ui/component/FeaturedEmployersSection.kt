package com.example.ptitjob.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlin.math.floor

// --- Data Model ---
data class FeaturedCompany(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val companySize: String,
    val description: String,
    val jobCount: Int,
    val address: String,
    val website: String?,
)

// --- Component Chính ---
@Composable
fun FeaturedEmployersSection(
    companies: List<FeaturedCompany>,
    isLoading: Boolean = false,
) {
    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        if (isLoading) {
            FeaturedEmployersLoadingState()
        } else {
            FeaturedEmployersHeader(
                onPrevClick = { /* TODO */ },
                onNextClick = { /* TODO */ },
                onViewAllClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (companies.isEmpty()) {
                NoEmployersState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // Cố định 3 cột
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.heightIn(max = 500.dp) // Giới hạn chiều cao
                ) {
                    items(companies) { company ->
                        EmployerCard(company = company)
                    }
                }
            }
        }
    }
}


// --- Các Trạng thái và Composable con ---

@Composable
private fun FeaturedEmployersHeader(
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onViewAllClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Các nhà tuyển dụng nổi bật",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Khám phá những công ty hàng đầu đang tuyển dụng",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onPrevClick) { Icon(Icons.Default.KeyboardArrowLeft, null) }
            IconButton(onClick = onNextClick) { Icon(Icons.Default.KeyboardArrowRight, null) }
            TextButton(onClick = onViewAllClick) {
                Text("Xem tất cả")
                Icon(Icons.Default.ArrowForward, null)
            }
        }
    }
}

@Composable
fun EmployerCard(company: FeaturedCompany) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val elevation by animateDpAsState(if (isHovered) 12.dp else 2.dp, label = "")
    val borderColor by animateColorAsState(if (isHovered) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f), label = "")
    val translationY by animateFloatAsState(if (isHovered) -6f else 0f, label = "")
    val rating by remember { mutableFloatStateOf((Math.random() * 1.5 + 3.5).toFloat()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .clickable { /* TODO */ }
            .hoverable(interactionSource)
            .graphicsLayer { this.translationY = translationY },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Header ---
                Image(
                    painter = rememberAsyncImagePainter(model = company.logoUrl),
                    contentDescription = "${company.name} logo",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = company.companySize,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RatingBar(rating = rating)

                // --- Description ---
                Text(
                    text = company.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.height(36.dp).padding(top = 8.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // --- Stats ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompanyStat(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Build,
                        value = company.jobCount.toString(),
                        label = "Việc làm",
                        color = MaterialTheme.colorScheme.primary
                    )
                    CompanyStat(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LocationOn,
                        value = company.address,
                        label = "Địa điểm",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- Website Button ---
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Xem website")
                }
            }
            // --- Verified Badge ---
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF66BB6A)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Done, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun RatingBar(rating: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (i <= floor(rating)) Color(0xFFFFC107) else Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun CompanyStat(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NoEmployersState() {
    // Tương tự như component JobRecommendationsSection
}

@Composable
private fun FeaturedEmployersLoadingState() {
    val shimmerBrush = createShimmerBrush() // Lấy từ file JobRecommendationsSection
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // ... Tương tự như skeleton của các component trước
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 1200)
@Composable
fun FeaturedEmployersSectionPreview() {
    val sampleCompanies = listOf(
        FeaturedCompany("1", "FPT Software", null, "1000+ nhân viên", "Là công ty phần mềm hàng đầu Việt Nam, cung cấp dịch vụ cho khách hàng toàn cầu.", 150, "Hà Nội", "https://fpt-software.com"),
        FeaturedCompany("2", "VNG Corporation", null, "500-1000 nhân viên", "Kỳ lân công nghệ của Việt Nam với các sản phẩm Zalo, ZaloPay, và game.", 85, "TP.HCM", "https://vng.com.vn"),
        FeaturedCompany("3", "Viettel Group", null, "5000+ nhân viên", "Tập đoàn Viễn thông và Công nghệ Quân đội, hoạt động đa quốc gia.", 210, "Toàn quốc", "https://viettel.com.vn")
    )
    MaterialTheme {
        FeaturedEmployersSection(companies = sampleCompanies, isLoading = false)
    }
}