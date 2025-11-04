package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// --- Data Model (tương đương với props) ---
data class JobSidebarData(
    val salary: String,
    val jobType: String,
    val locationName: String,
    val categoryName: String,
    val createdAt: String,
    val expiryDate: String?,
    val logoUrl: String?,
    val companyName: String,
    val companyId: String // Dùng cho navigation
)

// --- Component Chính ---
@Composable
fun JobSidebar(job: JobSidebarData) {
    Column {
        // --- Phần "Thông tin chung" ---
        Text(
            text = "Thông tin chung",
            style = MaterialTheme.typography.titleLarge, // Tương đương h6
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { // Tương đương gap: 2
            InfoRow(label = "Mức lương:", value = job.salary)
            InfoRow(label = "Hình thức:", value = job.jobType)
            InfoRow(label = "Địa điểm:", value = job.locationName)
            InfoRow(label = "Lĩnh vực:", value = job.categoryName)
            InfoRow(label = "Ngày đăng:", value = job.createdAt)
            job.expiryDate?.let {
                InfoRow(
                    label = "Hạn nộp:",
                    value = it,
                    valueColor = MaterialTheme.colorScheme.error // Tương đương warning.main
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp)) // Tương đương my: 3

        // --- Phần "Về công ty" ---
        Text(
            text = "Về công ty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // Tương đương gap: 2
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    CompanyAvatar(logoUrl = job.logoUrl)
                    Column {
                        Text(
                            text = job.companyName,
                            style = MaterialTheme.typography.titleMedium, // Tương đương subtitle1
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = job.locationName,
                            style = MaterialTheme.typography.bodyMedium, // Tương đương body2
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                OutlinedButton(
                    onClick = { /* TODO: Navigation logic */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xem trang công ty")
                }
            }
        }
    }
}

// --- Composable con để hiển thị một hàng thông tin ---
@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

// --- Composable con cho Avatar công ty với fallback Icon ---
@Composable
private fun CompanyAvatar(logoUrl: String?) {
    if (logoUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(model = logoUrl),
            contentDescription = "Company Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Company Icon",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


// --- Preview để xem trước giao diện ---
@Preview(showBackground = true)
@Composable
fun JobSidebarPreview() {
    val sampleJob = JobSidebarData(
        salary = "20.000.000 - 30.000.000 VND",
        jobType = "Toàn thời gian",
        locationName = "Quận 1, TP. Hồ Chí Minh",
        categoryName = "Công nghệ thông tin",
        createdAt = "25/10/2025",
        expiryDate = "25/11/2025",
        logoUrl = null, // Preview với fallback icon
        companyName = "Tech Solutions Inc.",
        companyId = "123"
    )

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            JobSidebar(job = sampleJob)
        }
    }
}