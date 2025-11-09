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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ptitjob.ui.screen.candidate.utilities.calculations.formatCurrency
import com.example.ptitjob.ui.screen.candidate.utilities.models.*
import com.example.ptitjob.ui.theme.*

// --- Route Component with ViewModel Integration ---
@Composable
fun SalaryCalculatorRoute(
    onBack: () -> Unit,
    viewModel: UtilitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.salaryState.collectAsStateWithLifecycle()
    
    SalaryCalculatorScreen(
        uiState = uiState,
        onInputChange = viewModel::updateSalaryInput,
        onCalculate = viewModel::calculateSalary,
        onClearResult = viewModel::clearSalaryResult,
        onBack = onBack
    )
}

// --- Main Screen Component ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryCalculatorScreen(
    uiState: CalculatorUiState<SalaryInput>,
    onInputChange: (SalaryInput) -> Unit,
    onCalculate: () -> Unit,
    onClearResult: () -> Unit,
    onBack: () -> Unit
) {
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
                    SalaryCalculateButton(
                        isLoading = uiState.isLoading,
                        onCalculate = onCalculate
                    )
                }

                // --- Error Display ---
                if (uiState.hasError) {
                    item {
                        ErrorCard(message = uiState.error ?: "Đã xảy ra lỗi")
                    }
                }

                // --- Results ---
                if (uiState.hasResult) {
                    val result = uiState.result as SalaryCalculationResult
                    item {
                        SalaryResultCard(
                            result = result,
                            calculationType = uiState.input.calculationType,
                            onClearResult = onClearResult
                        )
                    }
                }

                // --- Information Section ---
                item {
                    SalaryInformationSection()
                }
            }
        }
    }
}

// --- Calculator Type Selector ---
@Composable
private fun SalaryCalculatorTypeSelector(
    selectedType: SalaryCalculationType,
    onTypeChange: (SalaryCalculationType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md)
        ) {
            Text(
                text = "Chọn loại tính toán",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PTITTextPrimary,
                modifier = Modifier.padding(bottom = PTITSpacing.sm)
            )
            
            SalaryCalculationType.values().forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTypeChange(type) }
                        .padding(vertical = PTITSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == type,
                        onClick = { onTypeChange(type) },
                        colors = RadioButtonDefaults.colors(selectedColor = PTITPrimary)
                    )
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PTITTextPrimary,
                        modifier = Modifier.padding(start = PTITSpacing.xs)
                    )
                }
            }
        }
    }
}

// --- Input Form Component ---
@Composable
private fun SalaryInputForm(
    input: SalaryInput,
    validationErrors: List<String>,
    onInputChange: (SalaryInput) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Text(
                text = "Thông tin đầu vào",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PTITTextPrimary
            )

            // Salary Input
            OutlinedTextField(
                value = input.inputSalary,
                onValueChange = { onInputChange(input.copy(inputSalary = it)) },
                label = { 
                    Text(when (input.calculationType) {
                        SalaryCalculationType.GROSS_TO_NET -> "Lương GROSS (VNĐ)"
                        SalaryCalculationType.NET_TO_GROSS -> "Lương NET mong muốn (VNĐ)"
                    })
                },
                placeholder = { Text("Ví dụ: 15000000") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    focusedLabelColor = PTITPrimary
                )
            )

            // Dependents Input
            OutlinedTextField(
                value = input.dependents,
                onValueChange = { onInputChange(input.copy(dependents = it)) },
                label = { Text("Số người phụ thuộc") },
                placeholder = { Text("0") },
                leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    focusedLabelColor = PTITPrimary
                )
            )

            // Region Selection
            Text(
                text = "Vùng lương tối thiểu",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PTITTextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
            ) {
                (1..4).forEach { region ->
                    FilterChip(
                        selected = input.region == region,
                        onClick = { onInputChange(input.copy(region = region)) },
                        label = { Text("Vùng $region") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Unemployment Insurance Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = input.hasUnemploymentInsurance,
                    onCheckedChange = { onInputChange(input.copy(hasUnemploymentInsurance = it)) },
                    colors = CheckboxDefaults.colors(checkedColor = PTITPrimary)
                )
                Text(
                    text = "Đóng bảo hiểm thất nghiệp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PTITTextPrimary,
                    modifier = Modifier.padding(start = PTITSpacing.xs)
                )
            }

            // Validation Errors
            if (validationErrors.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PTITError.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(PTITSpacing.sm)) {
                        validationErrors.forEach { error ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = PTITError,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PTITError,
                                    modifier = Modifier.padding(start = PTITSpacing.xs)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalaryCalculateButton(
    isLoading: Boolean,
    onCalculate: () -> Unit
) {
    Button(
        onClick = onCalculate,
        enabled = !isLoading,
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
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.sm))
            Text(
                text = "Tính toán",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Result Card Component ---
@Composable
private fun SalaryResultCard(
    result: SalaryCalculationResult,
    calculationType: SalaryCalculationType,
    onClearResult: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITSuccess.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.md)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kết quả tính toán",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITSuccess
                )
                Row {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = PTITSuccess
                        )
                    }
                    IconButton(onClick = onClearResult) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Xóa kết quả",
                            tint = PTITTextSecondary
                        )
                    }
                }

                Text(
                    text = "Tính Lương Gross - Net",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Công cụ tính toán lương chính xác theo quy định thuế thu nhập cá nhân 2025",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black.copy(0.9f)
                    ),
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    Divider(color = PTITSuccess.copy(alpha = 0.3f))

                    // Main Results
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Lương GROSS",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.grossSalary),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PTITPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Lương NET",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.netSalary),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess
                            )
                        }
                    }

                    Divider(color = PTITNeutral200)

                    // Breakdown
                    Text(
                        text = "Chi tiết khấu trừ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )

                    SalaryBreakdownItem("BHXH (8%)", result.socialInsurance)
                    SalaryBreakdownItem("BHYT (1.5%)", result.healthInsurance)
                    if (result.unemploymentInsurance > 0) {
                        SalaryBreakdownItem("BHTN (1%)", result.unemploymentInsurance)
                    }
                    SalaryBreakdownItem("Thuế TNCN", result.personalIncomeTax)
                    
                    Divider(color = PTITNeutral200)
                    
                    SalaryBreakdownItem(
                        "Tổng khấu trừ", 
                        result.totalDeductions,
                        isTotal = true
                    )
                }
            }
        }
    }
}

@Composable
private fun SalaryBreakdownItem(
    label: String,
    amount: Long,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = PTITTextPrimary
        )
        Text(
            text = formatCurrency(amount),
            style = if (isTotal) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) PTITError else PTITTextSecondary
        )
    }
}

// --- Error Card Component ---
@Composable
private fun SalaryCalculatorTabs(currentTab: Int, onTabChange: (Int) -> Unit) {
    val tabs = listOf(
        "Gross → Net" to "Từ lương gộp sang thực nhận",
        "Net → Gross" to "Từ lương thực nhận sang gộp"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITError.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically
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

// --- Information Section ---
@Composable
private fun SalaryInformationSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Thông tin tham khảo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITInfo,
                    modifier = Modifier.padding(start = PTITSpacing.sm)
                )
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("• Tỷ lệ đóng bảo hiểm 2025:\n")
                    }
                    append("  - BHXH: 8% lương đóng bảo hiểm\n")
                    append("  - BHYT: 1.5% lương đóng bảo hiểm\n")
                    append("  - BHTN: 1% lương đóng bảo hiểm\n\n")
                    
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("• Giảm trừ thuế TNCN:\n")
                    }
                    append("  - Bản thân: 11.000.000 VNĐ/tháng\n")
                    append("  - Người phụ thuộc: 4.400.000 VNĐ/người/tháng\n\n")
                    
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("• Lưu ý: ")
                    }
                    append("Kết quả tính toán chỉ mang tính chất tham khảo")
                },
                style = MaterialTheme.typography.bodySmall,
                color = PTITTextSecondary
            )
        }
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 1200, heightDp = 2000)
@Composable
fun SalaryCalculatorScreenPreview() {
    MaterialTheme {
        // Preview with some default state
        SalaryCalculatorScreen(
            uiState = CalculatorUiState(input = SalaryInput()),
            onInputChange = {},
            onCalculate = {},
            onClearResult = {},
            onBack = {}
        )
    }
}