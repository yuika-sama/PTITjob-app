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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.component.TaxCalculationResult
import com.example.ptitjob.ui.component.TaxCalculator
import com.example.ptitjob.ui.component.TaxResultDisplay
import com.example.ptitjob.ui.theme.*

// Giả sử các component TaxCalculator, TaxResultDisplay và các data class tương ứng đã tồn tại

// --- Component Màn hình Chính ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalIncomeTaxScreen(
    onBack: () -> Unit = {}
) {
    // --- State Management cho UI ---
    var currentTab by remember { mutableIntStateOf(0) }
    var calculationResult by remember { mutableStateOf<TaxCalculationResult?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tính thuế TNCN",
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PTITGradientStart,
                            PTITGradientMiddle,
                            PTITGradientEnd
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(PTITSpacing.md),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                // --- Header Banner ---
                item {
                    TaxHeaderBanner()
                }

                // --- Navigation Tabs ---
                item {
                    TaxNavigationTabs(
                        currentTab = currentTab,
                        onTabChange = { currentTab = it }
                    )
                }

                // --- Content based on selected tab ---
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        when (currentTab) {
                            0 -> TaxCalculatorTab(
                                result = calculationResult,
                                onCalculate = { calculationResult = it }
                            )
                            1 -> TaxBracketsTab()
                            2 -> TaxFaqTab()
                        }
                    }
                }
            }
        }
    }
}


// --- Các Composable con và Section ---

@Composable
private fun TaxHeaderBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(PTITPrimary, PTITSecondary)
                ),
                shape = PTITCornerRadius.lg
            ),
        shape = PTITCornerRadius.lg,
        color = Color.Transparent,     // để không che gradient phía dưới
        tonalElevation = PTITElevation.md
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
                    .offset(x = 20.dp, y = (-20).dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.1f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-30).dp, y = 30.dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.05f))
            )

            Column(
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
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(PTITSize.iconXl)
                        )
                    }
                }
                
                Text(
                    text = "Công cụ tính Thuế thu nhập cá nhân",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Chuẩn 2025",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White.copy(0.9f),
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Tính toán chính xác theo quy định mới nhất của Bộ Tài chính",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(0.8f)
                    ),
                    textAlign = TextAlign.Center
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    TaxInfoChip("Cập nhật 2025")
                    TaxInfoChip("Miễn phí")
                    TaxInfoChip("Chính xác 100%")
                }
            }
        }
    }
}

@Composable
private fun TaxInfoChip(text: String) {
    Surface(
        shape = PTITCornerRadius.sm,
        color = Color.White.copy(0.2f)
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
private fun TaxNavigationTabs(currentTab: Int, onTabChange: (Int) -> Unit) {
    val tabs = listOf(
        "Máy tính thuế" to Icons.Default.Calculate,
        "Bảng thuế suất" to Icons.Default.Info,
        "Hướng dẫn & FAQ" to Icons.Default.School
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
            tabs.forEachIndexed { index, (title, icon) ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = PTITSpacing.xs),
                    shape = PTITCornerRadius.md,
                    color = if (currentTab == index) PTITPrimary else Color.Transparent,
                    onClick = { onTabChange(index) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PTITSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (currentTab == index) Color.White else PTITTextSecondary,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (currentTab == index) Color.White else PTITTextPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxCalculatorTab(
    result: TaxCalculationResult?, 
    onCalculate: (TaxCalculationResult) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        // Calculator Section
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
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = PTITPrimary,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                    Text(
                        text = "Nhập thông tin tính thuế",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                }
                
                TaxCalculator()
            }
        }

        // Result Section
        result?.let {
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
                            text = "Kết quả tính thuế",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITTextPrimary
                            )
                        )
                    }
                    
                    TaxResultDisplay(result = it)
                }
            }
        }
    }
}

@Composable
private fun TaxBracketsTab() {
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
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PTITError,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Bảng thuế suất thuế thu nhập cá nhân 2025",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            
            val taxBracketInfo = getTaxBracketInfo()
            Column(
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                taxBracketInfo.forEach { bracket ->
                    TaxBracketCard(bracket = bracket)
                }
            }
        }
    }
}

@Composable
private fun TaxBracketCard(bracket: Map<String, Any>) {
    val level = bracket["level"] as Int
    val color = when {
        level <= 2 -> PTITPrimary
        level <= 4 -> PTITSecondary
        else -> PTITError
    }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        color = color.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.xs
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
            ) {
                Text(
                    text = "Bậc ${bracket["level"]}: ${bracket["range"]}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
                Text(
                    text = bracket["description"].toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary
                    )
                )
            }
            
            Surface(
                shape = PTITCornerRadius.sm,
                color = color
            ) {
                Text(
                    text = bracket["rate"].toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(
                        horizontal = PTITSpacing.md,
                        vertical = PTITSpacing.sm
                    )
                )
            }
        }
    }
}

@Composable
private fun TaxFaqTab() {
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
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = PTITWarning,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Hướng dẫn và câu hỏi thường gặp",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            
            // Tax Formula Section
            TaxFormulaCard()
            
            // FAQ Section
            Column(
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Text(
                    text = "Câu hỏi thường gặp:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
                
                val faqData = getFaqData()
                faqData.forEach { (question, answer) ->
                    TaxFaqAccordion(question = question, answer = answer)
                }
            }
        }
    }
}

@Composable
private fun TaxFormulaCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        color = PTITInfo.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Functions,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = "Công thức tính thuế thu nhập cá nhân",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.sm,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                ) {
                    Text(
                        text = "Thu nhập chịu thuế = Thu nhập - Giảm trừ gia cảnh - Giảm trừ bản thân",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITInfo
                        )
                    )
                    Text(
                        text = "Thuế TNCN = Thu nhập chịu thuế × Thuế suất",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITInfo
                        )
                    )
                }
            }
            
            Text(
                text = "• Giảm trừ bản thân: 11,000,000 VNĐ/tháng\n• Giảm trừ người phụ thuộc: 4,400,000 VNĐ/người/tháng\n• Áp dụng thuế suất lũy tiến từ 5% đến 35%",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary
                )
            )
        }
    }
}

@Composable
private fun TaxFaqAccordion(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, 
        label = "tax_faq_rotation"
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
                    .padding(PTITSpacing.lg),
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
                Column(
                    modifier = Modifier.padding(
                        start = PTITSpacing.lg,
                        end = PTITSpacing.lg,
                        bottom = PTITSpacing.lg
                    )
                ) {
                    HorizontalDivider(color = PTITWarning.copy(alpha = 0.3f))
                    Spacer(Modifier.height(PTITSpacing.md))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                }
            }
        }
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 1200, heightDp = 1800)
@Composable
fun PersonalIncomeTaxScreenPreview() {
    MaterialTheme {
        PersonalIncomeTaxScreen()
    }
}


// --- Dữ liệu Mẫu và Placeholder ---
private fun getSampleTaxResult() = TaxCalculationResult(
    30000000,
    11000000,
    4400000,
    15400000,
    11450000,
    867500,
    25982500,
//    emptyList()
)
private fun getFaqData() = listOf(
    "Thuế thu nhập cá nhân là gì?" to "Là khoản tiền người có thu nhập cần trích nộp vào ngân sách nhà nước...",
    "Mức lương bao nhiêu phải nộp thuế?" to "Trên 11 triệu/tháng nếu không có người phụ thuộc...",
)
private fun getTaxBracketInfo() = listOf(
    mapOf("level" to 1, "range" to "Đến 5 triệu", "rate" to "5%", "description" to "Mức thuế thấp nhất"),
    mapOf("level" to 2, "range" to "Trên 5 - 10 triệu", "rate" to "10%", "description" to "Mức thuế cơ bản"),
)