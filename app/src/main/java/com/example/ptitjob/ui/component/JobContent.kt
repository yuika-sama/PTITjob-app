package com.example.ptitjob.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Data Model (tương đương với props) ---
data class JobContentData(
    val description: String?,
    val requirements: String?,
    val benefits: String?
)

// --- Component Chính ---
@Composable
fun JobContent(job: JobContentData) {
    Column(modifier = Modifier.padding(16.dp)) {
        // --- Section Mô tả công việc ---
        ContentSection(
            title = "Mô tả công việc",
            content = job.description
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp)) // Tương đương my: 3

        // --- Section Yêu cầu công việc ---
        ContentSection(
            title = "Yêu cầu công việc",
            content = job.requirements
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        // --- Section Quyền lợi ---
        ContentSection(
            title = "Quyền lợi",
            content = job.benefits
        )
    }
}

// --- Composable con có thể tái sử dụng cho mỗi section ---
@Composable
private fun ContentSection(title: String, content: String?) {
    // Xử lý chuỗi null và thay thế ký tự xuống dòng
    val processedContent = content?.replace("\\n", "\n") ?: "Chưa có nội dung."

    Column(modifier = Modifier.fillMaxWidth()) {
        // --- Tiêu đề Section ---
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall, // Tương đương h5
            fontWeight = FontWeight.SemiBold // Tương đương fontWeight={600}
        )

        Spacer(modifier = Modifier.height(8.dp)) // Thay thế cho gutterBottom

        // --- Nội dung Section ---
        Text(
            text = processedContent,
            style = MaterialTheme.typography.bodyLarge, // Tương đương body1
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Tương đương color: 'text.secondary'
            lineHeight = 28.sp // Tương đương lineHeight: 1.8 (16sp * 1.8)
        )
    }
}

// --- Preview để xem trước giao diện ---
@Preview(showBackground = true)
@Composable
fun JobContentPreview() {
    val sampleJobContent = JobContentData(
        description = "- Bán hàng qua các nền tảng phát trực tiếp (livestream) như TikTok, Facebook, Shopee.\n- Lên kịch bản, chuẩn bị nội dung và tương tác với khán giả trong suốt buổi live.\n- Phối hợp với team marketing để triển khai các chiến dịch quảng bá.",
        requirements = "- Có kinh nghiệm livestream bán hàng ít nhất 6 tháng.\\n- Kỹ năng giao tiếp tốt, tự tin, năng động.\\n- Ngoại hình sáng, không nói ngọng, nói lắp.",
        benefits = "- Lương cứng + % hoa hồng hấp dẫn.\n- Môi trường làm việc trẻ trung, sáng tạo.\n- Được đào tạo và phát triển kỹ năng."
    )

    MaterialTheme {
        Surface {
            JobContent(job = sampleJobContent)
        }
    }
}

@Preview(showBackground = true, name = "Job Content with Null Data")
@Composable
fun JobContentNullPreview() {
    val sampleJobContent = JobContentData(
        description = "Mô tả công việc.",
        requirements = null, // Giả lập trường hợp không có dữ liệu
        benefits = "Quyền lợi hấp dẫn."
    )

    MaterialTheme {
        Surface {
            JobContent(job = sampleJobContent)
        }
    }
}