package com.example.ptitjob.ui.screen.candidate.aiService

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIServicesMenu(
    onNavigateToCVEvaluation: () -> Unit,
    onNavigateToInterviewEmulate: () -> Unit,
    onBack: () -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🤖 AI Services",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = PTITTextPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            item {
                // Header info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = (PTITCornerRadius.md),
                    colors = CardDefaults.cardColors(
                        containerColor = PTITPrimary.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(PTITSpacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = PTITSpacing.sm)
                        )
                        Column {
                            Text(
                                text = "Công nghệ AI tiên tiến",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PTITTextPrimary,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hỗ trợ bạn cải thiện CV và kỹ năng phỏng vấn",
                                fontSize = 13.sp,
                                color = PTITTextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            items(aiServices) { service ->
                AIServiceCard(service = service)
            }
        }
    }
}

@Composable
fun AIServiceCard(
    service: AIServiceItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { service.onClick() },
        shape = (PTITCornerRadius.md),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = PTITElevation.sm
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with gradient effect
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip((PTITCornerRadius.md))
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
