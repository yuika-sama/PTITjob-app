package com.example.ptitjob.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ptitjob.data.model.CompanySidebarInfo

// --- Component Chính ---
@Composable
fun CompanySidebar(companyInfo: CompanySidebarInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp) // Tương đương borderRadius: 2
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Tương đương p: 2
            // --- Header: Logo và Tên công ty ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp) // Tương đương mb={2}
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = companyInfo.logoUrl),
                    contentDescription = "${companyInfo.name} logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)) // Tương đương variant="rounded"
                )
                Spacer(modifier = Modifier.width(16.dp)) // Tương đương mr: 2
                Text(
                    text = companyInfo.name,
                    style = MaterialTheme.typography.titleLarge, // Tương đương h6
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            // --- Danh sách thông tin ---
            Column(
                modifier = Modifier.padding(top = 16.dp), // Tương đương mt={2}
                verticalArrangement = Arrangement.spacedBy(12.dp) // Tương đương spacing={1.5}
            ) {
                InfoRow(
                    icon = Icons.Outlined.Person,
                    label = "Quy mô",
                    value = companyInfo.size
                )
                InfoRow(
                    icon = Icons.Outlined.Menu,
                    label = "Lĩnh vực",
                    value = companyInfo.industry
                )
                InfoRow(
                    icon = Icons.Outlined.Home,
                    label = "Địa điểm",
                    value = companyInfo.address,
                    alignIconToTop = true // Căn icon lên trên cho văn bản dài
                )
            }

            // --- Link xem trang công ty ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), // Tương đương mt={2}
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = { /* TODO: Logic điều hướng */ }) {
                    Text(
                        text = "Xem trang công ty",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


// --- Composable con có thể tái sử dụng để hiển thị một dòng thông tin ---
@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    alignIconToTop: Boolean = false
) {
    Row(
        verticalAlignment = if (alignIconToTop) Alignment.Top else Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant, // Tương đương color: 'text.secondary'
            modifier = Modifier
                .size(20.dp)
                // Căn lề thủ công nếu cần
                .padding(top = if (alignIconToTop) 2.dp else 0.dp)
        )
        Spacer(modifier = Modifier.width(12.dp)) // Tương đương mr: 1.5
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append("$label: ")
                }
                append(value)
            },
            style = MaterialTheme.typography.bodyMedium // Tương đương body2
        )
    }
}


// --- Preview để xem trước giao diện ---
@Preview(showBackground = true, widthDp = 350)
@Composable
fun CompanySidebarPreview() {
    val sampleCompanyInfo = CompanySidebarInfo(
        logoUrl = "https://example.com/logo.png", // Thay bằng URL logo thật để thấy ảnh
        name = "CÔNG TY TNHH AGARI",
        size = "25-99 nhân viên",
        industry = "Sản xuất",
        address = "30A Trần Cao Vân, Phường Đô Vinh, TP. Phan Rang-Tháp Chàm, Tỉnh Ninh Thuận"
    )

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            CompanySidebar(companyInfo = sampleCompanyInfo)
        }
    }
}