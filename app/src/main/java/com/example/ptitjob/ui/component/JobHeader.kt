package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Data Model (tương đương với props) ---
data class JobHeaderData(
    val title: String,
    val companyName: String,
    val salary: String,
    val location: String,
    val experience: String,
    val deadline: String
)

// --- Component Chính ---
@Composable
fun JobHeader(job: JobHeaderData) {
    // Màu sắc chủ đạo từ mã React
    val greenColor = Color(0xFF00B14F)

    Column(modifier = Modifier.padding(16.dp)) {
        // --- Tiêu đề và Tên công ty ---
        Text(
            text = job.title,
            style = MaterialTheme.typography.headlineMedium, // Tương đương h4
            fontWeight = FontWeight.Bold
        )
        Text(
            text = job.companyName,
            style = MaterialTheme.typography.titleMedium, // Tương đương subtitle1
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Các thông tin chính: Lương, Địa điểm, Kinh nghiệm ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp) // Tương đương spacing={3}
        ) {
            InfoChip(icon = Icons.Default.KeyboardArrowUp, text = job.salary, color = greenColor)
            InfoChip(icon = Icons.Default.LocationOn, text = job.location, color = greenColor)
            InfoChip(icon = Icons.Default.Person, text = job.experience, color = greenColor)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) // Tương đương my: 2

        // --- Hạn nộp hồ sơ ---
        InfoChip(
            icon = Icons.Default.DateRange,
            text = "Hạn nộp hồ sơ: ${job.deadline}",
            textStyle = MaterialTheme.typography.bodyMedium // Tương đương body2
        )

        Spacer(modifier = Modifier.height(24.dp)) // Tương đương mb={3}

        // --- Các nút hành động ---
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { // Tương đương spacing={2}
            Button(
                onClick = { /* TODO: Logic ứng tuyển */ },
                colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp) // Tương đương px: 4, py: 1.5
            ) {
                Icon(Icons.Default.Send, contentDescription = "Ứng tuyển")
                Spacer(Modifier.width(8.dp))
                Text("Ứng tuyển ngay", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            OutlinedButton(
                onClick = { /* TODO: Logic lưu tin */ },
                border = BorderStroke(1.dp, greenColor),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Done, contentDescription = "Lưu tin", tint = greenColor)
                Spacer(Modifier.width(8.dp))
                Text("Lưu tin", color = greenColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// --- Composable con có thể tái sử dụng để hiển thị cặp Icon và Text ---
@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge // Tương đương body1
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Icon chỉ mang tính trang trí
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp)) // Tương đương mr: 1
        Text(text = text, style = textStyle, color = color)
    }
}


// --- Preview để xem trước giao diện ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun JobHeaderPreview() {
    val sampleJob = JobHeaderData(
        title = "Live Streaming Host",
        companyName = "CÔNG TY TNHH AGARI",
        salary = "Thỏa thuận",
        location = "Hồ Chí Minh",
        experience = "1 năm",
        deadline = "29/10/2025"
    )

    MaterialTheme {
        Surface {
            JobHeader(job = sampleJob)
        }
    }
}