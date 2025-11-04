package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

// --- Data Models ---
data class TaxBracket(
    val level: Int, val from: Long, val to: Long, val rate: Int,
    val taxableAmount: Long, val taxAmount: Long
)
// --- Component Chính ---
@Composable
fun TaxResultDisplay(result: TaxCalculationResult) {
    val insurance = (result.grossSalary * 0.105).roundToLong()

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Assessment, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Text(
                "Kết quả chi tiết",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Summary Cards
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(
                "THU NHẬP GỐC",
                result.grossSalary,
                MaterialTheme.colorScheme.primary,
                Modifier.weight(1f)
            )
            SummaryCard(
                "THUẾ TNCN",
                result.personalIncomeTax,
                MaterialTheme.colorScheme.error,
                Modifier.weight(1f),
                percentage = if (result.grossSalary > 0) (result.personalIncomeTax.toDouble() / result.grossSalary * 100) else 0.0
            )
            SummaryCard(
                "THU NHẬP RÒNG",
                result.netSalary,
                MaterialTheme.colorScheme.primary,
                Modifier.weight(1f),
                percentage = if (result.grossSalary > 0) (result.netSalary.toDouble() / result.grossSalary * 100) else 0.0
            )
        }

        // Detailed Breakdown
        DetailedBreakdown(result = result, insurance = insurance)

        // Tax Bracket Table
        if (result.breakdown.isNotEmpty()) {
            TaxBracketTable(result = result)
        }
    }
}

// --- Các Composable con ---

@Composable
private fun SummaryCard(
    label: String,
    amount: Long,
    color: Color,
    modifier: Modifier = Modifier,
    percentage: Double? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(color.copy(alpha = 0.2f), color.copy(alpha = 0.1f))
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                formatCurrency(amount),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(top = 8.dp)
            )
            percentage?.let {
                Text(
                    "(${it.format(1)}% tổng thu nhập)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailedBreakdown(result: TaxCalculationResult, insurance: Long) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "Phân tích chi tiết",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            BreakdownRow(
                "Thu nhập gốc (Gross)",
                result.grossSalary,
                100f,
                MaterialTheme.colorScheme.primary
            )
            BreakdownRow(
                "Trừ: Bảo hiểm (10.5%)",
                insurance,
                if (result.grossSalary > 0) (insurance.toFloat() / result.grossSalary * 100) else 0f,
                MaterialTheme.colorScheme.error,
                isDeduction = true
            )
            BreakdownRow(
                "Trừ: Giảm trừ bản thân",
                result.personalDeduction,
                if (result.grossSalary > 0) (result.personalDeduction.toFloat() / result.grossSalary * 100) else 0f,
                MaterialTheme.colorScheme.tertiary,
                isDeduction = true
            )
            if (result.dependentDeduction > 0) {
                BreakdownRow(
                    "Trừ: Người phụ thuộc",
                    result.dependentDeduction,
                    if (result.grossSalary > 0) (result.dependentDeduction.toFloat() / result.grossSalary * 100) else 0f,
                    MaterialTheme.colorScheme.tertiary,
                    isDeduction = true
                )
            }
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Thu nhập chịu thuế", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    formatCurrency(result.taxableIncome),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    amount: Long,
    progress: Float,
    color: Color,
    isDeduction: Boolean = false
) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (isDeduction) "-${formatCurrency(amount)}" else formatCurrency(amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isDeduction) MaterialTheme.colorScheme.error else Color.Unspecified
            )
        }
        Spacer(Modifier.height(4.dp))
        // ✅ Material3: LinearProgressIndicator (determinate)
        LinearProgressIndicator(
            progress = { (progress / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color
        )
    }
}

@Composable
private fun TaxBracketTable(result: TaxCalculationResult) {
    OutlinedCard {
        Column {
            Column(Modifier.padding(16.dp)) {
                Text("Bảng thuế suất lũy tiến", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text("Chi tiết tính thuế theo từng bậc", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Table Header
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp)) {
                TableCell("Bậc", Modifier.weight(0.5f))
                TableCell("Mức thu nhập/tháng", Modifier.weight(1.5f))
                TableCell("Thuế suất", Modifier.weight(1f), Alignment.CenterHorizontally) // ✅ dùng CenterHorizontally
                TableCell("Thu nhập chịu thuế", Modifier.weight(1.5f), Alignment.End)
                TableCell("Thuế phải nộp", Modifier.weight(1.5f), Alignment.End)
            }
            // Table Body
            result.breakdown.forEach { bracket ->
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    TableCell(modifier = Modifier.weight(0.5f)) {
                        SuggestionChip(onClick = {}, label = { Text(bracket.level.toString()) })
                    }
                    TableCell(formatRange(bracket.from, bracket.to), Modifier.weight(1.5f))
                    TableCell(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) { // ✅
                        val color = when {
                            bracket.rate <= 5 -> MaterialTheme.colorScheme.primary
                            bracket.rate <= 15 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                        SuggestionChip(
                            onClick = {},
                            label = { Text("${bracket.rate}%") },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = color.copy(alpha = 0.1f),
                                labelColor = color
                            )
                        )
                    }
                    TableCell(formatCurrency(bracket.taxableAmount), Modifier.weight(1.5f), Alignment.End)
                    TableCell(formatCurrency(bracket.taxAmount), Modifier.weight(1.5f), Alignment.End, color = MaterialTheme.colorScheme.error)
                }
            }
            // Table Footer
            Row(Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)).padding(16.dp)) {
                TableCell("Tổng cộng", Modifier.weight(3f), fontWeight = FontWeight.Bold)
                TableCell(formatCurrency(result.taxableIncome), Modifier.weight(1.5f), Alignment.End, fontWeight = FontWeight.Bold)
                TableCell(formatCurrency(result.personalIncomeTax), Modifier.weight(1.5f), Alignment.End, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null
) {
    Box(modifier = modifier) {
        Text(
            text,
            modifier = Modifier.align(
                when (horizontalAlignment) {
                    Alignment.End -> Alignment.CenterEnd
                    Alignment.CenterHorizontally -> Alignment.Center
                    else -> Alignment.CenterStart
                }
            ),
            color = color,
            fontWeight = fontWeight
        )
    }
}

@Composable
fun RowScope.TableCell(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = when (horizontalAlignment) {
            Alignment.End -> Alignment.CenterEnd
            Alignment.CenterHorizontally -> Alignment.Center
            else -> Alignment.CenterStart
        },
        content = content
    )
}

// --- Helper Functions ---

private fun formatRange(from: Long, to: Long): String =
    if (to >= 1_000_000_000L) "Trên ${formatCurrency(from)}"
    else "${formatCurrency(from)} - ${formatCurrency(to)}"

private fun Double.format(digits: Int) = "%.${digits}f".format(this)

// --- Preview ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun TaxResultDisplayPreview() {
    val sampleResult = TaxCalculationResult(
        grossSalary = 30_000_000,
        personalDeduction = 11_000_000,
        dependentDeduction = 4_400_000,
        totalDeduction = 15_400_000,
        taxableIncome = 11_450_000,
        personalIncomeTax = 867_500,
        netSalary = 25_982_500,
        breakdown = listOf(
            TaxBracket(1, 0, 5_000_000, 5, 5_000_000, 250_000),
            TaxBracket(2, 5_000_000, 10_000_000, 10, 5_000_000, 500_000),
            TaxBracket(3, 10_000_000, 18_000_000, 15, 1_450_000, 117_500)
        )
    )
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            TaxResultDisplay(result = sampleResult)
        }
    }
}
