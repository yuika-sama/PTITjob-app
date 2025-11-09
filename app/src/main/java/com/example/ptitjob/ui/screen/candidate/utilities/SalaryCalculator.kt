package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

/* -------------------- Route (VM) -------------------- */

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

/* -------------------- Small UI helpers -------------------- */

@Composable
private fun ErrorCard(message: String) {
    Card(
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
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = PTITError
            )
            Spacer(Modifier.width(PTITSpacing.sm))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = PTITError
            )
        }
    }
}

/* -------------------- Main Screen -------------------- */

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
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
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
                .background(PTITBackgroundLight)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(PTITSpacing.md),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                /* Action */
                item {
                    SalaryCalculateButton(
                        isLoading = uiState.isLoading,
                        onCalculate = onCalculate
                    )
                }

                /* Errors */
                if (uiState.hasError) {
                    item { ErrorCard(message = uiState.error ?: "Đã xảy ra lỗi") }
                }

                /* Inputs */
                item {
                    SalaryCalculatorTypeSelector(
                        selectedType = uiState.input.calculationType,
                        onTypeChange = { onInputChange(uiState.input.copy(calculationType = it)) }
                    )
                }
                item {
                    SalaryInputForm(
                        input = uiState.input,
                        validationErrors = uiState.validationErrors ?: emptyList(),
                        onInputChange = onInputChange
                    )
                }

                /* Result */
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

                /* Info + FAQ */
                item { SalaryInformationSection() }
                item { SalaryFaqSection() }
            }
        }
    }
}

/* -------------------- Calculator Type -------------------- */

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
                color = PTITTextPrimary
            )
            Spacer(Modifier.height(PTITSpacing.sm))
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
                    Spacer(Modifier.width(PTITSpacing.xs))
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PTITTextPrimary
                    )
                }
            }
        }
    }
}

/* -------------------- Input Form -------------------- */

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

            OutlinedTextField(
                value = input.inputSalary,
                onValueChange = { onInputChange(input.copy(inputSalary = it)) },
                label = {
                    Text(
                        when (input.calculationType) {
                            SalaryCalculationType.GROSS_TO_NET -> "Lương GROSS (VNĐ)"
                            SalaryCalculationType.NET_TO_GROSS -> "Lương NET mong muốn (VNĐ)"
                        }
                    )
                },
                placeholder = { Text("Ví dụ: 15.000.000") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    focusedLabelColor = PTITPrimary
                )
            )

            OutlinedTextField(
                value = input.dependents,
                onValueChange = { onInputChange(input.copy(dependents = it)) },
                label = { Text("Số người phụ thuộc") },
                placeholder = { Text("0") },
                leadingIcon = { Icon(Icons.Default.People, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    focusedLabelColor = PTITPrimary
                )
            )

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = input.hasUnemploymentInsurance,
                    onCheckedChange = { onInputChange(input.copy(hasUnemploymentInsurance = it)) },
                    colors = CheckboxDefaults.colors(checkedColor = PTITPrimary)
                )
                Spacer(Modifier.width(PTITSpacing.xs))
                Text(
                    text = "Đóng bảo hiểm thất nghiệp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PTITTextPrimary
                )
            }

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
                                    tint = PTITError
                                )
                                Spacer(Modifier.width(PTITSpacing.xs))
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PTITError
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* -------------------- Calculate Button -------------------- */

@Composable
private fun SalaryCalculateButton(
    isLoading: Boolean,
    onCalculate: () -> Unit
) {
    Button(
        onClick = onCalculate,
        enabled = !isLoading,
        shape = PTITCornerRadius.lg,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = PTITElevation.xl),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(PTITPrimary, PTITSecondary)),
                shape = PTITCornerRadius.lg
            )
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Icon(Icons.Default.Calculate, contentDescription = null)
            Spacer(Modifier.width(PTITSpacing.sm))
            Text(
                text = "Tính toán",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* -------------------- Result Card -------------------- */

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
            }

            AnimatedVisibility(visible = isExpanded, enter = fadeIn() + slideInVertically()) {
                Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                    Divider(color = PTITSuccess.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Lương GROSS", style = MaterialTheme.typography.bodyMedium, color = PTITTextSecondary)
                            Text(
                                text = formatCurrency(result.grossSalary),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PTITPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Lương NET", style = MaterialTheme.typography.bodyMedium, color = PTITTextSecondary)
                            Text(
                                text = formatCurrency(result.netSalary),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess
                            )
                        }
                    }

                    Divider(color = PTITNeutral200)

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
                    SalaryBreakdownItem("Tổng khấu trừ", result.totalDeductions, isTotal = true)
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

/* -------------------- Information & FAQ -------------------- */

@Composable
private fun SalaryInformationSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = PTITSuccess
                )
                Spacer(Modifier.width(PTITSpacing.sm))
                Text(
                    text = "Kết quả tính lương",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            Text(
                text = "Kết quả sẽ hiển thị ở thẻ “Kết quả tính toán” sau khi bạn nhấn “Tính toán”.",
                style = MaterialTheme.typography.bodyMedium,
                color = PTITTextSecondary,
                textAlign = TextAlign.Center
            )

            SalaryFooterNote()
        }
    }
}

@Composable
private fun SalaryFaqSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = PTITInfo)
                Spacer(Modifier.width(PTITSpacing.sm))
                Text(
                    text = "Thông tin tham khảo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITInfo
                )
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("• Tỷ lệ đóng bảo hiểm 2025:\n") }
                    append("  - BHXH: 8% lương đóng bảo hiểm\n")
                    append("  - BHYT: 1.5% lương đóng bảo hiểm\n")
                    append("  - BHTN: 1% lương đóng bảo hiểm\n\n")

                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("• Giảm trừ thuế TNCN:\n") }
                    append("  - Bản thân: 11.000.000 VNĐ/tháng\n")
                    append("  - Người phụ thuộc: 4.400.000 VNĐ/người/tháng\n\n")

                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("• Lưu ý: ") }
                    append("Kết quả tính toán chỉ mang tính chất tham khảo.")
                },
                style = MaterialTheme.typography.bodySmall,
                color = PTITTextSecondary
            )

            Spacer(Modifier.height(PTITSpacing.md))

            // FAQs
            faqData.forEach { (q, a) ->
                SalaryFaqAccordion(question = q, answer = a)
            }
        }
    }
}

@Composable
private fun SalaryFaqAccordion(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f, label = "faq_rotate")

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
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = PTITTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = PTITTextSecondary,
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = PTITTextSecondary,
                    modifier = Modifier.padding(horizontal = PTITSpacing.md, vertical = PTITSpacing.sm)
                )
            }
        }
    }
}

/* -------------------- Placeholders để biên dịch -------------------- */

@Composable
private fun SalaryFooterNote() {
    Text(
        text = "Chúng tôi áp dụng mức giảm trừ mới nhất bạn đã cấu hình trong module tính toán.",
        style = MaterialTheme.typography.bodySmall,
        color = PTITTextSecondary,
        textAlign = TextAlign.Center
    )
}

private val faqData = listOf(
    "Gross và Net khác gì nhau?" to "Gross là lương trước khấu trừ; Net là số bạn thực nhận sau khi trừ BHXH, BHYT, BHTN và thuế TNCN.",
    "Số người phụ thuộc ảnh hưởng thế nào?" to "Mỗi người phụ thuộc được giảm trừ 4.400.000đ/tháng làm giảm thu nhập chịu thuế.",
    "BHTN có bắt buộc không?" to "Tùy thuộc hợp đồng và khu vực; nếu không tham gia, bỏ chọn “Đóng BHTN”."
)

/* -------------------- Optional Preview -------------------- */

@Preview(showBackground = true)
@Composable
private fun PreviewSalaryScreen() {
    val dummy = CalculatorUiState(
        input = SalaryInput(
            inputSalary = "15000000",
            dependents = "1",
            region = 1,
            hasUnemploymentInsurance = true,
            calculationType = SalaryCalculationType.GROSS_TO_NET
        ),
        isLoading = false,
        error = null,
        result = SalaryCalculationResult(
            grossSalary = 15000000,
            netSalary = 13000000,
            socialInsurance = 1200000,
            healthInsurance = 225000,
            unemploymentInsurance = 150000,
            personalIncomeTax = 250000,
            totalDeductions = 1825000,
            breakdown = SalaryBreakdown()
        ),
        validationErrors = emptyList()
    )

    SalaryCalculatorScreen(
        uiState = dummy,
        onInputChange = {},
        onCalculate = {},
        onClearResult = {},
        onBack = {}
    )
}
