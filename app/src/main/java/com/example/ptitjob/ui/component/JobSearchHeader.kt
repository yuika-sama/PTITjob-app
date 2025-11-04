package com.example.ptitjob.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Data Models ---
data class SearchOption(val value: String, val label: String)

// --- Component Chính ---
@Composable
fun JobSearchHeader(totalJobs: Int) {
    // --- State Management cho UI ---
    var category by remember { mutableStateOf("all") }
    var keyword by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("all") }

    // --- Dữ liệu mẫu ---
    val categories = listOf(
        SearchOption("all", "Tất cả danh mục"),
        SearchOption("it", "IT - Phần mềm"),
        SearchOption("marketing", "Marketing"),
        SearchOption("sales", "Sales - Bán hàng")
    )
    val locations = listOf(
        SearchOption("all", "Tất cả địa điểm"),
        SearchOption("ha-noi", "Hà Nội"),
        SearchOption("ho-chi-minh", "Hồ Chí Minh"),
        SearchOption("da-nang", "Đà Nẵng")
    )

    val hasFilters = category != "all" || keyword.isNotBlank() || location != "all"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Header Text ---
            Text(
                text = "Tuyển dụng ${"%,d".format(totalJobs)} việc làm",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Cập nhật: 29/10/2025",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Search Form ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Dropdown
                    SearchDropdown(
                        modifier = Modifier.weight(1.5f),
                        options = categories,
                        selectedValue = category,
                        onValueChange = { category = it },
                        leadingIcon = Icons.Default.Person
                    )
                    // Keyword Input
                    SearchKeywordInput(
                        modifier = Modifier.weight(2f),
                        keyword = keyword,
                        onKeywordChange = { keyword = it }
                    )
                    // Location Dropdown
                    SearchDropdown(
                        modifier = Modifier.weight(1.5f),
                        options = locations,
                        selectedValue = location,
                        onValueChange = { location = it },
                        leadingIcon = Icons.Default.LocationOn
                    )
                    // Action Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { /* TODO: Trigger Search */ },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("Tìm kiếm")
                        }
                        if (hasFilters) {
                            OutlinedButton(
                                onClick = {
                                    category = "all"
                                    keyword = ""
                                    location = "all"
                                },
                                modifier = Modifier.height(56.dp)
                            ) {
                                Text("Xóa")
                            }
                        }
                    }
                }
            }

            // --- Active Filters Display ---
            if (hasFilters) {
                Spacer(modifier = Modifier.height(16.dp))
                val activeFiltersText = buildAnnotatedString {
                    append("Đang tìm kiếm: ")
                    val filters = mutableListOf<String>()
                    if (category != "all") filters.add(categories.find { it.value == category }?.label ?: "")
                    if (keyword.isNotBlank()) filters.add("\"$keyword\"")
                    if (location != "all") filters.add(locations.find { it.value == location }?.label ?: "")

                    filters.forEachIndexed { index, filter ->
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append(filter)
                        }

                        if (index < filters.lastIndex) {
                            append(" • ")
                        }
                    }
                }
                Text(
                    text = activeFiltersText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}


// --- Các Composable con ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchDropdown(
    modifier: Modifier = Modifier,
    options: List<SearchOption>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.value == selectedValue }?.label ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(),
            leadingIcon = { Icon(leadingIcon, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchKeywordInput(
    modifier: Modifier = Modifier,
    keyword: String,
    onKeywordChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = keyword,
        onValueChange = onKeywordChange,
        modifier = modifier,
        placeholder = { Text("Vị trí, từ khóa...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            // TODO: Trigger search
            focusManager.clearFocus()
        }),
        singleLine = true
    )
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 1200)
@Composable
fun JobSearchHeaderPreview() {
    MaterialTheme {
        JobSearchHeader(totalJobs = 123456)
    }
}