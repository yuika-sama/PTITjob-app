package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ptitjob.ui.screen.candidate.utilities.calculations.UnemploymentInsuranceCalculator
import com.example.ptitjob.ui.screen.candidate.utilities.calculations.formatCurrency
import com.example.ptitjob.ui.screen.candidate.utilities.models.*
import com.example.ptitjob.ui.theme.*

// --- Route Component with ViewModel Integration ---
@Composable
fun UnemploymentInsuranceRoute(
    onBack: () -> Unit,
    viewModel: UtilitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.unemploymentState.collectAsStateWithLifecycle()
    
    UnemploymentInsuranceScreen(
        uiState = uiState,
        onInputChange = viewModel::updateUnemploymentInput,
        onCalculate = viewModel::calculateUnemployment,
        onClearResult = viewModel::clearUnemploymentResult,
        onBack = onBack
    )
}

// --- Main Screen Component ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnemploymentInsuranceScreen(
    uiState: CalculatorUiState<UnemploymentInput>,
    onInputChange: (UnemploymentInput) -> Unit,
    onCalculate: () -> Unit,
    onClearResult: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bảo hiểm thất nghiệp",
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
                    containerColor = PTITPrimary,
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
                    UnemploymentHeaderBanner()
                }

                // --- Input Form ---
                item {
                    UnemploymentInputForm(
                        input = uiState.input,
                        validationErrors = uiState.validationErrors,
                        onInputChange = onInputChange
                    )
                }

                // --- Calculate Button ---
                item {
                    UnemploymentCalculateButton(
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
                    val result = uiState.result as UnemploymentResult
                    item {
                        UnemploymentResultCard(
                            result = result,
                            onClearResult = onClearResult
                        )
                    }
                }

                // --- Benefits Information ---
                item {
                    UnemploymentBenefitsInformation()
                }

                // --- Requirements Information ---
                item {
                    UnemploymentRequirementsInformation()
                }
            }
        }
    }
}

// --- Header Banner Component ---
@Composable
private fun UnemploymentHeaderBanner() {
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
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = PTITPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(PTITSpacing.md))
            Column {
                Text(
                    text = "Bảo hiểm thất nghiệp",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
                Text(
                    text = "Tính toán trợ cấp thất nghiệp theo quy định mới nhất",
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
private fun UnemploymentInputForm(
    input: UnemploymentInput,
    validationErrors: List<String>,
    onInputChange: (UnemploymentInput) -> Unit
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
                text = "Thông tin tính toán",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PTITTextPrimary
            )

            // Average Salary Input
            OutlinedTextField(
                value = input.averageSalary,
                onValueChange = { onInputChange(input.copy(averageSalary = it)) },
                label = { Text("Lương bình quân 6 tháng (VNĐ)") },
                placeholder = { Text("Ví dụ: 15000000") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    focusedLabelColor = PTITPrimary
                )
            )

            // Contribution Months Input
            OutlinedTextField(
                value = input.contributionMonths,
                onValueChange = { onInputChange(input.copy(contributionMonths = it)) },
                label = { Text("Số tháng đóng BHTN") },
                placeholder = { Text("12") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    focusedLabelColor = PTITPrimary
                )
            )

            // Age Input
            OutlinedTextField(
                value = input.currentAge,
                onValueChange = { onInputChange(input.copy(currentAge = it)) },
                label = { Text("Tuổi") },
                placeholder = { Text("30") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PTITPrimary,
                    focusedLabelColor = PTITPrimary
                )
            )

            // Region Selection
            var regionExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = regionExpanded,
                onExpandedChange = { regionExpanded = it }
            ) {
                OutlinedTextField(
                    value = "Vùng ${input.regionLevel.coerceIn(1,4)}",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Vùng lương tối thiểu") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )
                ExposedDropdownMenu(
                    expanded = regionExpanded,
                    onDismissRequest = { regionExpanded = false }
                ) {
                    (1..4).forEach { region ->
                        DropdownMenuItem(
                            text = { Text("Vùng $region") },
                            onClick = {
                                onInputChange(input.copy(regionLevel = region))
                                regionExpanded = false
                            }
                        )
                    }
                }
            }

            // Job Training Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = input.hasVocationalTraining,
                    onCheckedChange = { onInputChange(input.copy(hasVocationalTraining = it)) },
                    colors = CheckboxDefaults.colors(checkedColor = PTITPrimary)
                )
                Text(
                    text = "Tham gia học nghề/đào tạo kỹ năng",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PTITTextPrimary,
                    modifier = Modifier.padding(start = PTITSpacing.sm)
                )
            }

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
private fun UnemploymentCalculateButton(
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
            containerColor = PTITPrimary,
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
                text = "Tính trợ cấp",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Result Card Component ---
@Composable
private fun UnemploymentResultCard(
    result: UnemploymentResult,
    onClearResult: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITPrimary.copy(alpha = 0.1f)),
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
                    text = "Kết quả tính toán",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITPrimary
                )
                Row {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = PTITPrimary
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
                    Divider(color = PTITPrimary.copy(alpha = 0.3f))

                    // Main Results Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Trợ cấp hàng tháng",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.monthlyBenefit),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Tổng số tháng",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextPrimary
                            )
                            Text(
                                text = "${result.monthlyBenefit} tháng",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PTITPrimary
                            )
                        }
                    }

                    // Total Benefit
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
                                text = "Tổng trợ cấp dự kiến",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PTITTextSecondary
                            )
                            Text(
                                text = formatCurrency(result.totalBenefit),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess,
                                textAlign = TextAlign.Center
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

                    // Display basic result fields (model provides limited fields)
                    UnemploymentDetailRow("Trợ cấp hàng tháng (ước tính)", formatCurrency(result.monthlyBenefit))
                    UnemploymentDetailRow("Thời gian tối đa nhận trợ cấp", "${result.maxDuration} tháng")
                    UnemploymentDetailRow("Tổng trợ cấp dự kiến", formatCurrency(result.totalBenefit))
                    UnemploymentDetailRow("Tình trạng đủ điều kiện", result.eligibilityStatus.displayName)
                    if (result.requirements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Yêu cầu/ghi chú:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PTITTextPrimary
                        )
                        result.requirements.forEach { req ->
                            UnemploymentDetailRow("-", req)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UnemploymentDetailRow(
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
            color = if (isHighlight) PTITPrimary else PTITTextSecondary
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

// --- Benefits Information ---
@Composable
private fun UnemploymentBenefitsInformation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PTITSuccess.copy(alpha = 0.1f)),
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
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = PTITSuccess,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Mức trợ cấp thất nghiệp",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITSuccess,
                    modifier = Modifier.padding(start = PTITSpacing.sm)
                )
            }
            
            Text(
                text = "• 60% lương bình quân 6 tháng gần nhất\n" +
                        "• Tối đa 5 lần mức lương tối thiểu vùng\n" +
                        "• Thời gian hưởng: 3-12 tháng tùy số tháng đóng BHTN\n" +
                        "• Tham gia học nghề có thể kéo dài thêm 1-3 tháng",
                style = MaterialTheme.typography.bodySmall,
                color = PTITTextSecondary
            )
        }
    }
}

// --- Requirements Information ---
@Composable
private fun UnemploymentRequirementsInformation() {
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
                    imageVector = Icons.Default.AssignmentTurnedIn,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Điều kiện hưởng BHTN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PTITInfo,
                    modifier = Modifier.padding(start = PTITSpacing.sm)
                )
            }

            
            Text(
                text = "• Đóng BHTN đủ 12 tháng trở lên trong 24 tháng gần nhất\n" +
                        "• Chấm dứt hợp đồng lao động không phải do lỗi của người lao động\n" +
                        "• Đã nộp hồ sơ tại trung tâm dịch vụ việc làm\n" +
                        "• Chưa tìm được việc làm phù hợp sau 15 ngày",
                style = MaterialTheme.typography.bodyMedium,
                color = PTITTextSecondary
            )
        }
    }
}