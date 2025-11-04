package com.example.ptitjob.ui.component
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

// --- 1. DATA MODEL ---
data class CompanyDetailData(
    val id: Int,
    val name: String,
    val logo: String,
    val coverImage: String,
    val description: String,
    val industry: String,
    val size: String,
    val location: String,
    val openJobs: Int?,
    val isTopCompany: Boolean,
    val isFeatured: Boolean
)

// --- 2. MAIN COMPONENT ---
@Composable
fun CompanyDetailCard(
    company: CompanyDetailData,
    onViewCompany: (Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Animation states for hover effect
    val elevation by animateDpAsState(if (isHovered) 12.dp else 2.dp, label = "elevation_anim")
    val borderColor by animateColorAsState(if (isHovered) Color(0xFF009A3E) else Color(0xFFE0E0E0), label = "border_color_anim")
    val translationY by animateFloatAsState(if (isHovered) -4f else 0f, label = "translation_y_anim")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { this.translationY = translationY }
            .clickable { onViewCompany(company.id) }
            .hoverable(interactionSource),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column {
            CoverImageSection(company = company)
            CompanyInfoSection(company = company, onViewCompany = { onViewCompany(company.id) })
        }
    }
}


// --- 3. CHILD COMPOSABLES ---

@Composable
private fun CoverImageSection(company: CompanyDetailData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // --- Cover Image ---
        Image(
            painter = rememberAsyncImagePainter(model = company.coverImage),
            contentDescription = "${company.name} cover image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- Badges ---
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (company.isTopCompany) {
                BadgeChip(text = "TOP COMPANY", backgroundColor = Color(0xFFFF6B35))
            }
            if (company.isFeatured) {
                BadgeChip(text = "NỔI BẬT", backgroundColor = Color(0xFF009A3E))
            }
        }

        // --- Logo ---
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 24.dp, y = 40.dp), // Nổi lên trên và lệch ra ngoài
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 8.dp,
            border = BorderStroke(4.dp, Color.White)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = company.logo),
                contentDescription = "${company.name} logo",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompanyInfoSection(company: CompanyDetailData, onViewCompany: () -> Unit) {
    Column(
        modifier = Modifier.padding(
            top = 48.dp, // Tạo không gian cho logo
            start = 24.dp,
            end = 24.dp,
            bottom = 24.dp
        )
    ) {
        Text(
            text = company.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = company.industry,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // --- Stats ---
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            InfoChip(icon = Icons.Default.Person, text = company.size)
            InfoChip(icon = Icons.Default.LocationOn, text = company.location)
            company.openJobs?.let {
                if (it > 0) {
                    InfoChip(icon = Icons.Default.Person, text = "$it vị trí tuyển dụng", color = Color(0xFF009A3E))
                }
            }
        }

        // --- Description ---
        Text(
            text = company.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 22.sp, // Tương đương line-height 1.6
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- Action Button ---
        OutlinedButton(
            onClick = onViewCompany,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color(0xFF009A3E)),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text("Xem thông tin công ty", color = Color(0xFF009A3E), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BadgeChip(text: String, backgroundColor: Color) {
    SuggestionChip(
        onClick = {},
        label = { Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = backgroundColor,
            labelColor = Color.White
        ),
        border = null,
        modifier = Modifier.height(22.dp)
    )
}

@Composable
private fun InfoChip(icon: ImageVector, text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}


// --- 4. PREVIEW ---
@Preview(showBackground = true)
@Composable
fun CompanyDetailCardPreview() {
    val sampleCompany = CompanyDetailData(
        id = 1,
        name = "CÔNG TY CỔ PHẦN CÔNG NGHỆ BORDER-X",
        logo = "https://i.imgur.com/your-logo.png", // Thay bằng URL thật để test
        coverImage = "https://i.imgur.com/your-cover.png", // Thay bằng URL thật để test
        description = "Công ty Border-X là một trong những đơn vị tiên phong trong lĩnh vực Print-on-Demand (POD) tại Việt Nam. Chúng tôi tạo ra các sản phẩm độc đáo và sáng tạo, mang lại giá trị cho khách hàng trên toàn thế giới. Môi trường làm việc năng động, chuyên nghiệp.",
        industry = "Thương mại điện tử & IT",
        size = "50 - 100 nhân viên",
        location = "Hà Nội",
        openJobs = 12,
        isTopCompany = true,
        isFeatured = true
    )
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp).width(400.dp)) {
            CompanyDetailCard(company = sampleCompany, onViewCompany = {})
        }
    }
}