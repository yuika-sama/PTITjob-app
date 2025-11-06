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
import com.example.ptitjob.ui.component.YearlyBreakdown
import com.example.ptitjob.ui.screen.candidate.utilities.calculations.formatCurrency
import com.example.ptitjob.ui.screen.candidate.utilities.models.*
import com.example.ptitjob.ui.theme.*

// --- Route Component with ViewModel Integration ---
@Composable
fun CompoundInterestRoute(
    onBack: () -> Unit,
    viewModel: UtilitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.compoundInterestState.collectAsStateWithLifecycle()
    
    CompoundInterestScreen(
        uiState = uiState,
        onInputChange = viewModel::updateCompoundInterestInput,
        onCalculate = viewModel::calculateCompoundInterest,
        onClearResult = viewModel::clearCompoundInterestResult,
        onBack = onBack
    )
}

// --- Main Screen Component ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestScreen(
    uiState: CalculatorUiState<CompoundInterestInput>,
    onInputChange: (CompoundInterestInput) -> Unit,
    onCalculate: () -> Unit,
    onClearResult: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lãi suất kép",
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
                    containerColor = PTITSuccess,
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
                    CompoundInterestHeaderBanner()
                }

                // --- Input Form ---
                item {
                    CompoundInterestInputForm(
                        input = uiState.input,
                        validationErrors = uiState.validationErrors,
                        onInputChange = onInputChange
                    )
                }

                // --- Calculate Button ---
                item {
                    CompoundInterestCalculateButton(
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
                    val result = uiState.result as CompoundInterestResult
                    item {
                        CompoundInterestResultCard(
                            result = result,
                            onClearResult = onClearResult
                        )
                    }
                    
//                    // --- Year by Year Breakdown ---
//                    if (result.yearlyBreakdown.isNotEmpty()) {
//                        item {
//                            // Ensure we're passing the correct list type
//                            YearlyBreakdownCard(breakdown = result.yearlyBreakdown)
//                        }
//                    }
                }

                // --- Investment Tips ---
                item {
                    InvestmentTipsInformation()
                }
            }
        }
    }
}

// --- Header Banner Component ---
@Composable
private fun CompoundInterestHeaderBanner() {
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
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = PTITSuccess,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.md))
            Column {
                Text(
                    text = "Tính lãi suất kép",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
                Text(
                    text = "Tính toán lợi nhuận đầu tư với lãi suất kép",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PTITTextSecondary
                )
            }
        }
    }
}

// --- Input Form Component ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompoundInterestInputForm(
    input: CompoundInterestInput,
    validationErrors: List<String>,
    onInputChange: (CompoundInterestInput) -> Unit
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
                text = "Thông tin đầu tư",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PTITTextPrimary
            )

            // Principal Amount Input
            OutlinedTextField(
                value = input.principal,
                onValueChange = { onInputChange(input.copy(principal = it)) },
                label = { Text("Số tiền gốc (VNĐ)") },
                placeholder = { Text("Ví dụ: 100000000") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITSuccess,
                    focusedLabelColor = PTITSuccess
                )
            )

            // Investment Period Input
            OutlinedTextField(
                value = input.years,
                onValueChange = { onInputChange(input.copy(years = it)) },
                label = { Text("Thời gian đầu tư (năm)") },
                placeholder = { Text("10") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITSuccess,
                    focusedLabelColor = PTITSuccess
                )
            )

            // Monthly Contribution Input
            OutlinedTextField(
                value = input.monthlyContribution,
                onValueChange = { onInputChange(input.copy(monthlyContribution = it)) },
                label = { Text("Đóng góp hàng tháng (VNĐ) - Tùy chọn") },
                placeholder = { Text("0") },
                leadingIcon = { Icon(Icons.Default.Savings, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITSuccess,
                    focusedLabelColor = PTITSuccess
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
private fun CompoundInterestCalculateButton(
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
            containerColor = PTITSuccess,
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
                text = "Tính toán",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Result Card Component ---
@Composable
private fun CompoundInterestResultCard(
    result: CompoundInterestResult,
    onClearResult: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITSuccess.copy(alpha = 0.1f)),
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
                    text = "Kết quả đầu tư",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITSuccess
                )
                Row {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = PTITSuccess
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
                    Divider(color = PTITSuccess.copy(alpha = 0.3f))

                    // Final Amount
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PTITSuccess.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(PTITSpacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Số tiền cuối kỳ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.finalAmount),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Investment Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Tổng tiền đóng góp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.totalContributions),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PTITPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Tổng lợi nhuận",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.totalInterest),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess
                            )
                        }
                    }

                    Divider(color = PTITNeutral200)

                    // Details
                    Text(
                        text = "Chi tiết tính toán",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CompoundInterestDetailRow(
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
            color = if (isHighlight) PTITSuccess else PTITTextSecondary
        )
    }
}

// --- Yearly Breakdown Card ---
@Composable
private fun YearlyBreakdownCard(breakdown: List<YearlyBreakdown>) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITInfo.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chi tiết theo năm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITInfo
                )
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = PTITInfo
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    Divider(color = PTITInfo.copy(alpha = 0.3f))
                    
                    breakdown.take(10).forEach { year ->
                        YearlyBreakdownRow(year = year)
                    }
                    
                    if (breakdown.size > 10) {
                        Text(
                            text = "... và ${breakdown.size - 10} năm khác",
                            style = MaterialTheme.typography.bodySmall,
                            color = PTITTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearlyBreakdownRow(year: YearlyBreakdown) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITNeutral50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Năm ${year.year}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PTITTextPrimary
                )
                Text(
                    text = formatCurrency(year.totalContributions),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITSuccess
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lãi năm: ${formatCurrency(year.interestEarned)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PTITTextSecondary
                )
                Text(
                    text = "Góp: ${formatCurrency(year.totalContributions)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PTITTextSecondary
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

// --- Investment Tips Information ---
@Composable
private fun InvestmentTipsInformation() {
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
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = PTITWarning,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Mẹo đầu tư thông minh",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITWarning,
                    modifier = Modifier.padding(start = PTITSpacing.sm)
                )
            }
            
            Text(
                text = "• Bắt đầu đầu tư sớm để tận dụng tối đa sức mạnh của lãi kép\n" +
                        "• Đóng góp đều đặn hàng tháng để tăng hiệu quả đầu tư\n" +
                        "• Tần suất tính lãi càng cao thì lợi nhuận càng lớn\n" +
                        "• Kiên nhẫn và không rút tiền sớm để tối ưu lợi nhuận\n" +
                        "• Đa dạng hóa danh mục đầu tư để giảm rủi ro",
                style = MaterialTheme.typography.bodySmall,
                color = PTITTextSecondary
            )
        }
    }
}