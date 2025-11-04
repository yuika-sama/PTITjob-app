package com.example.ptitjob.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Data Models ---
data class FilterCategory(val name: String, val count: Int)

// --- Component Chính ---
@Composable
fun JobFiltersSidebar() {
    // --- State Management cho các bộ lọc ---
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var selectedLevel by remember { mutableStateOf("Tất cả") }
    var selectedSalaryRange by remember { mutableStateOf("Tất cả") }

    // --- Dữ liệu mẫu ---
    val categories = listOf(
        FilterCategory("Marketing", 5497),
        FilterCategory("Kế toán", 5304),
        FilterCategory("Sales Bán lẻ/Dịch vụ", 1928),
        FilterCategory("Chăm sóc khách hàng", 1806),
        FilterCategory("Nhân sự", 1693)
    )
    val levels = listOf("Tất cả", "Nhân viên", "Trưởng nhóm", "Trưởng/Phó phòng", "Quản lý / Giám sát")
    val salaryRanges = listOf("Tất cả", "10 - 15 triệu", "15 - 20 triệu", "20 - 25 triệu", "Trên 50 triệu")

    Column(modifier = Modifier.widthIn(max = 320.dp)) {
        // --- Header ---
        FilterHeader()
        Spacer(Modifier.height(16.dp))

        // --- Các Accordion ---
        FilterAccordion(title = "Theo danh mục nghề") {
            CategoryFilter(
                categories = categories,
                selected = selectedCategories,
                onCategorySelect = { category ->
                    selectedCategories = if (selectedCategories.contains(category)) {
                        selectedCategories - category
                    } else {
                        selectedCategories + category
                    }
                }
            )
        }
        FilterAccordion(title = "Cấp bậc") {
            RadioFilter(
                options = levels,
                selected = selectedLevel,
                onOptionSelect = { selectedLevel = it }
            )
        }
        FilterAccordion(title = "Mức lương") {
            SalaryFilter(
                ranges = salaryRanges,
                selected = selectedSalaryRange,
                onRangeSelect = { selectedSalaryRange = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        // --- Nút Xóa bộ lọc ---
        Button(
            onClick = {
                selectedCategories = emptySet()
                selectedLevel = "Tất cả"
                selectedSalaryRange = "Tất cả"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Xóa tất cả bộ lọc")
        }

        // --- Hiển thị các bộ lọc đang áp dụng ---
        AppliedFilters(
            selectedCategories = selectedCategories,
            selectedLevel = selectedLevel,
            selectedSalaryRange = selectedSalaryRange,
            onCategoryDeselect = { category -> selectedCategories -= category },
            onLevelDeselect = { selectedLevel = "Tất cả" },
            onSalaryDeselect = { selectedSalaryRange = "Tất cả" }
        )
    }
}


// --- Các Composable con ---

@Composable
private fun FilterHeader() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                text = "Lọc nâng cao",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun FilterAccordion(
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "iconRotation")

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(bottom = if (isExpanded) 8.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                Icons.Default.MoreVert,
                contentDescription = if (isExpanded) "Thu gọn" else "Mở rộng",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                content()
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
fun CategoryFilter(
    categories: List<FilterCategory>,
    selected: Set<String>,
    onCategorySelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        categories.forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategorySelect(category.name) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selected.contains(category.name),
                    onCheckedChange = { onCategorySelect(category.name) }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(category.name, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "(${category.count})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RadioFilter(
    options: List<String>,
    selected: String,
    onOptionSelect: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (selected == option),
                        onClick = { onOptionSelect(option) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (selected == option), onClick = null)
                Text(option, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun SalaryFilter(
    ranges: List<String>,
    selected: String,
    onRangeSelect: (String) -> Unit
) {
    // Tương tự RadioFilter
    RadioFilter(options = ranges, selected = selected, onOptionSelect = onRangeSelect)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppliedFilters(
    selectedCategories: Set<String>,
    selectedLevel: String,
    selectedSalaryRange: String,
    onCategoryDeselect: (String) -> Unit,
    onLevelDeselect: () -> Unit,
    onSalaryDeselect: () -> Unit,
) {
    val hasFilters = selectedCategories.isNotEmpty() || selectedLevel != "Tất cả" || selectedSalaryRange != "Tất cả"
    if (hasFilters) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text("Bộ lọc đang áp dụng:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedCategories.forEach { category ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text(category) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Bỏ chọn $category",
                                modifier = Modifier.size(InputChipDefaults.IconSize).clickable { onCategoryDeselect(category) }
                            )
                        }
                    )
                }
                if (selectedLevel != "Tất cả") {
                    InputChip(selected = false, onClick = {}, label = { Text(selectedLevel) }, trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(InputChipDefaults.IconSize).clickable(onClick = onLevelDeselect)) })
                }
                if (selectedSalaryRange != "Tất cả") {
                    InputChip(selected = false, onClick = {}, label = { Text(selectedSalaryRange) }, trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(InputChipDefaults.IconSize).clickable(onClick = onSalaryDeselect)) })
                }
            }
        }
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun JobFiltersSidebarPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            JobFiltersSidebar()
        }
    }
}