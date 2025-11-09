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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ptitjob.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

data class TaxCalculationResult(
    val grossIncome: Double,
    val personalDeduction: Double,
    val dependentDeduction: Double,
    val totalDeduction: Double,
    val taxableIncome: Double,
    val tax: Double,
    val netIncome: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalIncomeTaxScreen(
    onBack: () -> Unit = {}
) {
    var currentTab by remember { mutableIntStateOf(0) }
    var calculationResult by remember { mutableStateOf<TaxCalculationResult?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PTITError,
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
                .background(PTITBackgroundLight)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(PTITSpacing.md),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                item { TaxHeaderBanner() }
                item {TaxRegulationInfo()}
                item {
                    TaxNavigationTabs(
                        currentTab = currentTab,
                        onTabChange = { currentTab = it }
                    )
                }
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

@Composable
private fun TaxHeaderBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PTITPrimary,
                            PTITSecondary
                        )
                    )
                )
                .padding(PTITSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Card(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = PTITError,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = "Công cụ tính Thuế thu nhập cá nhân chuẩn 2025",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun TaxRegulationInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.1f)), // Dùng màu nền nhạt
        border = BorderStroke(1.dp, PTITInfo.copy(alpha = 0.3f)) // Thêm viền nhẹ
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
        ) {
            Text(
                text = "Áp dụng quy định:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            Text(
                text = "• Giảm trừ gia cảnh bản thân: 11,000,000 VNĐ/tháng.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary,
                    lineHeight = 18.sp
                )
            )
            Text(
                text = "• Giảm trừ người phụ thuộc: 4,400,000 VNĐ/người/tháng.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary,
                    lineHeight = 18.sp
                )
            )
        }
    }
}


@Composable
private fun TaxNavigationTabs(currentTab: Int, onTabChange: (Int) -> Unit) {
    val tabs = listOf(
        "Máy tính thuế" to Icons.Default.Calculate,
        "Bảng thuế suất" to Icons.Default.Info,
        "Hướng dẫn & FAQ" to Icons.Default.School
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(PTITSpacing.sm)) {
            tabs.forEachIndexed { index, (title, icon) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentTab == index) PTITError else Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (currentTab == index) 2.dp else 0.dp
                    ),
                    onClick = { onTabChange(index) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = PTITSpacing.sm,
                                vertical = PTITSpacing.sm
                            ),
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (currentTab == index) Color.White else PTITTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == index) Color.White else PTITTextPrimary,
                                fontSize = 13.sp
                            ),
                            maxLines = 1
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
    var grossIncome by remember { mutableStateOf("") }
    var isInsuranceAbove by remember { mutableStateOf(true) }
    var dependentsCount by remember { mutableStateOf("0") }

    Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.lg,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = PTITError,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Nhập thông tin tính thuế",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                }

                // Income Input
                Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)) {
                    Text(
                        text = "Thu Nhập (Gross)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITTextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = grossIncome,
                        onValueChange = { grossIncome = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Thu nhập", fontSize = 14.sp) },
                        suffix = { Text("VNĐ", fontSize = 14.sp, color = PTITTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PTITError,
                            unfocusedBorderColor = PTITNeutral300
                        )
                    )
                }

                // Insurance Radio
                Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)) {
                    Text(
                        text = "Mức lương đóng bảo hiểm",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITTextPrimary
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isInsuranceAbove = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                        ) {
                            RadioButton(
                                selected = isInsuranceAbove,
                                onClick = { isInsuranceAbove = true },
                                colors = RadioButtonDefaults.colors(selectedColor = PTITError)
                            )
                            Text(
                                text = "Trên lương chính thức",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isInsuranceAbove = false },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                        ) {
                            RadioButton(
                                selected = !isInsuranceAbove,
                                onClick = { isInsuranceAbove = false },
                                colors = RadioButtonDefaults.colors(selectedColor = PTITError)
                            )
                            Text(
                                text = "Khác",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                            )
                        }
                    }
                }

                // Dependents Input
                Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)) {
                    Text(
                        text = "Số người phụ thuộc",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITTextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = dependentsCount,
                        onValueChange = { dependentsCount = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Số người", fontSize = 14.sp) },
                        suffix = { Text("Người", fontSize = 14.sp, color = PTITTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PTITError,
                            unfocusedBorderColor = PTITNeutral300
                        )
                    )
                }

                // Calculate Button
                Button(
                    onClick = {
                        val income = grossIncome.toDoubleOrNull() ?: 0.0
                        val deps = dependentsCount.toIntOrNull() ?: 0
                        val result = calculateTax(income, deps)
                        onCalculate(result)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PTITError),
                    contentPadding = PaddingValues(vertical = PTITSpacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(PTITSpacing.xs))
                    Text(
                        text = "Tính thuế",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Result Section
        result?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.lg,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = PTITSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Kết quả tính toán",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITTextPrimary
                            )
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = PTITCornerRadius.md,
                        colors = CardDefaults.cardColors(
                            containerColor = PTITError.copy(alpha = 0.05f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(PTITSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                        ) {
                            ResultRow(
                                label = "Giảm trừ bản thân",
                                value = formatCurrency(it.personalDeduction),
                                color = PTITTextSecondary
                            )
                            ResultRow(
                                label = "Giảm trừ gia cảnh",
                                value = formatCurrency(it.dependentDeduction),
                                color = PTITTextSecondary
                            )
                            HorizontalDivider(color = PTITNeutral300)
                            ResultRow(
                                label = "Thu nhập chịu thuế",
                                value = formatCurrency(it.taxableIncome),
                                color = PTITWarning,
                                bold = true
                            )
                            ResultRow(
                                label = "Thuế TNCN phải nộp",
                                value = formatCurrency(it.tax),
                                color = PTITError,
                                bold = true,
                                large = true
                            )
                            HorizontalDivider(color = PTITNeutral300)
                            ResultRow(
                                label = "Thu nhập thực nhận",
                                value = formatCurrency(it.netIncome),
                                color = PTITSuccess,
                                bold = true,
                                large = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    color: Color,
    bold: Boolean = false,
    large: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = if (large) 14.sp else 13.sp,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                color = PTITTextPrimary
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = if (large) 16.sp else 14.sp,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
                color = color
            )
        )
    }
}

@Composable
private fun TaxBracketsTab() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PTITError,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Bảng thuế suất TNCN 2025",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            val taxBracketInfo = getTaxBracketInfo()
            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
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
        level <= 2 -> PTITSuccess
        level <= 4 -> PTITWarning
        else -> PTITError
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bậc ${bracket["level"]}: ${bracket["range"]}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    ),
                    modifier = Modifier.weight(1f, fill = false)
                )

                Surface(
                    shape = PTITCornerRadius.sm,
                    color = color
                ) {
                    Text(
                        text = bracket["rate"].toString(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(
                            horizontal = PTITSpacing.sm,
                            vertical = PTITSpacing.xs
                        )
                    )
                }
            }

            Text(
                text = bracket["description"].toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITTextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun TaxFaqTab() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Hướng dẫn và câu hỏi thường gặp",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            TaxFormulaCard()

            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
                Text(
                    text = "Câu hỏi thường gặp:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    ),
                    modifier = Modifier.padding(top = PTITSpacing.sm)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        colors = CardDefaults.cardColors(
            containerColor = PTITInfo.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Functions,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Công thức tính thuế",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.sm,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Thu nhập chịu thuế = Thu nhập - Giảm trừ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITInfo
                        )
                    )
                    Text(
                        text = "Thuế TNCN = Thu nhập chịu thuế × Thuế suất",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITInfo
                        )
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "• Giảm trừ bản thân: 11 triệu/tháng",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PTITTextSecondary,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "• Giảm trừ phụ thuộc: 4.4 triệu/người",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PTITTextSecondary,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "• Thuế suất: 5% - 35% (lũy tiến)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PTITTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun TaxFaqAccordion(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "faq_rotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        colors = CardDefaults.cardColors(containerColor = PTITNeutral50),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Mở rộng",
                    tint = PTITTextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotationAngle)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(
                        start = PTITSpacing.md,
                        end = PTITSpacing.md,
                        bottom = PTITSpacing.md
                    )
                ) {
                    HorizontalDivider(color = PTITNeutral300, thickness = 0.5.dp)
                    Spacer(Modifier.height(PTITSpacing.sm))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PTITTextSecondary
                        )
                    )
                }
            }
        }
    }
}

// Helper Functions
private fun calculateTax(grossIncome: Double, dependentsCount: Int): TaxCalculationResult {
    val personalDeduction = 11_000_000.0
    val dependentDeduction = dependentsCount * 4_400_000.0
    val totalDeduction = personalDeduction + dependentDeduction
    val taxableIncome = maxOf(0.0, grossIncome - totalDeduction)

    val tax = when {
        taxableIncome <= 5_000_000 -> taxableIncome * 0.05
        taxableIncome <= 10_000_000 -> 5_000_000 * 0.05 + (taxableIncome - 5_000_000) * 0.10
        taxableIncome <= 18_000_000 -> 5_000_000 * 0.05 + 5_000_000 * 0.10 + (taxableIncome - 10_000_000) * 0.15
        taxableIncome <= 32_000_000 -> 5_000_000 * 0.05 + 5_000_000 * 0.10 + 8_000_000 * 0.15 + (taxableIncome - 18_000_000) * 0.20
        taxableIncome <= 52_000_000 -> 5_000_000 * 0.05 + 5_000_000 * 0.10 + 8_000_000 * 0.15 + 14_000_000 * 0.20 + (taxableIncome - 32_000_000) * 0.25
        taxableIncome <= 80_000_000 -> 5_000_000 * 0.05 + 5_000_000 * 0.10 + 8_000_000 * 0.15 + 14_000_000 * 0.20 + 20_000_000 * 0.25 + (taxableIncome - 52_000_000) * 0.30
        else -> 5_000_000 * 0.05 + 5_000_000 * 0.10 + 8_000_000 * 0.15 + 14_000_000 * 0.20 + 20_000_000 * 0.25 + 28_000_000 * 0.30 + (taxableIncome - 80_000_000) * 0.35
    }

    val netIncome = grossIncome - tax

    return TaxCalculationResult(
        grossIncome = grossIncome,
        personalDeduction = personalDeduction,
        dependentDeduction = dependentDeduction,
        totalDeduction = totalDeduction,
        taxableIncome = taxableIncome,
        tax = tax,
        netIncome = netIncome
    )
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(amount)} đ"
}

private fun getFaqData() = listOf(
    "Thuế thu nhập cá nhân là gì?" to "Thuế TNCN là khoản tiền mà người có thu nhập phải nộp cho nhà nước, được tính dựa trên thu nhập chịu thuế sau khi trừ các khoản giảm trừ.",
    "Mức lương bao nhiêu phải nộp thuế?" to "Nếu thu nhập sau khi trừ giảm trừ bản thân (11 triệu/tháng) và người phụ thuộc (4.4 triệu/người) lớn hơn 0 thì phải nộp thuế.",
    "Giảm trừ gia cảnh là gì?" to "Là khoản được trừ vào thu nhập trước khi tính thuế, gồm giảm trừ bản thân (11 triệu/tháng) và giảm trừ người phụ thuộc (4.4 triệu/người/tháng).",
    "Thuế suất lũy tiến là gì?" to "Là thuế suất tăng dần theo bậc thu nhập. Thu nhập càng cao thì phần vượt sẽ chịu thuế suất cao hơn, từ 5% đến 35%.",
    "Ai là người phụ thuộc?" to "Người phụ thuộc bao gồm con dưới 18 tuổi, người thân không có thu nhập hoặc thu nhập dưới 1 triệu/tháng và được đăng ký hợp lệ."
)

private fun getTaxBracketInfo() = listOf(
    mapOf("level" to 1, "range" to "Đến 5 triệu", "rate" to "5%", "description" to "Thu nhập chịu thuế đến 5 triệu đồng"),
    mapOf("level" to 2, "range" to "Trên 5 - 10 triệu", "rate" to "10%", "description" to "Thu nhập chịu thuế từ trên 5 đến 10 triệu đồng"),
    mapOf("level" to 3, "range" to "Trên 10 - 18 triệu", "rate" to "15%", "description" to "Thu nhập chịu thuế từ trên 10 đến 18 triệu đồng"),
    mapOf("level" to 4, "range" to "Trên 18 - 32 triệu", "rate" to "20%", "description" to "Thu nhập chịu thuế từ trên 18 đến 32 triệu đồng"),
    mapOf("level" to 5, "range" to "Trên 32 - 52 triệu", "rate" to "25%", "description" to "Thu nhập chịu thuế từ trên 32 đến 52 triệu đồng"),
    mapOf("level" to 6, "range" to "Trên 52 - 80 triệu", "rate" to "30%", "description" to "Thu nhập chịu thuế từ trên 52 đến 80 triệu đồng"),
    mapOf("level" to 7, "range" to "Trên 80 triệu", "rate" to "35%", "description" to "Thu nhập chịu thuế trên 80 triệu đồng")
)

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PersonalIncomeTaxScreenPreview() {
    MaterialTheme {
        PersonalIncomeTaxScreen()
    }
}