package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

// --- Data Models ---

// --- Component Chính ---
@Composable
fun SalaryResultDisplay(
    result: SalaryCalculationResult,
    calculationType: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // --- Kết quả chính ---
        MainResultCard(result = result, calculationType = calculationType)

        // --- Thông tin đầu vào ---
        InputInfoCard(result = result)

        // --- Bảng chi tiết ---
        CalculationTable(result = result)

        // --- Ghi chú ---
        NotesCard(result = result)
    }
}


// --- Các Composable con ---

@Composable
private fun MainResultCard(result: SalaryCalculationResult, calculationType: String) {
    val primaryColor = Color(0xFFDE221A)
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, primaryColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kết quả tính toán",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ResultBox(
                    label = "Lương Gross",
                    amount = result.grossSalary,
                    isActive = calculationType == "gross-to-net",
                    modifier = Modifier.weight(1f)
                )
                ResultBox(
                    label = "Lương Net",
                    amount = result.netSalary,
                    isActive = calculationType == "net-to-gross",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ResultBox(label: String, amount: Long, isActive: Boolean, modifier: Modifier = Modifier) {
    val primaryColor = Color(0xFFDE221A)
    val secondaryColor = Color(0xFFFF5A52)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(if (isActive) 8.dp else 2.dp),
        border = if (!isActive) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
    ) {
        Column(
            modifier = Modifier
                .background(
                    if (isActive) Brush.linearGradient(listOf(primaryColor, secondaryColor))
                    else Brush.linearGradient(listOf(Color.White, Color.White)))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatCurrencyCompact(amount),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun InputInfoCard(result: SalaryCalculationResult) {
    OutlinedCard {
        Column(Modifier.padding(16.dp)) {
            Text("Thông tin đầu vào", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 250.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 150.dp) // Cần thiết khi lồng Lazy-list
            ) {
                item { InfoChipRow("Vùng lương:", "Vùng ${result.region}") }
                item { InfoChipRow("Người phụ thuộc:", "${result.dependents} người") }
                item { InfoChipRow("Loại bảo hiểm:", if(result.insuranceType == "official") "Lương chính thức" else "Tự chọn") }
                item { InfoChipRow("Mức đóng BH:", formatCurrencyCompact(result.insuranceBase)) }
            }
        }
    }
}

@Composable
private fun InfoChipRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        SuggestionChip(onClick = {}, label = { Text(value) })
    }
}

@Composable
private fun CalculationTable(result: SalaryCalculationResult) {
    val detailRowData = listOf(
        "Bảo hiểm xã hội (8%)" to result.bhxh, "Bảo hiểm y tế (1.5%)" to result.bhyt,
        "Bảo hiểm thất nghiệp (1%)" to result.bhtn, "Tổng bảo hiểm" to result.totalInsurance,
        "Thu nhập chịu thuế" to result.taxableIncome, "Giảm trừ bản thân" to result.personalDeduction,
        "Giảm trừ người phụ thuộc" to result.dependentDeduction, "Tổng giảm trừ" to result.totalDeduction,
        "Thu nhập tính thuế" to result.taxBase, "Thuế thu nhập cá nhân" to result.personalIncomeTax
    )
    OutlinedCard {
        Column(Modifier.padding(16.dp)) {
            Text("Chi tiết tính toán", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            // Table Header
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(vertical = 8.dp)) {
                Text("Khoản mục", Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Text("Số tiền", Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = FontWeight.SemiBold)
            }
            // Table Body
            Column {
                detailRowData.forEach { (label, value) ->
                    val isTotal = label.startsWith("Tổng")
                    Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(label, Modifier.weight(1f), fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal)
                        Text(formatCurrencyCompact(value), Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (label.contains("bảo hiểm") || label.contains("Thuế")) MaterialTheme.colorScheme.error else Color.Unspecified)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesCard(result: SalaryCalculationResult) {
    OutlinedCard {
        Column(Modifier.padding(16.dp)) {
            Text("Ghi chú", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("• Giảm trừ bản thân: ${formatCurrencyCompact(result.personalDeduction)}/tháng", style = MaterialTheme.typography.bodyMedium)
            Text("• Giảm trừ người phụ thuộc: 4.4 triệu/người/tháng", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// --- Helper Functions ---

// --- Preview ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun SalaryResultDisplayPreview() {
    val sampleResult = SalaryCalculationResult(
        grossSalary = 30000000, netSalary = 25982500, bhxh = 2400000, bhyt = 450000,
        bhtn = 300000, totalInsurance = 3150000, taxableIncome = 26850000,
        personalDeduction = 11000000, dependentDeduction = 4400000, totalDeduction = 15400000,
        taxBase = 11450000, personalIncomeTax = 867500, region = "1", dependents = 1,
        insuranceBase = 30000000, insuranceType = "official"
    )
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SalaryResultDisplay(result = sampleResult, calculationType = "gross-to-net")
        }
    }
}