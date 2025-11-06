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
    onBack: () -> Unit,
    viewModel: UtilitiesViewModel = hiltViewModel()
) {
    UtilitiesMenu(
        onNavigateToBHXH = { onNavigateToCalculator("bhxh_calculator") },
        onNavigateToPersonalIncomeTax = { onNavigateToCalculator("tax_calculator") },
        onNavigateToSalaryCalculator = { onNavigateToCalculator("salary_calculator") },
        onNavigateToUnemploymentInsurance = { onNavigateToCalculator("unemployment_calculator") },
        onNavigateToCompoundInterest = { onNavigateToCalculator("compound_interest") },
        onBack = onBack
    )
}

/**
 * Menu chính cho Utilities/Calculators
 * Hiển thị tất cả các công cụ tính toán cho candidate
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilitiesMenu(
    onNavigateToBHXH: () -> Unit,
    onNavigateToPersonalIncomeTax: () -> Unit,
    onNavigateToSalaryCalculator: () -> Unit,
    onNavigateToUnemploymentInsurance: () -> Unit,
    onNavigateToCompoundInterest: () -> Unit,
    onBack: () -> Unit
) {
    val utilities = listOf(
        UtilityItem(
            title = "📋 Tính BHXH",
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
            color = PTITSecondary,
            onClick = onNavigateToUnemploymentInsurance
        ),
        UtilityItem(
            title = "📈 Lãi suất kép",
            description = "Tính toán lãi suất kép và đầu tư",
            icon = "💹",
            color = PTITPrimary,
            onClick = onNavigateToCompoundInterest
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🧮 Công cụ hỗ trợ",
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
                    text = "Các công cụ tính toán giúp bạn lập kế hoạch tài chính tốt hơn",
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
