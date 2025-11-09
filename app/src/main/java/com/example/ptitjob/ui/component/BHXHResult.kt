package com.example.ptitjob.ui.component

import android.provider.ContactsContract.CommonDataKinds.Email.getTypeLabel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

data class BHXHResult(
    val totalAmount: Double,
    val averageSalary: Double,
    val totalMonths: Int,
    val periods: List<BHXHPeriod>
)
@Composable
fun BHXHResultDisplay(result: BHXHResult?, type: String) {
    if (result == null) {
        EmptyState()
    } else {
        ResultState(result = result, type = type)
    }
}

@Composable
private fun EmptyState() {
    Column {
        Text("Kết quả tính toán", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("Vui lòng nhập thông tin và tính toán để xem kết quả", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ResultState(result: BHXHResult, type: String) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Kết quả tính toán", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            AssistChip(
                onClick = {},
                label = { Text(getTypeLabel(type), fontWeight = FontWeight.SemiBold) },
                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary, labelColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
        SummaryCard(totalMonths = result.totalMonths, averageSalary = result.averageSalary)
        TotalAmountCard(totalAmount = result.totalAmount)
        CalculationDetailsCard(periods = result.periods)
    }
}

@Composable
private fun SummaryCard(totalMonths: Int, averageSalary: Double) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.padding(16.dp)) {
            InfoBlock("Thời gian tham gia BHXH", formatTime(totalMonths), Modifier.weight(1f))
            InfoBlock("Mức bình quân tiền lương", "${formatCurrency(averageSalary)} VND", Modifier.weight(1f))
        }
    }
}

@Composable
private fun InfoBlock(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TotalAmountCard(totalAmount: Double) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Số tiền BHXH một lần bạn có thể nhận được", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary), color = MaterialTheme.colorScheme.surfaceVariant) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VD: ${formatCurrency(totalAmount)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("(VND)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text("* Số tiền này chỉ mang tính chất tham khảo...", style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
private fun CalculationDetailsCard(periods: List<BHXHPeriod>) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(16.dp)) {
            Text("Chi tiết tính toán", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            periods.forEachIndexed { index, period ->
                Column {
                    Text("Giai đoạn ${index + 1}: ${period.startYear} - ${period.endYear ?: period.startYear}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("Thời gian: ${formatTime(period.months)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Mức lương: ${formatCurrency(period.salary.toDouble())} VND", style = MaterialTheme.typography.bodyMedium)
                }
                if (index < periods.lastIndex) HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val millions = amount / 1_000_000.0
    return if (millions >= 1) {
        if (millions % 1 == 0.0) {
            "${millions.toInt()} triệu"
        } else {
            "%.1f triệu".format(millions)
        }
    } else {
        NumberFormat.getNumberInstance(Locale("vi", "VN")).format(amount.toLong())
    }
}
private fun formatTime(months: Int): String {
    val years = months / 12
    val remainingMonths = months % 12
    return when {
        years == 0 -> "$remainingMonths tháng"
        remainingMonths == 0 -> "$years năm"
        else -> "$years năm $remainingMonths tháng"
    }
}
private fun getTypeLabel(type: String): String = when (type) {
    "mandatory" -> "BHXH bắt buộc"
    "voluntary" -> "BHXH tự nguyện"
    else -> "Cả bắt buộc & tự nguyện"
}