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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// --- Data Models ---
data class Job(
    val id: String,
    val title: String,
    val company: Company,
    val salary: Salary,
    val location: String,
    val experience: String,
    val jobType: String,
    val postedDate: String,
    val isUrgent: Boolean,
    val isSaved: Boolean,
    val skills: List<String>?
)
data class Company(val id: String, val name: String, val logo: String?)
data class Salary(val min: Int?, val max: Int?, val currency: String, val period: String)

// --- Component Chính ---
@Composable
fun JobListing(
    jobs: List<Job>,
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(jobs, key = { it.id }) { job ->
                // Quản lý trạng thái isSaved cho mỗi mục
                var isSaved by remember { mutableStateOf(job.isSaved) }
                JobItemCard(
                    job = job,
                    isSaved = isSaved,
                    onSaveToggle = { isSaved = !isSaved }
                )
            }
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                    OutlinedButton(onClick = { /* TODO: Tải thêm */ }) {
                        Text("Xem thêm việc làm")
                    }
                }
            }
        }
    }
}


// --- Component Card cho một việc làm ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JobItemCard(
    job: Job,
    isSaved: Boolean,
    onSaveToggle: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val elevation by animateDpAsState(if (isHovered) 8.dp else 2.dp, label = "elevation_animation")
    val borderColor by animateColorAsState(if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, label = "border_color_animation")
    val translationY by animateFloatAsState(if (isHovered) -2f else 0f, label = "translation_y_animation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { this.translationY = translationY }
            .clickable { /* TODO: onJobClick */ }
            .hoverable(interactionSource),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // --- Logo ---
                CompanyLogo(logoUrl = job.company.logo, companyName = job.company.name)

                // --- Chi tiết công việc ---
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = job.company.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp).clickable { /* TODO: onCompanyClick */ }
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        InfoChip(icon = Icons.Default.LocationOn, text = job.location)
                        InfoChip(icon = Icons.Default.Place, text = job.experience)
                    }
                    Spacer(Modifier.height(12.dp))
                    job.skills?.let {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            it.forEach { skill ->
                                SuggestionChip(onClick = {}, label = { Text(skill) })
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoChip(icon = Icons.Default.DateRange, text = job.postedDate, textStyle = MaterialTheme.typography.bodySmall)
                        Text(
                            text = formatSalary(job.salary),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // --- Nút Lưu ---
            IconButton(
                onClick = onSaveToggle,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Done else Icons.Default.Done,
                    contentDescription = "Lưu công việc",
                    tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // --- Badge "Đề xuất" ---
            if (job.isUrgent) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("Đề xuất cho bạn") },
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 14.dp, end = 56.dp)
                )
            }
        }
    }
}

@Composable
private fun CompanyLogo(logoUrl: String?, companyName: String) {
    if (logoUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(model = logoUrl),
            contentDescription = "$companyName logo",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = companyName.firstOrNull()?.toString() ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}


@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(4.dp))
        Text(text, style = textStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatSalary(salary: Salary): String {
    return when {
        salary.min != null && salary.max != null -> "${salary.min} - ${salary.max} ${salary.currency}"
        salary.min != null -> "Từ ${salary.min} ${salary.currency}"
        else -> "Thỏa thuận"
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun JobListingPreview() {
    val mockJobs = listOf(
        Job("1", "Kỹ Sư Công Trình", Company("evercon", "CÔNG TY TNHH XÂY DỰNG EVERCON", null), Salary(12, 16, "triệu", "tháng"), "Hà Nội", "2 năm", "Kỹ sư xây dựng", "1 ngày trước", false, false, listOf("Tuổi 25 - 40")),
        Job("2", "Nhân Viên Kinh Doanh Thiết Bị Implant", Company("im8", "CÔNG TY TNHH THƯƠNG MẠI SIV", null), Salary(12, 14, "triệu", "tháng"), "Đà Nẵng, Hồ Chí Minh", "1 năm", "Kinh doanh", "1 ngày trước", true, false, null),
        Job("3", "Nhân Viên KD BĐS - Lương Cứng 10-20 Triệu", Company("phoxanh", "CÔNG TY CP BĐS PHỐ XANH", null), Salary(20, 50, "triệu", "tháng"), "Hà Nội", "Không yêu cầu", "Bất động sản", "1 ngày trước", false, true, listOf("Từ 25 tuổi", "+1")),
    )
    MaterialTheme {
        Surface {
            JobListing(jobs = mockJobs)
        }
    }
}