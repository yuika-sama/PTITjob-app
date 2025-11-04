@file:Suppress("DEPRECATION")

package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

// --- Data Models ---
data class CompoundInterestResult(
    val initialInvestment: Long, 
    val totalContributions: Long, 
    val totalInterest: Long, 
    val finalAmount: Long,
    val annualInterestRate: Double, 
    val years: Int, 
    val yearlyBreakdown: List<YearlyBreakdown>
)

data class YearlyBreakdown(
    val year: Int,
    val yearEndBalance: Long,
    val interestEarned: Long,
    val totalContributions: Long
)

// --- Component Chính ---
@Composable
fun CompoundInterestResultDisplay(result: CompoundInterestResult) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Summary Cards
        SummaryCards(result = result)
        // Chart
        OutlinedCard { CompoundInterestChart(result = result) }
        // Breakdown Table - chỉ hiển thị khi có dữ liệu
        if (result.yearlyBreakdown.isNotEmpty()) {
            BreakdownTable(result = result)
        }
        // Performance Summary
        PerformanceSummary(result = result)
    }
}


// --- Các Composable con ---

@Composable
private fun SummaryCards(result: CompoundInterestResult) {
    val summaryCards = listOf(
        Triple(Icons.Default.AccountBalance, "Tiền gốc đầu tư", result.initialInvestment),
        Triple(Icons.Default.Savings, "Tổng tiền gửi", result.totalContributions),
        Triple(Icons.Default.MonetizationOn, "Tổng lãi kiếm được", result.totalInterest),
        Triple(Icons.AutoMirrored.Filled.TrendingUp, "Giá trị cuối kỳ", result.finalAmount)
    )
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 250.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.heightIn(max = 400.dp) // Cần thiết khi lồng Lazy-list
    ) {
        items(summaryCards) { (icon, label, value) ->
            SummaryCard(icon = icon, label = label, value = value)
        }
    }
}

@Composable
private fun SummaryCard(icon: ImageVector, label: String, value: Long) {
    OutlinedCard(border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color.White)
                }
                Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(
                text = formatCurrency(value),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun CompoundInterestChart(result: CompoundInterestResult) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("LÃI KÉP ${result.annualInterestRate}% TRONG ${result.years} NĂM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Biểu đồ mô phỏng bằng các Box xếp chồng
            val totalHeight = 300f
            val principalHeight = if (result.finalAmount > 0) {
                (result.totalContributions.toFloat() / result.finalAmount * totalHeight).coerceIn(0f, totalHeight)
            } else 0f
            val interestHeight = if (result.finalAmount > 0) {
                (result.totalInterest.toFloat() / result.finalAmount * totalHeight).coerceIn(0f, totalHeight)
            } else 0f

            Column(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxWidth().height( (totalHeight - principalHeight - interestHeight).coerceAtLeast(0f).dp).background(Color.Transparent)) // Phần trống ở trên
                Box(Modifier.fillMaxWidth().height(interestHeight.dp).background(MaterialTheme.colorScheme.primary)) {
                    if (interestHeight > 20f) { // Chỉ hiển thị text khi có đủ không gian
                        Text("LÃI KÉP", Modifier.align(Alignment.Center), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Box(Modifier.fillMaxWidth().height(principalHeight.dp).background(MaterialTheme.colorScheme.secondary)) {
                    if (principalHeight > 20f) { // Chỉ hiển thị text khi có đủ không gian
                        Text("TIỀN GỐC", Modifier.align(Alignment.Center), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownTable(result: CompoundInterestResult) {
    OutlinedCard {
        Column {
            Text("Phân tích chi tiết theo năm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            // Header
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp)) {
                Text("Năm", Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Text("Tiền gốc", Modifier.weight(1.5f), textAlign = TextAlign.End, fontWeight = FontWeight.SemiBold)
                Text("Lãi", Modifier.weight(1.5f), textAlign = TextAlign.End, fontWeight = FontWeight.SemiBold)
                Text("Tổng giá trị", Modifier.weight(1.5f), textAlign = TextAlign.End, fontWeight = FontWeight.SemiBold)
            }
            // Body
            result.yearlyBreakdown.forEach { row ->
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Năm ${row.year}", Modifier.weight(1f))
                    Text(formatCurrency(row.totalContributions), Modifier.weight(1.5f), textAlign = TextAlign.End)
                    Text("+${formatCurrency(row.interestEarned)}", Modifier.weight(1.5f), textAlign = TextAlign.End, color = MaterialTheme.colorScheme.primary)
                    Text(formatCurrency(row.yearEndBalance), Modifier.weight(1.5f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PerformanceSummary(result: CompoundInterestResult) {
    OutlinedCard {
        Column(Modifier.padding(16.dp)) {
            Text("Sức mạnh của lãi suất kép", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Bạn đang có trong tay ${formatCurrency(result.initialInvestment)} ban đầu và muốn đầu tư với lãi suất ${result.annualInterestRate}%/năm.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// --- Helper Functions ---

// --- Preview ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun CompoundInterestResultDisplayPreview() {
    val sampleResult = CompoundInterestResult(
        initialInvestment = 10_000_000, totalContributions = 130_000_000,
        totalInterest = 110_550_000, finalAmount = 240_550_000,
        annualInterestRate = 10.0, years = 10,
        yearlyBreakdown = listOf(
            YearlyBreakdown(year = 1, yearEndBalance = 23_650_000, interestEarned = 1_650_000, totalContributions = 22_000_000),
            YearlyBreakdown(year = 2, yearEndBalance = 38_100_000, interestEarned = 4_100_000, totalContributions = 34_000_000)
        )
    )
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            CompoundInterestResultDisplay(result = sampleResult)
        }
    }
}