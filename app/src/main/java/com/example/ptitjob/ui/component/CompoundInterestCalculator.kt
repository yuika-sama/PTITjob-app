@file:Suppress("DEPRECATION")

package com.example.ptitjob.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// --- Data Models ---

// --- Component Chính ---
@Composable
fun CompoundInterestCalculator() {
    // --- State Management cho UI ---
    var activeStep by remember { mutableIntStateOf(0) }
    var initialInvestment by remember { mutableLongStateOf(10_000_000) }
    var monthlyContribution by remember { mutableLongStateOf(1_000_000) }
    var investmentPeriod by remember { mutableIntStateOf(10) }
    var interestRate by remember { mutableDoubleStateOf(10.0) }

    val result = CompoundInterestResult(
        initialInvestment = 10_000_000,
        totalContributions = 130_000_000,
        totalInterest = 110_550_000,
        finalAmount = 240_550_000,
        annualInterestRate = 10.0,
        years = 10,
        yearlyBreakdown = emptyList() // Hoặc thêm dữ liệu mẫu nếu cần
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            HeaderSection()
            Spacer(Modifier.height(24.dp))

            // Stepper
            Stepper(
                activeStep = activeStep,
                onStepChange = { activeStep = it },
                onReset = { activeStep = 0 }
            )

            Spacer(Modifier.height(24.dp))

            // Kết quả
            ResultOverview(result = result)

            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(onClick = { /* onCalculate(result) */ }, contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)) {
                    Icon(Icons.Default.Calculate, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tính lãi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


// --- Các Composable con ---

@Composable
private fun HeaderSection() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = Color.White)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text("Công cụ tính Lãi Kép & Lợi nhuận đầu tư", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Tính toán lợi nhuận thu được trong tương lai.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Stepper(activeStep: Int, onStepChange: (Int) -> Unit, onReset: () -> Unit) {
    val steps = listOf("Đầu tư ban đầu", "Khoản đóng góp", "Lãi suất", "Kỳ hạn")

    Column {
        steps.forEachIndexed { index, title ->
            StepItem(
                title = "Bước ${index + 1}: $title",
                isActive = activeStep == index,
                isCompleted = activeStep > index,
                onClick = { onStepChange(index) }
            ) {
                // Nội dung của từng bước
                AnimatedVisibility(visible = activeStep == index) {
                    Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
                        when (index) {
                            0 -> OutlinedTextField(value = "", onValueChange = {}, label = { Text("Số tiền gốc (VNĐ)") }, modifier = Modifier.fillMaxWidth())
                            1 -> Column {
                                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Gửi mỗi tháng (VNĐ)") }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Thời gian gửi (Năm)") }, modifier = Modifier.fillMaxWidth())
                            }
                            2 -> OutlinedTextField(value = "", onValueChange = {}, label = { Text("Lãi suất (%)") }, modifier = Modifier.fillMaxWidth())
                            3 -> OutlinedTextField(value = "", onValueChange = {}, label = { Text("Định kỳ gửi") }, modifier = Modifier.fillMaxWidth())
                        }
                        Row(modifier = Modifier.padding(top = 16.dp)) {
                            Button(onClick = { onStepChange(index + 1) }, enabled = index < steps.lastIndex) { Text("Tiếp tục") }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = { onStepChange(index - 1) }, enabled = index > 0) { Text("Quay lại") }
                        }
                    }
                }
            }
        }

        // Hoàn thành
        AnimatedVisibility(visible = activeStep >= steps.size) {
            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Đã hoàn thành!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = onReset) { Text("Tính lại") }
                }
            }
        }
    }
}

@Composable
private fun StepItem(
    title: String,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val color = when {
        isActive -> MaterialTheme.colorScheme.primary
        isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = color)
        }
        content()
    }
}

@Composable
private fun ResultOverview(result: CompoundInterestResult) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Text("📊 Tổng quan kết quả", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Column(verticalArrangement = Arrangement.Top) {
                ResultChip("Tổng tiền gửi", formatCurrencyCompact(result.totalContributions), MaterialTheme.colorScheme.primary, Modifier.weight(1f).size(40.dp))
                ResultChip("Lãi kiếm được", "+${formatCurrencyCompact(result.totalInterest)}", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f).size(40.dp))
                ResultChip("Tổng giá trị cuối kỳ", formatCurrencyCompact(result.finalAmount), MaterialTheme.colorScheme.error, Modifier.weight(1f).size(40.dp))
            }
        }
    }
}

@Composable
private fun ResultChip(label: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(amount, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

// --- Helper Functions ---

// --- Preview ---
@Preview(showBackground = true, widthDp = 800)
@Composable
fun CompoundInterestCalculatorPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            CompoundInterestCalculator()
        }
    }
}