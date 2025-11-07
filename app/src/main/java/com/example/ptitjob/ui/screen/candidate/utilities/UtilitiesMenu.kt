package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptitjob.ui.theme.*

data class UtilityItem(
    val title: String,
    val description: String,
    val icon: String,
    val color: Color,
    val onClick: () -> Unit
)

// --- Route Component for ViewModel Integration ---
@Composable
fun UtilitiesScreenRoute(
    onNavigateToCalculator: (String) -> Unit,
    onNavigateToAIService: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: UtilitiesViewModel = hiltViewModel()
) {
    UtilitiesMenu(
        onNavigateToBHXH = { onNavigateToCalculator("bhxh_calculator") },
        onNavigateToPersonalIncomeTax = { onNavigateToCalculator("tax_calculator") },
        onNavigateToSalaryCalculator = { onNavigateToCalculator("salary_calculator") },
        onNavigateToUnemploymentInsurance = { onNavigateToCalculator("unemployment_calculator") },
        onNavigateToCompoundInterest = { onNavigateToCalculator("compound_interest") },
        onNavigateToCareerFair3D = { onNavigateToCalculator("career_fair_3d") },
        onNavigateToCVEvaluation = { onNavigateToAIService("cv_evaluation") },
        onNavigateToInterviewEmulate = { onNavigateToAIService("interview_emulate") },
        onBack = onBack
    )
}

/**
 * Menu chính cho Utilities/Calculators
 * Hiển thị tất cả các công cụ tính toán và tiện ích cho candidate
 * Bao gồm cả các tính năng AI và trải nghiệm 3D
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilitiesMenu(
    onNavigateToBHXH: () -> Unit,
    onNavigateToPersonalIncomeTax: () -> Unit,
    onNavigateToSalaryCalculator: () -> Unit,
    onNavigateToUnemploymentInsurance: () -> Unit,
    onNavigateToCompoundInterest: () -> Unit,
    onNavigateToCareerFair3D: () -> Unit,
    onNavigateToCVEvaluation: () -> Unit = {},
    onNavigateToInterviewEmulate: () -> Unit = {},
    onBack: () -> Unit
) {
    val utilities = listOf(
        // Tính năng AI Services
        UtilityItem(
            title = "🤖 Đánh giá CV",
            description = "Sử dụng AI để phân tích và đưa ra nhận xét về CV của bạn",
            icon = "📄",
            color = PTITSecondary,
            onClick = onNavigateToCVEvaluation
        ),
        UtilityItem(
            title = "🎤 Mô phỏng phỏng vấn",
            description = "Luyện tập phỏng vấn với AI và nhận phản hồi",
            icon = "�",
            color = PTITPrimary,
            onClick = onNavigateToInterviewEmulate
        ),
        
        // Tính năng 3D
        UtilityItem(
            title = "🌐 Sảnh việc làm 3D",
            description = "Trải nghiệm hội chợ nghề nghiệp ảo với dữ liệu cố định",
            icon = "🧭",
            color = PTITPrimaryDark,
            onClick = onNavigateToCareerFair3D
        ),
        
        // Các công cụ tính toán
        UtilityItem(
            title = "�📋 Tính BHXH",
            description = "Tính toán bảo hiểm xã hội, bảo hiểm y tế",
            icon = "🏥",
            color = PTITInfo,
            onClick = onNavigateToBHXH
        ),
        UtilityItem(
            title = "💰 Thuế thu nhập cá nhân",
            description = "Tính thuế TNCN và thu nhập thực nhận",
            icon = "📊",
            color = PTITWarning,
            onClick = onNavigateToPersonalIncomeTax
        ),
        UtilityItem(
            title = "💵 Tính lương NET",
            description = "Tính toán lương thực lĩnh từ lương GROSS",
            icon = "💸",
            color = PTITSuccess,
            onClick = onNavigateToSalaryCalculator
        ),
        UtilityItem(
            title = "🛡️ Bảo hiểm thất nghiệp",
            description = "Tính toán trợ cấp thất nghiệp",
            icon = "🤝",
            color = PTITSecondary.copy(alpha = 0.8f),
            onClick = onNavigateToUnemploymentInsurance
        ),
        UtilityItem(
            title = "📈 Lãi suất kép",
            description = "Tính toán lãi suất kép và đầu tư",
            icon = "💹",
            color = PTITPrimary.copy(alpha = 0.9f),
            onClick = onNavigateToCompoundInterest
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🧮 Công cụ & Tiện ích",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PTITSurfaceLight,
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
                Text(
                    text = "Tổng hợp các công cụ AI, tính toán tài chính và trải nghiệm ảo giúp bạn trong hành trình tìm việc",
                    fontSize = 14.sp,
                    color = PTITTextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = PTITSpacing.sm)
                )
            }

            items(utilities) { utility ->
                UtilityCard(utility = utility)
            }
        }
    }
}

@Composable
fun UtilityCard(
    utility: UtilityItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { utility.onClick() },
        shape = (PTITCornerRadius.md),
        colors = CardDefaults.cardColors(
            containerColor = PTITSurfaceLight
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
            // Icon container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip((PTITCornerRadius.md))
                    .background(utility.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = utility.icon,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(PTITSpacing.md))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = utility.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PTITTextPrimary,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = utility.description,
                    fontSize = 13.sp,
                    color = PTITTextSecondary,
                    lineHeight = 18.sp
                )
            }

            // Arrow indicator
            Text(
                text = "→",
                fontSize = 20.sp,
                color = PTITTextSecondary
            )
        }
    }
}
