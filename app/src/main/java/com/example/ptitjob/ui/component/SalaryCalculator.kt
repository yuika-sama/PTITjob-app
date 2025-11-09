package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

data class SalaryCalculationResult (
    val grossSalary: Long, val netSalary: Long, val bhxh: Long, val bhyt: Long, val bhtn: Long,
    val totalInsurance: Long, val taxableIncome: Long, val personalDeduction: Long,
    val dependentDeduction: Long, val totalDeduction: Long, val taxBase: Long,
    val personalIncomeTax: Long, val region: String, val dependents: Int,
    val insuranceBase: Long, val insuranceType: String
)

const val PERSONAL_DEDUCTION = 11000000L
const val DEPENDENT_DEDUCTION = 4400000L
val MINIMUM_WAGES = mapOf("I" to 4960000L, "II" to 4410000L, "III" to 3860000L, "IV" to 3450000L)
val TAX_BRACKETS = listOf(
    Pair(5000000L, 0.05),
    Pair(10000000L, 0.10),
    Pair(18000000L, 0.15),
    Pair(32000000L, 0.20),
    Pair(52000000L, 0.25),
    Pair(80000000L, 0.30),
    Pair(Long.MAX_VALUE, 0.35)
)


@JvmOverloads
@Composable
fun SalaryCalculator(
    calculationType: String, // "gross-to-net" or "net-to-gross"
    onCalculate: (SalaryCalculationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    // --- State Management cho UI ---
    var salaryInput by remember { mutableLongStateOf(15_000_000L) }
    var dependents by remember { mutableStateOf(0) }
    var region by remember { mutableStateOf("I") }
    var insuranceType by remember { mutableStateOf("official") }
    var customInsuranceBase by remember { mutableStateOf(5_000_000L) }
    var modalOpen by remember { mutableStateOf(false) }


    val primaryColor = Color(0xFFDE221A)
    val secondaryColor = Color(0xFFFF5A52)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // --- Header ---
            val title = if (calculationType == "gross-to-net") "Tính lương Net từ Gross" else "Tính lương Gross từ Net"
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = primaryColor,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .drawBehind {
                        drawLine(
                            color = primaryColor,
                            start = Offset(0f, size.height + 4.dp.toPx()),
                            end = Offset(40.dp.toPx(), size.height + 4.dp.toPx()),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
            )

            // --- Form Sections ---
            InputSection(
                label = if (calculationType == "gross-to-net") "Thu Nhập:" else "Lương thực nhận:",
                value = salaryInput,
                onValueChange = { salaryInput = it },
                placeholder = if (calculationType == "gross-to-net") "Nhập lương Gross" else "Nhập lương Net"
            )
            DependentsSection(dependents = dependents, onDependentsChange = { dependents = it })
            RegionSection(selectedRegion = region, onRegionChange = { region = it }, onExplainClick = { modalOpen = true })
            InsuranceSection(
                insuranceType = insuranceType, onInsuranceTypeChange = { insuranceType = it },
                customBase = customInsuranceBase, onCustomBaseChange = { customInsuranceBase = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // --- Calculate Button ---
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = {
                        val minimumWage = MINIMUM_WAGES[region] ?: 0L
                        val result = if (calculationType == "gross-to-net") {
                            calculateGrossToNet(
                                grossSalary = salaryInput, dependents = dependents, region = region,
                                insuranceType = insuranceType, customInsuranceBase = customInsuranceBase,
                                minimumWage = minimumWage
                            )
                        } else {
                            calculateNetToGross(
                                netSalary = salaryInput, dependents = dependents, region = region,
                                minimumWage = minimumWage
                            )
                        }
                        onCalculate(result)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    modifier = Modifier.width(250.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(primaryColor, secondaryColor)))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (calculationType == "gross-to-net") "GROSS → NET" else "NET → GROSS",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // --- Modal giải thích vùng ---
    if (modalOpen) {
        RegionExplanationDialog(onDismiss = { modalOpen = false })
    }
}


// --- Các Composable con ---

@Composable
private fun FormSection(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        content()
    }
}

@Composable
private fun InputSection(label: String, value: Long, onValueChange: (Long) -> Unit, placeholder: String) {
    FormSection(title = label) {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { onValueChange(it.toLongOrNull() ?: 0L) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            suffix = { Text("VND") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
private fun DependentsSection(dependents: Int, onDependentsChange: (Int) -> Unit) {
    FormSection(title = "Số người phụ thuộc:") {
        OutlinedTextField(
            value = dependents.toString(),
            onValueChange = { onDependentsChange(it.toIntOrNull()?.coerceAtLeast(0) ?: 0) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            suffix = { Text("Người") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
private fun RegionSection(selectedRegion: String, onRegionChange: (String) -> Unit, onExplainClick: () -> Unit) {
    FormSection(title = "Vùng lương tối thiểu") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onExplainClick) {
                Text("(giải thích)", style = MaterialTheme.typography.bodySmall)
            }
        }
        Column {
            MINIMUM_WAGES.forEach { (regionKey, wage) ->
                RadioOption(
                    label = "Vùng $regionKey - ${formatCurrencyCompact(wage)}/tháng",
                    selected = selectedRegion == regionKey,
                    onClick = { onRegionChange(regionKey) }
                )
            }
        }
    }
}

@Composable
private fun InsuranceSection(
    insuranceType: String, onInsuranceTypeChange: (String) -> Unit,
    customBase: Long, onCustomBaseChange: (Long) -> Unit
) {
    FormSection(title = "Mức lương đóng bảo hiểm:") {
        Column {
            RadioOption(label = "Trên lương chính thức", selected = insuranceType == "official", onClick = { onInsuranceTypeChange("official") })
            RadioOption(label = "Khác:", selected = insuranceType == "custom", onClick = { onInsuranceTypeChange("custom") })
        }
        if (insuranceType == "custom") {
            OutlinedTextField(
                value = customBase.toString(),
                onValueChange = { onCustomBaseChange(it.toLongOrNull() ?: 0L) },
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp),
                placeholder = { Text("Nhập mức lương") },
                suffix = { Text("VND") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
private fun RadioOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected, onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFDE221A))
        )
        Text(text = label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun RegionExplanationDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.heightIn(max = 500.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Mức lương tối thiểu vùng 2025", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { Text("Vùng I: 4,960,000 đồng/tháng", fontWeight = FontWeight.SemiBold) }
                    item { Text("Vùng II: 4,410,000 đồng/tháng", fontWeight = FontWeight.SemiBold) }
                    // Thêm chi tiết các vùng khác nếu cần
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Đóng")
                }
            }
        }
    }
}

// --- Helper Functions ---

private fun calculateProgressiveTax(taxableIncome: Long): Long {
    var remainingIncome = taxableIncome
    var totalTax: Double = 0.0
    var previousBracketMax: Long = 0

    if (remainingIncome <= 0) return 0

    for ((bracketMax, rate) in TAX_BRACKETS) {
        if (remainingIncome > 0) {
            val incomeInBracket = minOf(remainingIncome, bracketMax - previousBracketMax)
            totalTax += incomeInBracket * rate
            remainingIncome -= incomeInBracket
            previousBracketMax = bracketMax
        } else {
            break
        }
    }
    return totalTax.toLong()
}


fun calculateGrossToNet(
    grossSalary: Long, dependents: Int, region: String,
    insuranceType: String, customInsuranceBase: Long, minimumWage: Long
): SalaryCalculationResult {
    val insuranceBase = when (insuranceType) {
        "official" -> minOf(grossSalary, 20 * minimumWage)
        else -> customInsuranceBase
    }

    val bhxh = (insuranceBase * 0.08).toLong()
    val bhyt = (insuranceBase * 0.015).toLong()
    val bhtn = (insuranceBase * 0.01).toLong()
    val totalInsurance = bhxh + bhyt + bhtn

    val personalDeduction = PERSONAL_DEDUCTION
    val dependentDeductionAmount = (dependents * DEPENDENT_DEDUCTION)
    val totalDeduction = personalDeduction + dependentDeductionAmount

    val taxableIncome = grossSalary - totalInsurance
    val taxBase = max(0, taxableIncome - totalDeduction)

    val personalIncomeTax = calculateProgressiveTax(taxBase)

    val netSalary = grossSalary - totalInsurance - personalIncomeTax

    return SalaryCalculationResult(
        grossSalary = grossSalary, netSalary = netSalary, bhxh = bhxh, bhyt = bhyt, bhtn = bhtn,
        totalInsurance = totalInsurance, taxableIncome = taxableIncome,
        personalDeduction = personalDeduction, dependentDeduction = dependentDeductionAmount,
        totalDeduction = totalDeduction, taxBase = taxBase, personalIncomeTax = personalIncomeTax,
        region = region, dependents = dependents, insuranceBase = insuranceBase, insuranceType = insuranceType
    )
}

fun calculateNetToGross(
    netSalary: Long, dependents: Int, region: String, minimumWage: Long
): SalaryCalculationResult {
    // This is a simplified iterative approach as the formula is complex
    var estimatedGross = netSalary // Start with a rough estimate

    for (i in 0..10) { // Iterate a few times to converge
        val insuranceBase = minOf(estimatedGross, 20 * minimumWage)
        val bhxh = (insuranceBase * 0.08).toLong()
        val bhyt = (insuranceBase * 0.015).toLong()
        val bhtn = (insuranceBase * 0.01).toLong()
        val totalInsurance = bhxh + bhyt + bhtn

        val personalDeduction = PERSONAL_DEDUCTION
        val dependentDeductionAmount = (dependents * DEPENDENT_DEDUCTION)

        val pretaxIncome = netSalary - (personalDeduction + dependentDeductionAmount - totalInsurance)
        val taxBase = max(0, pretaxIncome) // This is an approximation
        val personalIncomeTax = calculateProgressiveTax(taxBase)

        estimatedGross = netSalary + totalInsurance + personalIncomeTax
    }

    // Final calculation with the estimated gross
    return calculateGrossToNet(
        grossSalary = estimatedGross,
        dependents = dependents,
        region = region,
        insuranceType = "official", // Net-to-Gross usually assumes official insurance base
        customInsuranceBase = 0L,
        minimumWage = minimumWage
    )
}