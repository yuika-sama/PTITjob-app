package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.component.SalaryCalculator
import com.example.ptitjob.ui.component.SalaryResultDisplay
import com.example.ptitjob.ui.screen.candidate.utilities.models.SalaryCalculationResult
import com.example.ptitjob.ui.theme.*

// --- Component Màn hình Chính ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryCalculatorScreen(
    onBack: () -> Unit = {}
) {
    // --- State Management cho UI ---
    var tabValue by remember { mutableIntStateOf(0) }
    var calculationResult by remember { mutableStateOf<SalaryCalculationResult?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tính lương Gross - Net",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PTITPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background( PTITBackgroundLight
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(PTITSpacing.md),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                // --- Header Banner ---
                item {
                    SalaryHeaderBanner()
                }

                // --- Calculator Tabs ---
                item {
                    SalaryCalculatorTabs(
                        currentTab = tabValue,
                        onTabChange = {
                            tabValue = it
                            calculationResult = null // Reset kết quả khi chuyển tab
                        }
                    )
                }

                // --- Calculator Section ---
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        SalaryCalculatorSection(
                            calculationType = if (tabValue == 0) "gross-to-net" else "net-to-gross",
                            onCalculate = { calculationResult = it }
                        )
                    }
                }

                // --- Result Section ---
                item {
                    AnimatedVisibility(
                        visible = calculationResult != null,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        calculationResult?.let { result ->
                            SalaryResultSection(
                                result = result,
                                calculationType = if (tabValue == 0) "gross-to-net" else "net-to-gross"
                            )
                        }
                    }
                }

                // --- FAQ Section ---
                item {
                    SalaryFaqSection()
                }

                // --- Footer Note ---
                item {
                    SalaryFooterNote()
                }
            }
        }
    }
}


@Composable
private fun SalaryHeaderBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    listOf(PTITPrimary, PTITSecondary)
                ),
                shape = PTITCornerRadius.lg
            ),
        shape = PTITCornerRadius.lg,
        color = Color.Transparent,
        tonalElevation = PTITElevation.xl
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.xl)
        ) {
            // Decorative elements
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-50).dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.1f))
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconXxl),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Money,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(PTITSize.iconXl)
                        )
                    }
                }

                Text(
                    text = "Tính Lương Gross - Net",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Công cụ tính toán lương chính xác theo quy định thuế thu nhập cá nhân 2025",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(0.9f)
                    ),
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    SalaryInfoChip("Cập nhật 2025", PTITSecondary)
                    SalaryInfoChip("Tính toán chính xác", PTITInfo)
                    SalaryInfoChip("Miễn phí", PTITSuccess)
                }
            }
        }
    }
}

@Composable
private fun SalaryInfoChip(text: String, color: Color) {
    Surface(
        shape = PTITCornerRadius.sm,
        color = color
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(
                horizontal = PTITSpacing.sm,
                vertical = PTITSpacing.xs
            )
        )
    }
}

@Composable
private fun SalaryCalculatorTabs(currentTab: Int, onTabChange: (Int) -> Unit) {
    val tabs = listOf(
        "Gross → Net" to "Từ lương gộp sang thực nhận",
        "Net → Gross" to "Từ lương thực nhận sang gộp"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.md)
        ) {
            tabs.forEachIndexed { index, (title, description) ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = PTITSpacing.xs),
                    shape = PTITCornerRadius.md,
                    color = if (currentTab == index) PTITPrimary else Color.Transparent,
                    onClick = { onTabChange(index) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PTITSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (currentTab == index) Color.White else PTITTextPrimary
                            )
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (currentTab == index) Color.White.copy(0.8f) else PTITTextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SalaryCalculatorSection(
    calculationType: String,
    onCalculate: (SalaryCalculationResult) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.xxxl
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = PTITPrimary,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Nhập thông tin tính lương",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            SalaryCalculator(
                calculationType = calculationType,
                onCalculate = onCalculate
            )
        }
    }
}

@Composable
private fun SalaryResultSection(
    result: SalaryCalculationResult,
    calculationType: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = PTITSuccess.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = PTITSuccess,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Kết quả tính lương",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            SalaryResultDisplay(
                result = result,
                calculationType = calculationType
            )
        }
    }
}

@Composable
private fun SalaryFaqSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = null,
                    tint = PTITWarning,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Câu hỏi thường gặp",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            val faqData = listOf(
                "Thuế thu nhập cá nhân được tính như thế nào?" to "Thuế TNCN được tính theo thang thuế lũy tiến từng phần với các mức từ 5% đến 35% tùy theo mức thu nhập chịu thuế của bạn.",
                "Mức giảm trừ gia cảnh năm 2025 là bao nhiêu?" to "Giảm trừ bản thân: 11.000.000 VND/tháng. Giảm trừ người phụ thuộc: 4.400.000 VND/người/tháng.",
                "Sự khác biệt giữa lương Gross và Net?" to "Lương Gross là lương trước thuế và bảo hiểm. Lương Net là lương thực nhận sau khi đã trừ các khoản thuế, bảo hiểm xã hội, bảo hiểm y tế và bảo hiểm thất nghiệp."
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                faqData.forEach { (question, answer) ->
                    SalaryFaqAccordion(question = question, answer = answer)
                }
            }
        }
    }
}

@Composable
private fun SalaryFaqAccordion(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "salary_faq_rotation"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        color = PTITWarning.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.xs
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(PTITSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Mở rộng",
                    tint = PTITWarning,
                    modifier = Modifier
                        .size(PTITSize.iconMd)
                        .rotate(rotationAngle)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        ),
                        modifier = Modifier.padding(PTITSpacing.md)
                    )
                }
            }
        }
    }
}

@Composable
private fun SalaryFooterNote() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = PTITInfo.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = "Lưu ý quan trọng",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            Text(
                buildAnnotatedString {
                    append("Kết quả tính toán chỉ mang tính chất ")
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = PTITInfo
                    )) {
                        append("tham khảo")
                    }
                    append(". Mức lương và thuế thực tế có thể khác nhau tùy theo chính sách của từng công ty và các quy định pháp luật hiện hành.")
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 1200, heightDp = 2000)
@Composable
fun SalaryCalculatorScreenPreview() {
    MaterialTheme {
        SalaryCalculatorScreen()
    }
}