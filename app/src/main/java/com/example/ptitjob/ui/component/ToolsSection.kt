package com.example.ptitjob.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Data Model ---
data class FeatureTool(
    val id: String,
    val title: String,
    val description: String,
    val cta: String,
    val icon: ImageVector,
)

// --- Dữ liệu tĩnh ---
val profileTools = listOf(
    FeatureTool(
        id = "unemployment-insurance",
        title = "Tính trợ cấp thất nghiệp",
        description = "Công cụ giúp bạn tính toán mức trợ cấp thất nghiệp dựa trên thời gian đóng bảo hiểm, mức lương trung bình và quy định hiện hành.",
        cta = "Tính ngay",
        icon = Icons.Outlined.List,
    ),
    FeatureTool(
        id = "compound-interest",
        title = "Tính lãi suất kép",
        description = "Tìm hiểu sức mạnh của lãi kép! Nhập số vốn ban đầu, lãi suất và thời gian để biết số tiền bạn sẽ nhận được sau nhiều năm.",
        cta = "Khám phá lãi kép",
        icon = Icons.Outlined.Done,
    ),
)

val selfInsightTools = listOf(
    FeatureTool(
        id = "salary-calculator",
        title = "Tính lương thực nhận",
        description = "Công cụ tính lương giúp bạn biết chính xác số tiền thực nhận sau khi trừ các khoản bảo hiểm và thuế thu nhập cá nhân.",
        cta = "Tính lương ngay",
        icon = Icons.Outlined.AccountBox,
    ),
    FeatureTool(
        id = "personal-income-tax",
        title = "Tính thuế thu nhập cá nhân",
        description = "Tính nhanh số tiền thuế TNCN phải nộp dựa trên thu nhập hàng tháng hoặc hàng năm, giúp bạn lập kế hoạch tài chính hiệu quả.",
        cta = "Tính thuế ngay",
        icon = Icons.Outlined.Info,
    ),
)


// --- Component Chính ---
@Composable
fun ToolsSection() {
    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        // --- Section 1: Profile Tools ---
        SectionHeader("Cùng PTIT Job xây dựng thương hiệu cá nhân")
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.heightIn(max= 500.dp) //chiều cao tối đa,
        ) {
            items(profileTools) { tool ->
                FeatureCard(tool = tool)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Section 2: Self-Insight Tools ---
        // Bạn có thể thêm header ở đây nếu cần
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.heightIn(max= 500.dp) //chiều cao tối đa
        ) {
            items(selfInsightTools) { tool ->
                FeatureCard(tool = tool)
            }
        }
    }
}


// --- Các Composable con ---

@Composable
private fun SectionHeader(title: String) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = primaryColor,
        modifier = Modifier
            .padding(start = 24.dp) // Khoảng trống cho thanh trang trí
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

@Composable
fun FeatureCard(tool: FeatureTool) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val translationY by animateFloatAsState(targetValue = if (isHovered) -4f else 0f, label = "translationY")
    val borderColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        label = "borderColor"
    )
    val shadowColor = MaterialTheme.colorScheme.primary.copy(alpha = if (isHovered) 0.15f else 0.05f)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .graphicsLayer { this.translationY = translationY } // Hiệu ứng nhấc lên
            .hoverable(interactionSource)
            .clickable { /* TODO: Navigation Logic */ },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Phần nội dung bên trái ---
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f)) // Đẩy nút bấm xuống dưới
                GradientButton(
                    text = tool.cta,
                    onClick = { /* TODO: Navigation Logic */ }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- Phần icon bên phải ---
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary,
                            spotColor = MaterialTheme.colorScheme.primary
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.title,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryDarkColor = MaterialTheme.colorScheme.primaryContainer

    val translationY by animateFloatAsState(targetValue = if (isHovered) -1f else 0f, label = "buttonTranslateY")
    val shadowElevation by animateDpAsState(targetValue = if (isHovered) 10.dp else 6.dp, label = "buttonShadow")

    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer { this.translationY = translationY }
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(50),
                spotColor = primaryColor,
                ambientColor = primaryColor
            )
            .hoverable(interactionSource),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(), // Xóa padding mặc định
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = if (isHovered) listOf(primaryDarkColor, primaryColor) else listOf(primaryColor, primaryDarkColor)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 1200)
@Composable
fun ToolsSectionPreview() {
    MaterialTheme {
        Surface {
            ToolsSection()
        }
    }
}