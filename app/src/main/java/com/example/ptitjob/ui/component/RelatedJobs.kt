package com.example.ptitjob.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.data.model.RelatedJob

// --- Component Chính ---
@Composable
fun RelatedJobs(jobs: List<RelatedJob>) {
    Column(modifier = Modifier.padding(top = 40.dp)) { // Tương đương mt={5}
        // --- Tiêu đề Section ---
        Text(
            text = "Việc làm liên quan",
            style = MaterialTheme.typography.headlineSmall, // Tương đương h5
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp) // Tương đương gutterBottom
        )

        // --- Lưới các công việc liên quan ---
        LazyVerticalGrid(
            // Tự động điều chỉnh số cột (1, 2, 3) dựa trên chiều rộng màn hình
            columns = GridCells.Adaptive(minSize = 300.dp),
            // Khoảng cách giữa các mục
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Tương đương gap: 2
            verticalArrangement = Arrangement.spacedBy(16.dp),
            // Tắt cuộn của lưới để LazyColumn bên ngoài xử lý
            modifier = Modifier.heightIn(max = 1000.dp)
        ) {
            items(jobs) { job ->
                RelatedJobCard(job = job)
            }
        }
    }
}

// --- Component Card cho một việc làm liên quan ---
@Composable
private fun RelatedJobCard(job: RelatedJob) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Logic điều hướng */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- Tiêu đề và Tên công ty ---
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = job.company,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Lương và Địa điểm ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(
                    icon = Icons.Default.KeyboardArrowUp,
                    text = job.salary,
                    color = Color(0xFF00B14F) // Màu xanh lá
                )
                InfoChip(
                    icon = Icons.Default.LocationOn,
                    text = job.location
                )
            }
        }
    }
}

// --- Composable con để hiển thị cặp Icon và Text ---
@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

// --- Preview để xem trước giao diện ---
@Preview(showBackground = true, widthDp = 1200)
@Composable
fun RelatedJobsPreview() {
    val sampleJobs = listOf(
        RelatedJob(
            title = "TikTok Livestream Game Streamer/Idol",
            company = "KUA SHIDAI NETWORK TECHNOLOGY",
            salary = "15 - 30 triệu",
            location = "Hồ Chí Minh"
        ),
        RelatedJob(
            title = "Nhân Viên Livestream Ngành Du Lịch (Biết Tiếng Trung)",
            company = "YING YING",
            salary = "35 - 45 triệu",
            location = "Hồ Chí Minh"
        ),
        RelatedJob(
            title = "Host Livestream (MC Bán Hàng Online)",
            company = "CAO SỸ OAI",
            salary = "20 - 50 triệu",
            location = "Hồ Chí Minh"
        ),
        RelatedJob(
            title = "Frontend Developer (ReactJS)",
            company = "Global Tech",
            salary = "Thỏa thuận",
            location = "Đà Nẵng"
        )
    )

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            RelatedJobs(jobs = sampleJobs)
        }
    }
}