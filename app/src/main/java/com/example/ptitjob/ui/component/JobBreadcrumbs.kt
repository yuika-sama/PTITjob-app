package com.example.ptitjob.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Component Chính ---
@Composable
fun JobBreadcrumbs(
    jobTitle: String,
    onNavigate: (path: String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 24.dp) // Tương đương mb: 3
    ) {
        // --- Mục "Trang chủ" ---
        TextButton(onClick = { onNavigate("candidate/") }) {
            Text(
                text = "Trang chủ",
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Tương đương color="inherit"
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Separator()

        // --- Mục "Việc làm" ---
        TextButton(onClick = { onNavigate("/candidate/jobs") }) {
            Text(
                text = "Việc làm",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Separator()

        // --- Mục Tiêu đề công việc (tĩnh) ---
        Text(
            text = jobTitle,
            color = MaterialTheme.colorScheme.onSurface, // Tương đương color="text.primary"
            fontWeight = FontWeight.SemiBold, // Làm nổi bật mục hiện tại
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// --- Composable con cho dấu phân cách ---
@Composable
private fun Separator() {
    Text(
        text = ">",
        modifier = Modifier.padding(horizontal = 8.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
}


// --- Preview để xem trước giao diện ---
@Preview(showBackground = true)
@Composable
fun JobBreadcrumbsPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            JobBreadcrumbs(
                jobTitle = "Tuyển Live Streaming Host",
                onNavigate = { path ->
                    // Trong thực tế, đây sẽ là lệnh gọi điều hướng.
                    // Đối với preview, chúng ta chỉ in ra để xác nhận.
                    println("Navigate to: $path")
                }
            )
        }
    }
}