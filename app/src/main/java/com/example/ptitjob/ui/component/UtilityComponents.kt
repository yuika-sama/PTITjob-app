package com.example.ptitjob.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.screen.candidate.utilities.calculations.*
import com.example.ptitjob.ui.screen.candidate.utilities.models.*
import com.example.ptitjob.ui.theme.*
import java.text.NumberFormat
import java.util.*

// ===== UTILITY FUNCTIONS =====
fun formatCurrency(amount: Long): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${numberFormat.format(amount)} ₫"
}

fun formatPercentage(percentage: Double): String {
    return "%.2f%%".format(percentage)
}

// ===== TAX CALCULATOR COMPONENT =====
@Composable
fun TaxCalculator(
    onCalculate: (com.example.ptitjob.ui.screen.candidate.utilities.models.TaxCalculationResult) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var input: TaxInput by remember { mutableStateOf(TaxInput()) }
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // Gross Salary Input
        UtilityTextField(
            value = input.monthlyGrossSalary,
            onValueChange = { input = input.copy(monthlyGrossSalary = it) },
            label = "Lương gộp tháng (VNĐ)",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Money,
            placeholder = "Ví dụ: 20000000"
        )

        // Dependents Input
        UtilityTextField(
            value = input.dependents,
            onValueChange = { input = input.copy(dependents = it) },
            label = "Số người phụ thuộc",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.People,
            placeholder = "0"
        )

        // Other Deductions
        UtilityTextField(
            value = input.otherDeductions,
            onValueChange = { input = input.copy(otherDeductions = it) },
            label = "Các khoản giảm trừ khác (VNĐ)",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Remove,
            placeholder = "0"
        )

        // Validation Errors
        if (validationErrors.isNotEmpty()) {
            ValidationErrorCard(errors = validationErrors)
        }

        // Calculate Button
        Button(
            onClick = {
                val validation = input.validate()
                if (validation.isValid) {
                    validationErrors = emptyList()
                    val result: com.example.ptitjob.ui.screen.candidate.utilities.models.TaxCalculationResult = TaxCalculator.calculatePersonalIncomeTax(input)
                    onCalculate(result)
                } else {
                    validationErrors = validation.errorMessages
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.sm))
            Text("Tính thuế TNCN", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TaxResultDisplay(
    result: com.example.ptitjob.ui.screen.candidate.utilities.models.TaxCalculationResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // Summary Card
        ResultSummaryCard(
            title = "Kết quả tính thuế",
            items = listOf(
                "Lương gộp" to formatCurrency(result.grossSalary),
                "Thuế TNCN phải nộp" to formatCurrency(result.personalIncomeTax),
                "Lương thực nhận" to formatCurrency(result.netSalary)
            ),
            color = PTITSuccess
        )

        // Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Text(
                    text = "Chi tiết tính toán",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )

                BreakdownItem("Thu nhập chịu thuế", formatCurrency(result.taxableIncome))
                BreakdownItem("Giảm trừ bản thân", formatCurrency(result.personalDeduction))
                BreakdownItem("Giảm trừ người phụ thuộc", formatCurrency(result.dependentDeduction))
                BreakdownItem("Khấu trừ bảo hiểm", formatCurrency(result.insuranceDeduction))

                if (result.breakdown.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(PTITSpacing.sm))
                    Text(
                        text = "Thuế theo từng bậc:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    result.breakdown.forEach { bracket ->
                        BreakdownItem(
                            "Bậc ${bracket.level} (${formatPercentage(bracket.rate)})",
                            formatCurrency(bracket.tax)
                        )
                    }
                }
            }
        }
    }
}

// ===== SALARY CALCULATOR COMPONENT =====
@Composable
fun SalaryCalculator(
    calculationType: String,
    onCalculate: (com.example.ptitjob.ui.screen.candidate.utilities.models.SalaryCalculationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    var input by remember {
        mutableStateOf(
            SalaryInput(
                calculationType = if (calculationType == "gross-to-net")
                    SalaryCalculationType.GROSS_TO_NET
                else
                    SalaryCalculationType.NET_TO_GROSS
            )
        )
    }
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // Salary Input
        UtilityTextField(
            value = input.inputSalary,
            onValueChange = { input = input.copy(inputSalary = it) },
            label = if (calculationType == "gross-to-net") "Lương gộp (VNĐ)" else "Lương thực nhận (VNĐ)",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Money,
            placeholder = "Ví dụ: 15000000"
        )

        // Dependents Input
        UtilityTextField(
            value = input.dependents,
            onValueChange = { input = input.copy(dependents = it) },
            label = "Số người phụ thuộc",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.People,
            placeholder = "0"
        )

        // Region Selection
        RegionSelector(
            selectedRegion = input.region,
            onRegionSelected = { input = input.copy(region = it) }
        )

        // Unemployment Insurance Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = input.hasUnemploymentInsurance,
                onCheckedChange = { input = input.copy(hasUnemploymentInsurance = it) }
            )
            Text(
                text = "Tham gia bảo hiểm thất nghiệp",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Validation Errors
        if (validationErrors.isNotEmpty()) {
            ValidationErrorCard(errors = validationErrors)
        }

        // Calculate Button
        Button(
            onClick = {
                val validation = input.validate()
                if (validation.isValid) {
                    validationErrors = emptyList()
                    val result =
                        if (calculationType == "gross-to-net") {
                            SalaryCalculator.calculateGrossToNet(input)
                        } else {
                            SalaryCalculator.calculateNetToGross(input)
                        }
                    onCalculate(result)
                } else {
                    validationErrors = validation.errorMessages
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.sm))
            Text(
                text = if (calculationType == "gross-to-net") "Tính lương NET" else "Tính lương GROSS",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SalaryResultDisplay(
    result: com.example.ptitjob.ui.screen.candidate.utilities.models.SalaryCalculationResult,
    calculationType: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // Summary Card
        ResultSummaryCard(
            title = "Kết quả tính lương",
            items = listOf(
                "Lương gộp (GROSS)" to formatCurrency(result.grossSalary),
                "Lương thực nhận (NET)" to formatCurrency(result.netSalary),
                "Tổng khấu trừ" to formatCurrency(result.totalDeductions)
            ),
            color = PTITSuccess
        )

        // Deductions Breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PTITWarning.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Text(
                    text = "Chi tiết các khoản khấu trừ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )

                BreakdownItem("Bảo hiểm xã hội (8%)", formatCurrency(result.socialInsurance))
                BreakdownItem("Bảo hiểm y tế (1.5%)", formatCurrency(result.healthInsurance))
                if (result.unemploymentInsurance > 0) {
                    BreakdownItem("Bảo hiểm thất nghiệp (1%)", formatCurrency(result.unemploymentInsurance))
                }
                BreakdownItem("Thuế thu nhập cá nhân", formatCurrency(result.personalIncomeTax))
            }
        }
    }
}

// ===== COMPOUND INTEREST CALCULATOR =====
@Composable
fun CompoundInterestCalculator(
    onCalculate: (com.example.ptitjob.ui.screen.candidate.utilities.models.CompoundInterestResult) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var input by remember { mutableStateOf(CompoundInterestInput()) }
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // Principal Input
        UtilityTextField(
            value = input.principal,
            onValueChange = { input = input.copy(principal = it) },
            label = "Số tiền ban đầu (VNĐ)",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Money,
            placeholder = "Ví dụ: 100000000"
        )

        // Annual Rate Input
        UtilityTextField(
            value = input.annualRate,
            onValueChange = { input = input.copy(annualRate = it) },
            label = "Lãi suất năm (%)",
            keyboardType = KeyboardType.Decimal,
            leadingIcon = Icons.Default.Percent,
            placeholder = "Ví dụ: 8.5"
        )

        // Years Input
        UtilityTextField(
            value = input.years,
            onValueChange = { input = input.copy(years = it) },
            label = "Số năm đầu tư",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Schedule,
            placeholder = "Ví dụ: 10"
        )

        // Monthly Contribution Input
        UtilityTextField(
            value = input.monthlyContribution,
            onValueChange = { input = input.copy(monthlyContribution = it) },
            label = "Đóng góp hàng tháng (VNĐ)",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Add,
            placeholder = "0 (tùy chọn)"
        )

        // Compound Frequency Selection
        CompoundFrequencySelector(
            selectedFrequency = input.compoundFrequency,
            onFrequencySelected = { input = input.copy(compoundFrequency = it) }
        )

        // Validation Errors
        if (validationErrors.isNotEmpty()) {
            ValidationErrorCard(errors = validationErrors)
        }

        // Calculate Button
        Button(
            onClick = {
                val validation = input.validate()
                if (validation.isValid) {
                    validationErrors = emptyList()
                    val result = CompoundInterestCalculator.calculate(input)
                    onCalculate(result)
                } else {
                    validationErrors = validation.errorMessages
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.sm))
            Text("Tính lãi suất kép", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CompoundInterestResultDisplay(
    result: com.example.ptitjob.ui.screen.candidate.utilities.models.CompoundInterestResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // Summary Card
        ResultSummaryCard(
            title = "Kết quả đầu tư",
            items = listOf(
                "Số tiền cuối kỳ" to formatCurrency(result.finalAmount),
                "Tổng lãi" to formatCurrency(result.totalInterest),
                "Tỷ suất lợi nhuận" to formatPercentage(result.effectiveRate)
            ),
            color = PTITSuccess
        )

        // Investment Breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Text(
                    text = "Phân tích đầu tư",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )

                BreakdownItem("Số tiền ban đầu", formatCurrency(result.principal))
                BreakdownItem("Tổng đóng góp", formatCurrency(result.totalContributions))
                BreakdownItem("Tổng đầu tư", formatCurrency(result.totalInvestment))
                BreakdownItem("Lãi tích lũy", formatCurrency(result.totalInterest))
            }
        }
    }
}

// ===== BHXH CALCULATOR =====
@Composable
fun BHXHCalculator(
    type: String,
    onCalculate: (com.example.ptitjob.ui.screen.candidate.utilities.models.BHXHResult) -> Unit,
    modifier: Modifier = Modifier
) {
    var input by remember {
        mutableStateOf(
            BHXHInput(
                insuranceType = when (type) {
                    "mandatory" -> BHXHType.MANDATORY
                    "voluntary" -> BHXHType.VOLUNTARY
                    else -> BHXHType.BOTH
                }
            )
        )
    }
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // Monthly Salary Input
        UtilityTextField(
            value = input.monthlyGrossSalary,
            onValueChange = { input = input.copy(monthlyGrossSalary = it) },
            label = "Lương trung bình tháng (VNĐ)",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Money,
            placeholder = "Ví dụ: 8000000"
        )

        // Contribution Months Input
        UtilityTextField(
            value = input.contributionMonths,
            onValueChange = { input = input.copy(contributionMonths = it) },
            label = "Số tháng đóng BHXH",
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Schedule,
            placeholder = "Ví dụ: 120"
        )

        // Region Level Selection
        RegionSelector(
            selectedRegion = input.regionLevel,
            onRegionSelected = { input = input.copy(regionLevel = it) }
        )

        // Validation Errors
        if (validationErrors.isNotEmpty()) {
            ValidationErrorCard(errors = validationErrors)
        }

        // Calculate Button
        Button(
            onClick = {
                val validation = input.validate()
                if (validation.isValid) {
                    validationErrors = emptyList()
                    val result = BHXHCalculator.calculate(input)
                    onCalculate(result)
                } else {
                    validationErrors = validation.errorMessages
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.sm))
            Text("Tính BHXH một lần", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun BHXHResultDisplay(
    result: com.example.ptitjob.ui.screen.candidate.utilities.models.BHXHResult,
    type: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        ResultSummaryCard(
            title = "Kết quả tính BHXH",
            items = listOf(
                "Số tiền nhận được" to formatCurrency(result.totalAmount.toLong()),
                "Lương trung bình" to formatCurrency(result.averageSalary.toLong()),
                "Tổng số tháng đóng" to "${result.totalMonths} tháng"
            ),
            color = PTITSuccess
        )
    }
}

// ===== SHARED UI COMPONENTS =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        placeholder = { Text(placeholder, color = PTITTextSecondary) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PTITPrimary,
            focusedLabelColor = PTITPrimary
        )
    )
}

@Composable
fun ValidationErrorCard(
    errors: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITError.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, PTITError.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = PTITError,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = "Vui lòng sửa các lỗi sau:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITError
                    )
                )
            }

            errors.forEach { error ->
                Text(
                    text = "• $error",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PTITError
                    ),
                    modifier = Modifier.padding(start = PTITSpacing.lg)
                )
            }
        }
    }
}

@Composable
fun ResultSummaryCard(
    title: String,
    items: List<Pair<String, String>>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            items.forEachIndexed { index, (label, value) ->
                ResultItem(label = label, value = value, isHighlight = index == 0)
            }
        }
    }
}

@Composable
fun ResultItem(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = PTITTextSecondary,
                fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isHighlight) PTITPrimary else PTITTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun BreakdownItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PTITTextSecondary
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PTITTextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun RegionSelector(
    selectedRegion: Int,
    onRegionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
    ) {
        Text(
            text = "Vùng lương tối thiểu",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = PTITTextPrimary
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            (1..4).forEach { region ->
                FilterChip(
                    onClick = { onRegionSelected(region) },
                    label = { Text("Vùng $region") },
                    selected = selectedRegion == region,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CompoundFrequencySelector(
    selectedFrequency: CompoundFrequency,
    onFrequencySelected: (CompoundFrequency) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
    ) {
        Text(
            text = "Tần suất ghép lãi",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = PTITTextPrimary
            )
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            items(CompoundFrequency.entries) { frequency ->
                FilterChip(
                    onClick = { onFrequencySelected(frequency) },
                    label = { Text(frequency.displayName) },
                    selected = selectedFrequency == frequency
                )
            }
        }
    }
}
