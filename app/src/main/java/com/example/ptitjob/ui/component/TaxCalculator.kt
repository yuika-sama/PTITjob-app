package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

// --- Data Models (để quản lý trạng thái kết quả) ---

// Thêm định nghĩa tối thiểu để file này tự compile

data class TaxCalculationResult(
    val grossSalary: Long,
    val personalDeduction: Long,
    val dependentDeduction: Long,
    val totalDeduction: Long,
    val taxableIncome: Long,
    val personalIncomeTax: Long,
    val netSalary: Long,
    val breakdown: List<TaxBracket> = emptyList()
)

// --- Component Chính ---
@Composable
fun TaxCalculator() {
    // --- State Management cho UI ---
    var grossSalary by remember { mutableLongStateOf(10_000_000L) }
    var dependents by remember { mutableIntStateOf(0) }
    var insuranceType by remember { mutableStateOf("official") }
    var customInsurance by remember { mutableLongStateOf(0L) }

    // --- Giả lập tính toán kết quả ---
    val result = calculateTax(grossSalary, dependents, insuranceType, customInsurance)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    "Công cụ tính Thuế thu nhập cá nhân chuẩn 2025",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(24.dp))

            // Quy định
            RegulationsInfo()
            Spacer(Modifier.height(24.dp))

            // Bố cục 2 cột
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Cột Input
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InputSection(
                        grossSalary = grossSalary, onGrossSalaryChange = { grossSalary = it },
                        insuranceType = insuranceType, onInsuranceTypeChange = { insuranceType = it },
                        customInsurance = customInsurance, onCustomInsuranceChange = { customInsurance = it },
                        dependents = dependents, onDependentsChange = { dependents = it }
                    )
                }
                // Cột Kết quả
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ResultSection(result = result)
                }
            }
        }
    }
}

// --- Các Composable con ---

@Composable
private fun RegulationsInfo() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "✓ Áp dụng quy định:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "• Mức giảm trừ gia cảnh 11 triệu/tháng (bản thân) và 4.4 triệu/tháng (người phụ thuộc).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InputSection(
    grossSalary: Long, onGrossSalaryChange: (Long) -> Unit,
    insuranceType: String, onInsuranceTypeChange: (String) -> Unit,
    customInsurance: Long, onCustomInsuranceChange: (Long) -> Unit,
    dependents: Int, onDependentsChange: (Int) -> Unit
) {
    // Thu nhập
    Text("Thu Nhập (Gross)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    OutlinedTextField(
        value = grossSalary.toString(),
        onValueChange = { onGrossSalaryChange(it.toLongOrNull() ?: 0L) },
        label = { Text("Thu nhập") },
        suffix = { Text("VND") },
        modifier = Modifier.fillMaxWidth()
    )

    // Mức lương đóng bảo hiểm
    Text("Mức lương đóng bảo hiểm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    Column {
        RadioOption(
            label = "Trên lương chính thức",
            selected = insuranceType == "official",
            onClick = { onInsuranceTypeChange("official") }
        )
        RadioOption(
            label = "Khác",
            selected = insuranceType == "other",
            onClick = { onInsuranceTypeChange("other") }
        )
    }
    if (insuranceType == "other") {
        OutlinedTextField(
            value = customInsurance.toString(),
            onValueChange = { onCustomInsuranceChange(it.toLongOrNull() ?: 0L) },
            label = { Text("Mức bảo hiểm tùy chỉnh") },
            suffix = { Text("VND") },
            modifier = Modifier.fillMaxWidth()
        )
    }

    // Người phụ thuộc
    Text("Số người phụ thuộc", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    OutlinedTextField(
        value = dependents.toString(),
        onValueChange = { onDependentsChange(it.toIntOrNull() ?: 0) },
        label = { Text("Số người") },
        suffix = { Text("Người") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun RadioOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun ResultSection(result: TaxCalculationResult) {
    Text("Kết quả tính toán", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

    // Summary Boxes
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard(
            label = "Giảm trừ bản thân",
            amount = result.personalDeduction,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "Người phụ thuộc",
            amount = result.dependentDeduction,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }

    // Detailed Breakdown
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Chi tiết tính toán:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // Sửa: dùng result.grossSalary thay vì biến grossSalary không có trong scope
            val insuranceAmount = result.grossSalary - result.taxableIncome - result.totalDeduction

            ResultRow("Thu nhập gốc:", formatCurrency(result.grossSalary))
            ResultRow("Bảo hiểm:", "-${formatCurrency(insuranceAmount)}", MaterialTheme.colorScheme.error)
            ResultRow("Giảm trừ gia cảnh:", "-${formatCurrency(result.totalDeduction)}", MaterialTheme.colorScheme.error)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            ResultRow("Thu nhập chịu thuế:", formatCurrency(result.taxableIncome), isBold = true)
            ResultRow("Thuế TNCN:", "-${formatCurrency(result.personalIncomeTax)}", MaterialTheme.colorScheme.error, isBold = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            ResultRow("Thu nhập ròng:", formatCurrency(result.netSalary), MaterialTheme.colorScheme.primary, isBold = true, isTitle = true)
        }
    }

    Button(
        onClick = { /* onCalculate(result) */ },
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        Text("Tính thuế TNCN", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SummaryCard(label: String, amount: Long, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatCurrency(amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    isBold: Boolean = false,
    isTitle: Boolean = false
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (isTitle) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            style = if (isTitle) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}

// --- Helper Functions ---
//private fun formatCurrency(amount: Long): String =
//    "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(amount)}đ"

private fun calculateTax(
    grossSalary: Long,
    dependents: Int,
    insuranceType: String,
    customInsurance: Long
): TaxCalculationResult {
    // Giả lập logic tính toán đơn giản cho preview
    val insurance = if (insuranceType == "official") (grossSalary * 0.105).toLong() else customInsurance
    val personalDeduction = 11_000_000L
    val dependentDeduction = (dependents * 4_400_000L)
    val totalDeduction = personalDeduction + dependentDeduction
    val taxableIncome = (grossSalary - insurance - totalDeduction).coerceAtLeast(0L)
    val personalIncomeTax = (taxableIncome * 0.1).toLong() // Giả lập 10%
    val netSalary = grossSalary - insurance - personalIncomeTax

    return TaxCalculationResult(
        grossSalary = grossSalary,
        personalDeduction = personalDeduction,
        dependentDeduction = dependentDeduction,
        totalDeduction = totalDeduction,
        taxableIncome = taxableIncome,
        personalIncomeTax = personalIncomeTax,
        netSalary = netSalary
    )
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 1000)
@Composable
fun TaxCalculatorPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            TaxCalculator()
        }
    }
}
