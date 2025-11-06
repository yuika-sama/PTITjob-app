package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ptitjob.ui.screen.candidate.utilities.calculations.formatCurrency
import com.example.ptitjob.ui.screen.candidate.utilities.models.*
import com.example.ptitjob.ui.theme.*

// --- Route Component with ViewModel Integration ---
@Composable
fun BHXHCalculatorRoute(
    onBack: () -> Unit,
    viewModel: UtilitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.bhxhState.collectAsStateWithLifecycle()
    
    BHXHCalculatorScreen(
        uiState = uiState,
        onInputChange = viewModel::updateBHXHInput,
        onCalculate = viewModel::calculateBHXH,
        onClearResult = viewModel::clearBHXHResult,
        onBack = onBack
    )
}

// --- Main Screen Component ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BHXHCalculatorScreen(
    uiState: CalculatorUiState<BHXHInput>,
    onInputChange: (BHXHInput) -> Unit,
    onCalculate: () -> Unit,
    onClearResult: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tính BHXH",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PTITInfo,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PTITGradientStart,
                            PTITGradientMiddle,
                            PTITGradientEnd
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(PTITSpacing.md),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                // --- Header Banner ---
                item {
                    BHXHHeaderBanner()
                }

                // --- Input Form ---
                item {
                    BHXHInputForm(
                        input = uiState.input,
                        validationErrors = uiState.validationErrors,
                        onInputChange = onInputChange
                    )
                }

                // --- Calculate Button ---
                item {
                    BHXHCalculateButton(
                        isLoading = uiState.isLoading,
                        onCalculate = onCalculate
                    )
                }

                // --- Error Display ---
                if (uiState.hasError) {
                    item {
                        ErrorCard(message = uiState.error ?: "Đã xảy ra lỗi")
                    }
                }

                // --- Results ---
                if (uiState.hasResult) {
                    val result = uiState.result as BHXHResult
                    item {
                        BHXHResultCard(
                            result = result,
                            onClearResult = onClearResult
                        )
                    }
                }

                // --- Information Section ---
                item {
                    BHXHInformationSection()
                }
            }
        }
    }
}

// --- Header Banner Component ---
@Composable
private fun BHXHHeaderBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = PTITInfo,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.md))
            Column {
                Text(
                    text = "Tính toán BHXH",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
                Text(
                    text = "Ước tính mức hưởng bảo hiểm xã hội",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PTITTextSecondary
                )
            }
        }
    }
}

// --- Input Form Component ---
@Composable
private fun BHXHInputForm(
    input: BHXHInput,
    validationErrors: List<String>,
    onInputChange: (BHXHInput) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Text(
                text = "Thông tin đầu vào",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PTITTextPrimary
            )

            // Monthly Salary Input
            OutlinedTextField(
                value = input.monthlyGrossSalary,
                onValueChange = { onInputChange(input.copy(monthlyGrossSalary = it)) },
                label = { Text("Lương tháng gộp (VNĐ)") },
                placeholder = { Text("Ví dụ: 15000000") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITInfo,
                    focusedLabelColor = PTITInfo
                )
            )

            // Contribution Months Input
            OutlinedTextField(
                value = input.contributionMonths,
                onValueChange = { onInputChange(input.copy(contributionMonths = it)) },
                label = { Text("Số tháng đóng BHXH") },
                placeholder = { Text("Ví dụ: 120") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITInfo,
                    focusedLabelColor = PTITInfo
                )
            )

            // Insurance Type Selection
            Text(
                text = "Loại bảo hiểm xã hội",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PTITTextPrimary
            )
            
            BHXHType.values().forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onInputChange(input.copy(insuranceType = type)) }
                        .padding(vertical = PTITSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = input.insuranceType == type,
                        onClick = { onInputChange(input.copy(insuranceType = type)) },
                        colors = RadioButtonDefaults.colors(selectedColor = PTITInfo)
                    )
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PTITTextPrimary,
                        modifier = Modifier.padding(start = PTITSpacing.xs)
                    )
                }
            }

            // Region Level Selection
            Text(
                text = "Vùng lương tối thiểu",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PTITTextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
            ) {
                (1..4).forEach { region ->
                    FilterChip(
                        selected = input.regionLevel == region,
                        onClick = { onInputChange(input.copy(regionLevel = region)) },
                        label = { Text("Vùng $region") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Dependents Input
            OutlinedTextField(
                value = input.dependents,
                onValueChange = { onInputChange(input.copy(dependents = it)) },
                label = { Text("Số người phụ thuộc") },
                placeholder = { Text("0") },
                leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITInfo,
                    focusedLabelColor = PTITInfo
                )
            )

            // Validation Errors
            if (validationErrors.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PTITError.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(PTITSpacing.sm)) {
                        validationErrors.forEach { error ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = PTITError,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PTITError,
                                    modifier = Modifier.padding(start = PTITSpacing.xs)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Calculate Button Component ---
@Composable
private fun BHXHCalculateButton(
    isLoading: Boolean,
    onCalculate: () -> Unit
) {
    Button(
        onClick = onCalculate,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PTITInfo,
            contentColor = Color.White
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.sm))
            Text(
                text = "Tính BHXH",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Result Card Component ---
@Composable
private fun BHXHResultCard(
    result: BHXHResult,
    onClearResult: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.md)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kết quả tính BHXH",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITInfo
                )
                Row {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = PTITInfo
                        )
                    }
                    IconButton(onClick = onClearResult) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Xóa kết quả",
                            tint = PTITTextSecondary
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Divider(color = PTITInfo.copy(alpha = 0.3f))

                    // Main Results
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.05f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(PTITSpacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Tổng số tiền BHXH dự kiến",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = formatCurrency(result.totalAmount.toLong()),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = PTITInfo,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Divider(color = PTITNeutral200)

                    // Detailed Information
                    BHXHDetailRow("Lương trung bình", formatCurrency(result.averageSalary.toLong()))
                    BHXHDetailRow("Tổng số tháng đóng", "${result.totalMonths} tháng")
                    BHXHDetailRow("Loại BHXH", result.calculationType.displayName)

                    Divider(color = PTITNeutral200)

                    // Periods Information
                    if (result.periods.isNotEmpty()) {
                        Text(
                            text = "Chi tiết thời gian đóng",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PTITTextPrimary
                        )
                        
                        result.periods.forEach { period ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = PTITNeutral50)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(PTITSpacing.sm)
                                ) {
                                    Text(
                                        text = "Giai đoạn ${period.startYear} - ${period.endYear ?: "hiện tại"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PTITTextPrimary
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Số tháng: ${period.months}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PTITTextSecondary
                                        )
                                        Text(
                                            text = "Lương: ${formatCurrency(period.averageSalary)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PTITTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BHXHDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = PTITTextPrimary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = PTITTextSecondary
        )
    }
}

// --- Error Card Component ---
@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITError.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = PTITError,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = PTITError,
                modifier = Modifier.padding(start = PTITSpacing.sm)
            )
        }
    }
}

// --- Information Section ---
@Composable
private fun BHXHInformationSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITWarning.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PTITWarning,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Thông tin quan trọng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITWarning,
                    modifier = Modifier.padding(start = PTITSpacing.sm)
                )
            }
            
            Text(
                text = """
                • Điều kiện hưởng lương hưu:
                  - Nam: đủ 60 tuổi và có ít nhất 20 năm đóng BHXH
                  - Nữ: đủ 55 tuổi và có ít nhất 20 năm đóng BHXH
                
                • Mức hưởng BHXH = 45% + 2% x số năm đóng vượt quá 15 năm
                • Tối đa 75% lương đóng BHXH trung bình
                
                • Lưu ý: Kết quả chỉ mang tính chất ước tính, tham khảo
                """.trimIndent(),
                style = MaterialTheme.typography.bodySmall,
                color = PTITTextSecondary
            )
        }
    }
}