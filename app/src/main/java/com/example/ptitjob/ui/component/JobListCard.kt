package com.example.ptitjob.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

// --- Data Model ---
data class JobListCardData(
    val id: Int,
    val backendId: String = id.toString(),
    val title: String,
    val company: String,
    val companyLogo: String?,
    val salary: String,
    val location: String,
    val experience: String?,
    val postedTime: String,
    val deadline: String?,
    val isUrgent: Boolean,
    val isVerified: Boolean,
    val tags: List<String>?
)

// --- Component Chính ---
@Composable
fun JobListCard(
    job: JobListCardData,
    onApply: (JobListCardData) -> Unit,
    onSave: (JobListCardData) -> Unit,
    onCardClick: (JobListCardData) -> Unit = {}
) {
    var isSaved by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val elevation by animateDpAsState(if (isHovered) 8.dp else 1.dp, label = "")
    val borderColor by animateColorAsState(if (isHovered) Color(0xFF009A3E) else Color(0xFFE0E0E0), label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(job) }
            .hoverable(interactionSource),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Cột 1: Logo ---
            CompanyLogo(logoUrl = job.companyLogo, companyName = job.company)

            // --- Cột 2: Chi tiết công việc ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { /* Handle title click */ }
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Text(text = job.company, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (job.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Done, contentDescription = "Verified", tint = Color(0xFF009A3E), modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
                InfoRow(job = job)
                Spacer(Modifier.height(16.dp))
                TagsRow(tags = job.tags)
            }

            // --- Cột 3: Lương và Hành động ---
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                SalaryInfo(salary = job.salary, isUrgent = job.isUrgent)
                ActionButtons(
                    job = job,
                    isSaved = isSaved,
                    onApply = {
                        onApply(job)
                    },
                    onSave = {
                        isSaved = !isSaved
                        onSave(job)
                    }
                )
            }
        }
    }
}


// --- Các Composable con ---

@Composable
private fun CompanyLogo(logoUrl: String?, companyName: String) {
    if (logoUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(model = logoUrl),
            contentDescription = "$companyName logo",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = companyName.firstOrNull()?.toString() ?: "", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun InfoRow(job: JobListCardData) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        InfoChip(icon = Icons.Default.LocationOn, text = job.location)
        job.experience?.let { InfoChip(text = it) }
        job.deadline?.let { InfoChip(icon = Icons.Default.DateRange, text = "Còn $it") }
        InfoChip(icon = Icons.Default.AccessTime, text = job.postedTime)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsRow(tags: List<String>?) {
    if (!tags.isNullOrEmpty()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                SuggestionChip(
                    onClick = {},
                    label = { Text(tag, fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFFE8F5E8),
                        labelColor = Color(0xFF2E7D32)
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
private fun SalaryInfo(salary: String, isUrgent: Boolean) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = salary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF009A3E)
        )
        if (isUrgent) {
            SuggestionChip(
                onClick = {},
                label = { Text("GẤP", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = Color(0xFFFFF3E0),
                    labelColor = Color(0xFFF57C00)
                ),
                border = null,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ActionButtons(
    job: JobListCardData,
    isSaved: Boolean,
    onApply: (JobListCardData) -> Unit,
    onSave: (JobListCardData) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { onApply(job) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009A3E)),
            modifier = Modifier.width(120.dp)
        ) {
            Text("Ứng tuyển", fontWeight = FontWeight.SemiBold)
        }
        IconButton(onClick = { onSave(job) }) {
            Icon(
                imageVector = if (isSaved) Icons.Filled.Done else Icons.Default.BookmarkBorder,
                contentDescription = "Lưu",
                tint = if (isSaved) Color(0xFF009A3E) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector? = null, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun JobListCardPreview() {
    val sampleJob = JobListCardData(
        id = 1,
        title = "Nhân Viên Kinh Doanh - Lương Cứng 10-20 Triệu",
        company = "CÔNG TY CỔ PHẦN BẤT ĐỘNG SẢN PHỐ XANH",
        companyLogo = null,
        salary = "20 - 50 triệu",
        location = "Hà Nội",
        experience = "Không yêu cầu",
        postedTime = "Đăng 2 ngày trước",
        deadline = "5 ngày",
        isUrgent = true,
        isVerified = true,
        tags = listOf("Bất động sản", "Kinh doanh", "Hoa hồng cao")
    )
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            JobListCard(job = sampleJob, onApply = {}, onSave = {})
        }
    }
}