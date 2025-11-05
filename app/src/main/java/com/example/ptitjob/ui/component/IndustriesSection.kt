package com.example.ptitjob.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// --- Data Classes giả lập ---
data class IndustryItem(
    val id: Int,
    val name: String,
    val jobs: Int,
    val iconUrl: String?,
    val backendId: String = id.toString(),
)

// --- Helper để quản lý màu sắc động ---
private data class IndustryColors(
    val primary: Color,
    val secondary: Color,
    val shadow: Color,
    val background: Color,
    val border: Color
)

private fun getIndustryColors(index: Int): IndustryColors {
    return when (index % 4) {
        0 -> IndustryColors(Color(0xFFC62828), Color(0xFFD32F2F), Color(0xFFC62828).copy(alpha = 0.3f), Color(0xFFC62828).copy(alpha = 0.1f), Color(0xFFC62828).copy(alpha = 0.2f))
        1 -> IndustryColors(Color(0xFF1976D2), Color(0xFF1E88E5), Color(0xFF1976D2).copy(alpha = 0.3f), Color(0xFF1976D2).copy(alpha = 0.1f), Color(0xFF1976D2).copy(alpha = 0.2f))
        2 -> IndustryColors(Color(0xFF388E3C), Color(0xFF4CAF50), Color(0xFF388E3C).copy(alpha = 0.3f), Color(0xFF388E3C).copy(alpha = 0.1f), Color(0xFF388E3C).copy(alpha = 0.2f))
        else -> IndustryColors(Color(0xFFF57C00), Color(0xFFFF9800), Color(0xFFF57C00).copy(alpha = 0.3f), Color(0xFFF57C00).copy(alpha = 0.1f), Color(0xFFF57C00).copy(alpha = 0.2f))
    }
}

// --- Component Chính ---
@Composable
fun IndustriesSection(
    categories: List<IndustryItem>,
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            SectionHeader()
            Spacer(modifier = Modifier.height(24.dp))
            IndustriesGrid(categories = categories)
        }
    }
}


// --- Các Composable con ---

@Composable
private fun SectionHeader() {
    val primaryColor = MaterialTheme.colorScheme.primary
    Text(
        text = "Ngành nghề hàng đầu",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = primaryColor,
        modifier = Modifier
            .padding(start = 24.dp) // Tạo khoảng trống cho thanh trang trí
            .drawBehind {
                drawLine(
                    color = primaryColor,
                    start = Offset(-16.dp.toPx(), 0f),
                    end = Offset(-16.dp.toPx(), size.height),
                    strokeWidth = 4.dp.toPx()
                )
            }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IndustriesGrid(categories: List<IndustryItem>) {
    FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp)
    ){
        categories.forEach { category ->
            Box(modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
            ){
                IndustryCard(item = category, index = 0)
            }
        }
    }
}

@Composable
private fun IndustryCard(item: IndustryItem, index: Int) {
    val colors = getIndustryColors(index)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // --- Animate states for hover effect ---
    val translationY by animateFloatAsState(targetValue = if (isHovered) -8f else 0f, label = "translateY")
    val elevation by animateDpAsState(targetValue = if (isHovered) 12.dp else 4.dp, label = "elevation")
    val borderColor by animateColorAsState(targetValue = if (isHovered) colors.border else Color.Transparent, label = "borderColor")
    val overlayAlpha by animateFloatAsState(targetValue = if (isHovered) 1f else 0f, label = "overlayAlpha")

    Card(
        modifier = Modifier
            .height(200.dp)
            .graphicsLayer { this.translationY = translationY } // Hiệu ứng nhấc lên
            .hoverable(interactionSource)
            .clickable { /* TODO: Logic điều hướng */ },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(width = 1.dp, color = borderColor)
    ) {
        Box {
            // --- Background overlay for hover ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(overlayAlpha) // Animate opacity
                    .background(
                        Brush.linearGradient(
                            listOf(colors.primary.copy(alpha = 0.1f), colors.primary.copy(alpha = 0.05f))
                        )
                    )
            )

            // --- Main Content ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // --- Top Row: Icon and Job Count ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(colors.primary, colors.secondary)
                                )
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Icon
                        if (item.iconUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = item.iconUrl),
                                contentDescription = item.name,
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowForward, // Placeholder, as Computer is not default
                                contentDescription = item.name,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    // Job Count Badge
                    Text(
                        text = "${item.jobs} vị trí",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.primary,
                        modifier = Modifier
                            .background(colors.background, RoundedCornerShape(50))
                            .border(1.dp, colors.border, RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // Pushes content below to the bottom

                // --- Bottom Part: Title, Description, CTA ---
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Khám phá cơ hội trong lĩnh vực ${item.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO */ }
                ) {
                    Text(
                        text = "Xem chi tiết",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun IndustriesSectionPreview() {
    val sampleCategories = listOf(
        IndustryItem(1, "Công nghệ thông tin", 1250, null),
        IndustryItem(2, "Marketing & Truyền thông", 830, null),
        IndustryItem(3, "Kinh doanh & Bán hàng", 940, null),
        IndustryItem(4, "Thiết kế & Sáng tạo", 450, null),
        IndustryItem(5, "Nhân sự & Tuyển dụng", 320, null),
        IndustryItem(6, "Tài chính & Ngân hàng", 670, null),
        IndustryItem(7, "Sản xuất & Vận hành", 710, null),
        IndustryItem(8, "Giáo dục & Đào tạo", 280, null)
    )
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            IndustriesSection(categories = sampleCategories, isLoading = false)
        }
    }
}