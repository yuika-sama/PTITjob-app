package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.example.ptitjob.ui.component.CompoundInterestCalculator
import com.example.ptitjob.ui.component.CompoundInterestResult
import com.example.ptitjob.ui.component.CompoundInterestResultDisplay
import com.example.ptitjob.ui.theme.*

// --- Component Màn hình Chính ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestScreen(
    onBack: () -> Unit = {}
) {
    var tabValue by remember { mutableIntStateOf(0) }
    var calculationResult by remember { mutableStateOf<CompoundInterestResult?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tính lãi suất kép",
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
                // Header
                item { CompoundInterestHeaderSection() }

                // Tabs
                item {
                    CompoundInterestTabs(
                        currentTab = tabValue,
                        onTabChange = { tabValue = it }
                    )
                }

                // Tab content
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        when (tabValue) {
                            0 -> CompoundInterestCalculatorTab(
                                result = calculationResult,
                                onCalculate = { calculationResult = it }
                            )
                            1 -> CompoundInterestFormulaTab()
                            2 -> CompoundInterestFaqTab()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompoundInterestHeaderSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.md
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
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
                color = Color.Transparent,
                tonalElevation = PTITElevation.md
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(PTITSize.iconXl)
                    )
                }
            }

            Text(
                text = "Công cụ tính Lãi suất kép",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Công cụ miễn phí giúp bạn tính toán đầu tư và tiết kiệm hiệu quả",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextSecondary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompoundInterestTabs(currentTab: Int, onTabChange: (Int) -> Unit) {
    val tabs = listOf(
        "Máy tính lãi kép" to Icons.Default.Calculate,
        "Công thức tính toán" to Icons.Default.Functions,
        "Câu hỏi thường gặp" to Icons.Default.Help
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(modifier = Modifier.padding(PTITSpacing.md)) {
            tabs.forEachIndexed { index, (title, icon) ->
                Card( // ✅ Card hỗ trợ onClick ổn định
                    onClick = { onTabChange(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = PTITSpacing.xs),
                    shape = PTITCornerRadius.md,
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentTab == index) PTITPrimary else Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
private fun CompoundInterestCalculatorTab(
    result: CompoundInterestResult?,
    onCalculate: (CompoundInterestResult) -> Unit
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
                        text = "Nhập thông tin tính toán",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                }

                // Giả sử composable này tự quản lý state nội bộ và hiển thị kết quả qua callback
                CompoundInterestCalculator()
            }
        }

        // Result Section
        AnimatedVisibility(
            visible = result != null,
            enter = slideInVertically() + fadeIn()
        ) {
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
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = PTITSuccess,
                                modifier = Modifier.size(PTITSize.iconMd)
                            )
                            Text(
                                text = "Kết quả tính toán",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PTITTextPrimary
                                )
                            )
                        }

                        CompoundInterestResultDisplay(result = it)
                    }
                }
            }
        }
    }
}

@Composable
private fun CompoundInterestFormulaTab() {
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
                    imageVector = Icons.Default.Functions,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Công thức tính lãi suất kép",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            FormulaCard(
                title = "Công thức cơ bản",
                formula = "A = P(1 + r/n)^(nt)",
                description = "Trong đó:\n• A = Số tiền cuối kỳ\n• P = Số tiền gốc ban đầu\n• r = Lãi suất hàng năm (%)\n• n = Số lần ghép lãi trong năm\n• t = Số năm đầu tư",
                icon = Icons.Default.Calculate
            )

            FormulaCard(
                title = "Ví dụ thực tế",
                formula = "Đầu tư 100 triệu, lãi suất 8%/năm, 10 năm",
                description = "A = 100,000,000 × (1 + 0.08/1)^(1×10)\nA = 100,000,000 × (1.08)^10\nA ≈ 215,892,500 VNĐ\n\nLợi nhuận ≈ 115,892,500 VNĐ",
                icon = Icons.Default.Calculate
            )
        }
    }
}

@Composable
private fun CompoundInterestFaqTab() {
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

            FaqAccordion(
                question = "Mức lãi suất là bao nhiêu?",
                answer = "Lãi suất phụ thuộc vào loại hình đầu tư:\n• Tiết kiệm ngân hàng: 4-6%/năm\n• Trái phiếu chính phủ: 2-4%/năm\n• Cổ phiếu: 8-12%/năm (trung bình)\n• Quỹ đầu tư: 6-10%/năm"
            )

            FaqAccordion(
                question = "Làm thế nào để tận dụng sức mạnh của lãi kép?",
                answer = "Để tận dụng lãi kép hiệu quả:\n• Bắt đầu đầu tư sớm nhất có thể\n• Đầu tư đều đặn hàng tháng\n• Tái đầu tư lợi nhuận thay vì rút ra\n• Kiên nhẫn dài hạn (ít nhất 5-10 năm)\n• Chọn kênh đầu tư phù hợp với khẩu vị rủi ro"
            )

            FaqAccordion(
                question = "Tần suất ghép lãi ảnh hưởng như thế nào?",
                answer = "Tần suất ghép lãi càng cao thì lợi nhuận càng lớn:\n• Hàng năm < Hàng quý < Hàng tháng < Hàng ngày\nSự chênh lệch không quá lớn ở mức lãi suất thông thường, nhưng vẫn có ý nghĩa trong dài hạn."
            )
        }
    }
}

@Composable
fun FormulaCard(
    title: String,
    formula: String,
    description: String,
    icon: ImageVector
) {
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
        color = Color.Transparent,
        tonalElevation = PTITElevation.md
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = title,
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
                Text(
                    text = formula,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITInfo
                    ),
                    modifier = Modifier.padding(PTITSpacing.md)
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                )
            )
        }
    }
}

@Composable
fun FaqAccordion(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "expand_icon_rotation"
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        ),
                        modifier = Modifier.padding(PTITSpacing.lg)
                    )
                }
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 1200, heightDp = 1800)
@Composable
fun CompoundInterestScreenPreview() {
    MaterialTheme {
        CompoundInterestScreen()
    }
}
