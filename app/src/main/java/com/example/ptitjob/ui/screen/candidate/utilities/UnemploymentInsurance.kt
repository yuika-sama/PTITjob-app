package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

// --- Data Models ---
data class FormData(
    val applicableDate: String = "new",
    val salaryChangeType: String = "noChange",
    val monthlySalary: Long = 6_000_000,
    val totalMonths: Int = 12,
    val employeeType: String = "state",
    val region: Int = 1
)

data class CalculationResult(
    val monthlyBenefit: Double,
    val maxMonthlyBenefit: Double,
    val benefitPeriod: Int,
    val totalBenefit: Double
)

// --- Component Chính ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnemploymentInsuranceScreen(
    onBack: () -> Unit = {}
) {
    // --- State Management cho UI ---
    var formData by remember { mutableStateOf(FormData()) }
    var result by remember { mutableStateOf<CalculationResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Giả lập tính toán khi state thay đổi
    LaunchedEffect(formData) {
        if (formData.monthlySalary > 0 && formData.totalMonths >= 12) {
            error = null
            val base = (formData.monthlySalary.toDouble() * 0.6) // ✅ ép Double
            val capped = base.coerceAtMost(24_800_000.0)
            result = CalculationResult(
                monthlyBenefit = capped,
                maxMonthlyBenefit = 24_800_000.0,
                benefitPeriod = 3,
                totalBenefit = capped * 3
            )
        } else {
            result = null
        }
    }

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
                    PTITBackgroundLight
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(PTITSpacing.md),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                // Header Section
                item { UnemploymentHeaderSection() }

                // Form Section
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        UnemploymentFormSection(
                            formData = formData,
                            onFormChange = { formData = it },
                            onCalculate = {  }
                        )
                    }
                }

                // Result Section
                item {
                    AnimatedVisibility(
                        visible = result != null || error != null,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        UnemploymentResultSection(
                            result = result,
                            error = error,
                            baseSalary = 2_340_000L,  // ✅ Long
                            minWage = 4_960_000L      // ✅ Long
                        )
                    }
                }
            }
        }
    }
}


// --- Các Composable con ---

@Composable
private fun UnemploymentHeaderSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
          .background(
                brush = Brush.linearGradient(
                    listOf(PTITPrimary, PTITSecondary)
                ),
                shape = PTITCornerRadius.lg
            ),
        shape = PTITCornerRadius.lg,
        color = Color.Transparent,
        tonalElevation = PTITElevation.md
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.xl)
        ) {
            // Decorative elements
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.1f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-40).dp, y = 40.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.05f))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconXxl),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(PTITSize.iconXl)
                        )
                    }
                }

                Text(
                    text = "Công cụ tính mức hưởng bảo hiểm thất nghiệp",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Tính nhanh, chính xác theo quy định mới nhất",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(0.9f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun UnemploymentFormSection(
    formData: FormData,
    onFormChange: (FormData) -> Unit,
    onCalculate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = PTITPrimary,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Nhập thông tin tính toán",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                OutlinedTextField(
                    value = formData.monthlySalary.toString(),
                    onValueChange = {
                        onFormChange(formData.copy(monthlySalary = it.toLongOrNull() ?: 0))
                    },
                    label = { Text("Lương đóng BHTN") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.MonetizationOn, null, tint = PTITPrimary)
                    },
                    suffix = { Text("VND") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    ),
                    shape = PTITCornerRadius.md
                )

                OutlinedTextField(
                    value = formData.totalMonths.toString(),
                    onValueChange = {
                        onFormChange(formData.copy(totalMonths = it.toIntOrNull() ?: 0))
                    },
                    label = { Text("Tổng tháng đóng BHTN") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, null, tint = PTITPrimary)
                    },
                    suffix = { Text("tháng") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    ),
                    shape = PTITCornerRadius.md
                )

                Button(
                    onClick = onCalculate,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PTITPrimary
                    ),
                    shape = PTITCornerRadius.md,
                    contentPadding = PaddingValues(PTITSpacing.md)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                        Text(
                            text = "Tính bảo hiểm thất nghiệp",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnemploymentResultSection(
    result: CalculationResult?,
    error: String?,
    baseSalary: Long,
    minWage: Long
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        if (error != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.lg,
                color = PTITError.copy(alpha = 0.1f),
                tonalElevation = PTITElevation.sm
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = PTITError,
                        modifier = Modifier.size(PTITSize.iconLg)
                    )
                    Text(
                        text = "Lỗi tính toán",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITError
                        )
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        result?.let { calculationResult ->
            // Main Result Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.lg,
                color = PTITSuccess.copy(alpha = 0.1f),
                tonalElevation = PTITElevation.sm
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PTITSuccess,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                        Text(
                            text = "Kết quả tính toán",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITTextPrimary
                            )
                        )
                    }

                    // Monthly Benefit
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = PTITCornerRadius.md,
                        color = Color.White,
                        tonalElevation = PTITElevation.xs
                    ) {
                        Column(
                            modifier = Modifier.padding(PTITSpacing.lg),
                            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Mức hưởng hàng tháng",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = PTITTextSecondary
                                    )
                                )

                                // ✅ PlainTooltipBox (API mới)
                                val tooltipState = rememberTooltipState()
                                TooltipBox(
                                    state = tooltipState,
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = {
                                        // Có thể dùng chỉ Text(...) nếu version của bạn không có PlainTooltip
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                                        ) {
                                            Text(
                                                text = "Công thức: 60% × lương bình quân",
                                                color = MaterialTheme.colorScheme.surface,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                ) {
                                    IconButton(onClick = { /* có thể không làm gì, TooltipBox vẫn hoạt động khi hover/focus */ }) {
                                        Icon(
                                            Icons.Default.Info,
                                            null,
                                            tint = PTITInfo,
                                            modifier = Modifier.size(PTITSize.iconSm))
                                    }
                                }
                            }
                            Text(
                                text = formatCurrency(calculationResult.monthlyBenefit),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PTITSuccess
                                )
                            )
                        }
                    }

                    // Additional Details
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = PTITCornerRadius.md,
                            color = PTITInfo.copy(alpha = 0.1f),
                            tonalElevation = PTITElevation.xs
                        ) {
                            Column(
                                modifier = Modifier.padding(PTITSpacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                            ) {
                                Text(
                                    text = "${calculationResult.benefitPeriod}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PTITInfo
                                    )
                                )
                                Text(
                                    text = "tháng",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PTITTextSecondary
                                    )
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = PTITCornerRadius.md,
                            color = PTITSecondary.copy(alpha = 0.1f),
                            tonalElevation = PTITElevation.xs
                        ) {
                            Column(
                                modifier = Modifier.padding(PTITSpacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                            ) {
                                Text(
                                    text = formatCurrency(calculationResult.totalBenefit),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PTITSecondary
                                    )
                                )
                                Text(
                                    text = "Tổng số tiền",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PTITTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Additional Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.lg,
                color = Color.White,
                tonalElevation = PTITElevation.sm
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PTITInfo,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                        Text(
                            text = "Thông tin quan trọng",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITTextPrimary
                            )
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        UnemploymentInfoRow(
                            label = "Mức lương cơ sở",
                            value = formatCurrency(baseSalary.toDouble()),
                            icon = Icons.Default.Money
                        )
                        UnemploymentInfoRow(
                            label = "Mức lương tối thiểu vùng",
                            value = formatCurrency(minWage.toDouble()),
                            icon = Icons.Default.Paid
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnemploymentInfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PTITInfo,
            modifier = Modifier.size(PTITSize.iconSm)
        )
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PTITTextSecondary
            )
        )
    }
}

// --- Helper Functions ---
private fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)

// --- Preview ---
@Preview(showBackground = true, widthDp = 1100)
@Composable
fun UnemploymentInsuranceScreenPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            UnemploymentInsuranceScreen()
        }
    }
}


