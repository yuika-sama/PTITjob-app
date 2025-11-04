package com.example.ptitjob.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// --- Data Model ---
data class RecommendedJob(
    val id: String,
    val title: String,
    val companyName: String,
    val logoUrl: String?,
    val salary: String,
    val location: String,
    val jobType: String,
    val description: String,
    val dateRange: String,
)

// --- Component Chính ---
@Composable
fun JobRecommendationsSection(
    jobs: List<RecommendedJob>,
    isLoading: Boolean = false,
) {
    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        if (isLoading) {
            JobRecommendationsLoadingState()
        } else {
            JobRecommendationsHeader()
            Spacer(modifier = Modifier.height(16.dp))
            if (jobs.isEmpty()) {
                NoRecommendationsState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 350.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.heightIn(max = 1200.dp)
                ) {
                    items(jobs) { job ->
                        JobCard(job = job)
                    }
                }
            }
        }
    }
}


// --- Các Trạng thái và Composable con ---

@Composable
private fun JobRecommendationsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Gợi ý việc làm phù hợp",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Dựa trên hồ sơ và sở thích của bạn",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun JobCard(job: RecommendedJob) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val elevation by animateDpAsState(if (isHovered) 8.dp else 1.dp, label = "elevation")
    val borderColor by animateColorAsState(
        if (isHovered) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle click */ }
            .hoverable(interactionSource),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- Header: Logo, Title, Company ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(model = job.logoUrl),
                    contentDescription = "${job.companyName} logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = job.companyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // --- Details: Salary, Location, Type ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                JobInfoChip(icon = Icons.Default.KeyboardArrowUp, text = job.salary)
                JobInfoChip(icon = Icons.Default.LocationOn, text = job.location)
                JobInfoChip(icon = Icons.Default.Build, text = job.jobType)
            }

            Spacer(Modifier.height(12.dp))

            // --- Description ---
            Text(
                text = job.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2
            )

            Spacer(Modifier.height(12.dp))

            // --- Footer: Date & Badge ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                JobInfoChip(icon = Icons.Default.DateRange, text = job.dateRange)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Nổi bật",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Nổi bật",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}

@Composable
private fun JobInfoChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NoRecommendationsState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFF5F7FA), Color(0xFFC3CFE2))
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🔍 Chưa có gợi ý việc làm phù hợp",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Hãy cập nhật hồ sơ của bạn để nhận được những gợi ý việc làm phù hợp nhất",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = { /* TODO */ }) {
                Text("Khám phá việc làm")
            }
        }
    }
}

// --- Skeleton Loading State ---

@Composable
private fun JobRecommendationsLoadingState() {
    val shimmerBrush = createShimmerBrush()
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .height(40.dp)
                    .width(300.dp)
                    .background(shimmerBrush, RoundedCornerShape(8.dp))
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(6) {
                LoadingJobCard(brush = shimmerBrush)
            }
        }
    }
}

@Composable
private fun LoadingJobCard(brush: Brush) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(
                    modifier = Modifier
                        .size(48.dp)
                        .background(brush, CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Spacer(modifier = Modifier.height(20.dp).fillMaxWidth(0.8f).background(brush, RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.height(16.dp).fillMaxWidth(0.6f).background(brush, RoundedCornerShape(4.dp)))
                }
            }
            Spacer(Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(40.dp).fillMaxWidth().background(brush, RoundedCornerShape(4.dp)))
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(20.dp).width(120.dp).background(brush, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(20.dp).width(80.dp).background(brush, RoundedCornerShape(4.dp)))
            }
        }
    }
}

@Composable
fun createShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer_translate"
    )
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun JobRecommendationsSectionPreview_Content() {
    val sampleJobs = List(6) {
        RecommendedJob(
            id = "$it",
            title = "Android Developer (Kotlin, Jetpack Compose)",
            companyName = "TechCorp Solutions",
            logoUrl = "https://example.com/logo.png",
            salary = "25 - 40 triệu",
            location = "Hà Nội",
            jobType = "Full-time",
            description = "Phát triển các ứng dụng Android gốc bằng Kotlin và Jetpack Compose. Tối ưu hóa hiệu suất và đảm bảo trải nghiệm người dùng tốt nhất.",
            dateRange = "01/10/2025 - 31/10/2025"
        )
    }
    MaterialTheme {
        JobRecommendationsSection(jobs = sampleJobs, isLoading = false)
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun JobRecommendationsSectionPreview_Empty() {
    MaterialTheme {
        JobRecommendationsSection(jobs = emptyList(), isLoading = false)
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun JobRecommendationsSectionPreview_Loading() {
    MaterialTheme {
        JobRecommendationsSection(jobs = emptyList(), isLoading = true)
    }
}