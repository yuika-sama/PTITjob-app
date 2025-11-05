package com.example.ptitjob.ui.screen.candidate.aiService

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ptitjob.ui.component.PTITScreenContainer
import com.example.ptitjob.ui.theme.*

data class AIServiceItem(
    val title: String,
    val description: String,
    val icon: String,
    val color: Color,
    val badge: String? = null,
    val onClick: () -> Unit
)

/**
 * Menu chính cho AI Services
 * Hiển thị các dịch vụ AI cho candidate
 * Updated to work with new PTIT navbar layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIServicesMenu(
    onNavigateToCVEvaluation: () -> Unit,
    onNavigateToInterviewEmulate: () -> Unit,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val aiServices = listOf(
        AIServiceItem(
            title = "📄 Đánh giá CV",
            description = "AI phân tích và đánh giá CV của bạn, đưa ra gợi ý cải thiện",
            icon = "🤖",
            color = PTITPrimary,
            badge = "AI",
            onClick = onNavigateToCVEvaluation
        ),
        AIServiceItem(
            title = "💬 Mô phỏng phỏng vấn",
            description = "Luyện tập phỏng vấn với AI, nhận phản hồi chi tiết",
            icon = "🎯",
            color = PTITSuccess,
            badge = "AI",
            onClick = onNavigateToInterviewEmulate
        )
    )

    PTITScreenContainer(
        hasGradientBackground = true,
        snackbarHostState = snackbarHostState
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            item {
                // Header info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = CardDefaults.cardColors(
                        containerColor = PTITPrimary.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(PTITSpacing.md)
                    ) {
                        Text(
                            text = "🚀 Dịch vụ AI thông minh",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = PTITPrimary
                        )
                        Spacer(modifier = Modifier.height(PTITSpacing.xs))
                        Text(
                            text = "Sử dụng trí tuệ nhân tạo để nâng cao hiệu quả tìm việc và phát triển nghề nghiệp của bạn",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PTITTextSecondary
                        )
                    }
                }
            }

            items(aiServices) { service ->
                AIServiceCard(
                    service = service
                )
            }

            item {
                // Footer info
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = PTITCornerRadius.md,
                    colors = CardDefaults.cardColors(
                        containerColor = PTITSuccess.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(PTITSpacing.md)
                    ) {
                        Text(
                            text = "💡 Mẹo sử dụng",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PTITSuccess
                        )
                        Spacer(modifier = Modifier.height(PTITSpacing.xs))
                        Text(
                            text = "• Chuẩn bị CV ở định dạng PDF để có kết quả đánh giá tốt nhất\n• Luyện tập phỏng vấn thường xuyên để tự tin hơn\n• Áp dụng gợi ý của AI để cải thiện hồ sơ",
                            style = MaterialTheme.typography.bodySmall,
                            color = PTITTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIServiceCard(
    service: AIServiceItem,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier
        .fillMaxWidth(), shape = PTITCornerRadius.md, colors = CardDefaults.cardColors(
        containerColor = Color.White
    ), elevation = CardDefaults.cardElevation(
        defaultElevation = PTITElevation.sm
    ), onClick = service.onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with gradient effect
            Box(
                modifier = Modifier.size(60.dp).clip(PTITCornerRadius.md)
                    .background(service.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = service.icon,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(PTITSpacing.md))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary,
                        lineHeight = 22.sp
                    )
                    service.badge?.let { badge ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = service.color.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = service.color,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = service.description,
                    fontSize = 13.sp,
                    color = PTITTextSecondary,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.width(PTITSpacing.sm))

            // Arrow indicator
            Text(
                text = "→",
                fontSize = 24.sp,
                color = PTITTextSecondary
            )
        }
    }
}
