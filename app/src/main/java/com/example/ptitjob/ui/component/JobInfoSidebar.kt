package com.example.ptitjob.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Data Model (tương đương với type GeneralInfo) ---
data class GeneralInfo(
    val level: String,
    val education: String,
    val quantity: String,
    val format: String
)

// --- Component Chính ---
@Composable
fun JobInfoSidebar(generalInfo: GeneralInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp) // Tương đương borderRadius: 2
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Tương đương p: 2
            // --- Tiêu đề ---
            Text(
                text = "Thông tin chung",
                style = MaterialTheme.typography.titleLarge, // Tương đương h6
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // --- Danh sách thông tin ---
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp) // Tương đương spacing={2}
            ) {
                InfoRow(
                    icon = Icons.Outlined.Build,
                    label = "Cấp bậc",
                    value = generalInfo.level
                )
                InfoRow(
                    icon = Icons.Outlined.Person,
                    label = "Học vấn",
                    value = generalInfo.education
                )
                InfoRow(
                    icon = Icons.Outlined.AccountCircle,
                    label = "Số lượng tuyển",
                    value = generalInfo.quantity
                )
                InfoRow(
                    icon = Icons.Outlined.DateRange,
                    label = "Hình thức làm việc",
                    value = generalInfo.format
                )
            }
        }
    }
}


// --- Composable con có thể tái sử dụng để hiển thị một dòng thông tin ---
@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    val greenColor = Color(0xFF00B14F)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Icon chỉ mang tính trang trí
            tint = greenColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp)) // Tương đương mr: 1.5
        Column {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium, // Tương đương body2
                color = MaterialTheme.colorScheme.onSurfaceVariant // Màu cho văn bản phụ
            )
        }
    }
}


// --- Preview để xem trước giao diện ---
@Preview(showBackground = true, widthDp = 350)
@Composable
fun JobInfoSidebarPreview() {
    // Dữ liệu mẫu
    val sampleInfo = GeneralInfo(
        level = "Nhân viên",
        education = "Trung cấp trở lên",
        quantity = "1 người",
        format = "Toàn thời gian"
    )

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            JobInfoSidebar(generalInfo = sampleInfo)
        }
    }
}