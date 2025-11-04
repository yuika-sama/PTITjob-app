package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Data Models ---
data class BHXHPeriod(
    val id: String,
    val startYear: Int,
    val endYear: Int?,
    val months: Int,
    val salary: Long // Sử dụng Long cho giá trị tiền tệ lớn
)
@Composable
fun BHXHCalculator(type: String, onCalculate: () -> Unit) {
    var periods by remember {
        mutableStateOf(listOf(BHXHPeriod("1", 2024, 2024, 1, 5000000L)))
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Giai đoạn nộp BHXH", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Button(onClick = onCalculate, contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)) {
                Text("Tính BHXH", fontWeight = FontWeight.SemiBold)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            periods.forEachIndexed { index, period ->
                PeriodCard(
                    period = period, index = index,
                    onUpdate = { updatedPeriod -> periods = periods.map { if (it.id == updatedPeriod.id) updatedPeriod else it } },
                    onRemove = { periodId -> periods = periods.filter { it.id != periodId } },
                    canRemove = periods.size > 1
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { periods = periods + BHXHPeriod((periods.size + 1).toString(), 2024, 2024, 1, 5000000L) }) {
                Text("Thêm giai đoạn")
            }
            if (type != "mandatory") {
                OutlinedButton(onClick = { /* Logic for pregnancy/voluntary */ }) {
                    Text(if (type == "both") "Thêm giai đoạn thai sản" else "Thêm BHXH tự nguyện")
                }
            }
        }
    }
}

@Composable
private fun PeriodCard(period: BHXHPeriod, index: Int, onUpdate: (BHXHPeriod) -> Unit, onRemove: (String) -> Unit, canRemove: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Giai đoạn ${index + 1}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (canRemove) {
                    IconButton(onClick = { onRemove(period.id) }) {
                        Icon(Icons.Default.Delete, "Xóa giai đoạn", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = period.startYear.toString(), onValueChange = { onUpdate(period.copy(startYear = it.toIntOrNull() ?: 2024)) }, label = { Text("Từ năm") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = (period.endYear ?: period.startYear).toString(), onValueChange = { onUpdate(period.copy(endYear = it.toIntOrNull() ?: 2024)) }, label = { Text("Đến năm") }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = period.months.toString(), onValueChange = { onUpdate(period.copy(months = it.toIntOrNull() ?: 1)) }, label = { Text("Số tháng") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = period.salary.toString(), onValueChange = { onUpdate(period.copy(salary = it.toLongOrNull() ?: 0L)) }, label = { Text("Mức lương") }, suffix = { Text("VND") }, modifier = Modifier.weight(1f))
            }
        }
    }
}
