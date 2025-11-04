package com.example.ptitjob.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Data Model (tương đương với props) ---
data class JobDescriptionData(
    val category: String,
    val description: List<String>,
    val requirements: List<String>,
    val benefits: List<String>,
    val workLocation: String
)

// --- Component Chính ---
@Composable
fun JobDescription(job: JobDescriptionData) {
    // Column là container chính cho toàn bộ component
    Column(modifier = Modifier.padding(16.dp)) {
        // --- Tiêu đề chính ---
        Text(
            text = "Chi tiết tin tuyển dụng",
            style = MaterialTheme.typography.headlineSmall, // Tương đương h5
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp)) // Thay thế cho gutterBottom

        // --- Chip danh mục ---
        AssistChip(
            onClick = { /* Do nothing */ },
            label = { Text(job.category) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color(0xFFE0E0E0) // Màu nền tùy chỉnh
            )
        )

        Spacer(modifier = Modifier.height(24.dp)) // Tương đương mb: 3

        // --- Các section chi tiết ---
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) { // Tương đương mt: 3
            DescriptionSection(
                title = "Mô tả công việc",
                items = job.description
            )
            DescriptionSection(
                title = "Yêu cầu ứng viên",
                items = job.requirements
            )
            DescriptionSection(
                title = "Quyền lợi",
                items = job.benefits
            )

            // --- Section địa điểm làm việc ---
            Column {
                Text(
                    text = "Địa điểm làm việc",
                    style = MaterialTheme.typography.titleLarge, // Tương đương h6
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = job.workLocation,
                    style = MaterialTheme.typography.bodyLarge // Tương đương body1
                )
            }
        }
    }
}


// --- Composable con có thể tái sử dụng để hiển thị một section danh sách ---
@Composable
private fun DescriptionSection(title: String, items: List<String>) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge, // Tương đương h6
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp)) // Thay thế cho gutterBottom

        // Cột chứa các mục danh sách
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Tương đương mb: 1 cho mỗi <li>
            items.forEach { item ->
                ListItem(text = item)
            }
        }
    }
}


// --- Composable con để hiển thị một mục danh sách có dấu đầu dòng ---
@Composable
private fun ListItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "• ",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


// --- Preview để xem trước giao diện ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun JobDescriptionPreview() {
    val sampleJob = JobDescriptionData(
        category = "Host Livestream/Streamer",
        description = listOf(
            "Bán hàng qua các nền tảng phát trực tiếp như TikTok, Facebook.",
            "Lên kịch bản, chuẩn bị nội dung và tương tác với khán giả trong suốt buổi live.",
            "Làm việc 6 ngày/tuần, nghỉ 1 ngày không cố định."
        ),
        requirements = listOf(
            "Có khả năng nói chuyện lưu loát, tự tin trước ống kính.",
            "Ngoại hình sáng, ưu tiên ứng viên đã có kinh nghiệm ở vị trí tương đương.",
            "Biết sử dụng cơ bản phần mềm OBS là một lợi thế."
        ),
        benefits = listOf(
            "Lương: Thỏa Thuận + Thưởng doanh số + Lương tháng 13.",
            "Môi trường làm việc năng động, trẻ trung, sáng tạo.",
            "Được đào tạo các kỹ năng chuyên môn cần thiết cho công việc."
        ),
        workLocation = "61 Hoàng Trọng Mậu, Khu dân cư Him Lam, Phường Tân Hưng, Quận 7, TP.HCM"
    )

    MaterialTheme {
        Surface {
            JobDescription(job = sampleJob)
        }
    }
}