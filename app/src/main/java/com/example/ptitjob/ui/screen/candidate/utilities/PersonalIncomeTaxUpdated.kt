package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun PersonalIncomeTaxRoute(
    onBack: () -> Unit,
    viewModel: UtilitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.taxState.collectAsStateWithLifecycle()
    
    PersonalIncomeTaxScreen(
        uiState = uiState,
        onInputChange = viewModel::updateTaxInput,
        onCalculate = viewModel::calculateTax,
        onClearResult = viewModel::clearTaxResult,
        onBack = onBack
    )
}

// --- Main Screen Component ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalIncomeTaxScreen(
    uiState: CalculatorUiState<TaxInput>,
    onInputChange: (TaxInput) -> Unit,
    onCalculate: () -> Unit,
    onClearResult: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Thuế thu nhập cá nhân",
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
                    containerColor = PTITWarning,
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
                    TaxHeaderBanner()
                }

                // --- Input Form ---
                item {
                    TaxInputForm(
                        input = uiState.input,
                        validationErrors = uiState.validationErrors,
                        onInputChange = onInputChange
                    )
                }

                // --- Calculate Button ---
                item {
                    TaxCalculateButton(
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
                    val result = uiState.result as TaxCalculationResult
                    item {
                        TaxResultCard(
                            result = result,
                            onClearResult = onClearResult
                        )
                    }
                }

                // --- Tax Brackets Information ---
                item {
                    TaxBracketsInformation()
                }
            }
        }
    }
}

// --- Header Banner Component ---
@Composable
private fun TaxHeaderBanner() {
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
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = PTITWarning,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.md))
            Column {
                Text(
                    text = "Tính thuế TNCN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
                Text(
                    text = "Tính toán thuế thu nhập cá nhân theo luật 2025",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PTITTextSecondary
                )
            }
        }
    }
}

// --- Input Form Component ---
@Composable
private fun TaxInputForm(
    input: TaxInput,
    validationErrors: List<String>,
    onInputChange: (TaxInput) -> Unit
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

            // Monthly Gross Salary Input
            OutlinedTextField(
                value = input.monthlyGrossSalary,
                onValueChange = { onInputChange(input.copy(monthlyGrossSalary = it)) },
                label = { Text("Lương gộp tháng (VNĐ)") },
                placeholder = { Text("Ví dụ: 20000000") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITWarning,
                    focusedLabelColor = PTITWarning
                )
            )

            // Dependents Input
            OutlinedTextField(
                value = input.dependents,
                onValueChange = { onInputChange(input.copy(dependents = it)) },
                label = { Text("Số người phụ thuộc") },
                placeholder = { Text("0") },
                leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITWarning,
                    focusedLabelColor = PTITWarning
                )
            )

            // Personal Deduction Input
            OutlinedTextField(
                value = input.personalDeduction,
                onValueChange = { onInputChange(input.copy(personalDeduction = it)) },
                label = { Text("Giảm trừ bản thân (VNĐ)") },
                placeholder = { Text("11000000") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITWarning,
                    focusedLabelColor = PTITWarning
                )
            )

            // Insurance Deduction Input  
            OutlinedTextField(
                value = input.insuranceDeduction,
                onValueChange = { onInputChange(input.copy(insuranceDeduction = it)) },
                label = { Text("Khấu trừ bảo hiểm (VNĐ) - Để trống để tự tính") },
                placeholder = { Text("Tự động tính từ lương") },
                leadingIcon = { Icon(Icons.Default.HealthAndSafety, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITWarning,
                    focusedLabelColor = PTITWarning
                )
            )

            // Other Deductions Input
            OutlinedTextField(
                value = input.otherDeductions,
                onValueChange = { onInputChange(input.copy(otherDeductions = it)) },
                label = { Text("Khấu trừ khác (VNĐ)") },
                placeholder = { Text("0") },
                leadingIcon = { Icon(Icons.Default.Remove, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITWarning,
                    focusedLabelColor = PTITWarning
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
private fun TaxCalculateButton(
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
            containerColor = PTITWarning,
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
                text = "Tính thuế",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Result Card Component ---
@Composable
private fun TaxResultCard(
    result: TaxCalculationResult,
    onClearResult: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITWarning.copy(alpha = 0.1f)),
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
                    text = "Kết quả tính thuế",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITWarning
                )
                Row {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = PTITWarning
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
                    Divider(color = PTITWarning.copy(alpha = 0.3f))

                    // Main Results Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Lương gộp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.grossSalary),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PTITPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Lương thực nhận",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.netSalary),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess
                            )
                        }
                    }

                    Divider(color = PTITNeutral200)

                    // Tax and Deductions Breakdown
                    Text(
                        text = "Chi tiết tính thuế",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )

                    TaxDetailRow("Thu nhập chịu thuế", formatCurrency(result.taxableIncome))
                    TaxDetailRow("Thuế TNCN", formatCurrency(result.personalIncomeTax), isHighlight = true)
                    
                    Divider(color = PTITNeutral200)
                    
                    Text(
                        text = "Chi tiết giảm trừ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )
                    
                    TaxDetailRow("Giảm trừ bản thân", formatCurrency(result.personalDeduction))
                    TaxDetailRow("Giảm trừ người phụ thuộc", formatCurrency(result.dependentDeduction))
                    TaxDetailRow("Khấu trừ bảo hiểm", formatCurrency(result.insuranceDeduction))
                    if (result.otherDeductions > 0) {
                        TaxDetailRow("Khấu trừ khác", formatCurrency(result.otherDeductions))
                    }

                    // Tax Brackets Breakdown
                    if (result.breakdown.isNotEmpty()) {
                        Divider(color = PTITNeutral200)
                        
                        Text(
                            text = "Chi tiết thuế theo bậc",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PTITTextPrimary
                        )
                        
                        result.breakdown.forEach { bracket ->
                            TaxBracketCard(bracket = bracket)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxDetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isHighlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = PTITTextPrimary
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) PTITWarning else PTITTextSecondary
        )
    }
}

@Composable
private fun TaxBracketCard(bracket: TaxBracketBreakdown) {
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
                text = "Bậc ${bracket.level}: ${(bracket.rate * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PTITTextPrimary
            )
            Text(
                text = bracket.description,
                style = MaterialTheme.typography.bodySmall,
                color = PTITTextSecondary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Thu nhập: ${formatCurrency(bracket.taxableAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PTITTextSecondary
                )
                Text(
                    text = "Thuế: ${formatCurrency(bracket.tax)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PTITWarning
                )
            }
        }
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

// --- Tax Brackets Information ---
@Composable
private fun TaxBracketsInformation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.1f)),
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
                    tint = PTITInfo,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Biểu thuế TNCN 2025",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITInfo,
                    modifier = Modifier.padding(start = PTITSpacing.sm)
                )
            }
            
            UtilityConstants.TAX_BRACKETS.forEachIndexed { index, bracket ->
                val maxFormatted = if (bracket.max == Long.MAX_VALUE) "trở lên" else formatCurrency(bracket.max)
                val rate = (bracket.rate * 100).toInt()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bậc ${index + 1}: $rate%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )
                    Text(
                        text = "${formatCurrency(bracket.min)} - $maxFormatted",
                        style = MaterialTheme.typography.bodySmall,
                        color = PTITTextSecondary
                    )
                }
            }
            
            Divider(color = PTITInfo.copy(alpha = 0.3f))
            
            Text(
                text = "• Giảm trừ gia cảnh: 11.000.000 VNĐ/tháng\n" +
                        "• Giảm trừ người phụ thuộc: 4.400.000 VNĐ/người/tháng\n" +
                        "• Khấu trừ bảo hiểm = 10.5% lương gộp",
                style = MaterialTheme.typography.bodySmall,
                color = PTITTextSecondary
            )
        }
    }
}