package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.component.BHXHCalculator
import com.example.ptitjob.ui.component.BHXHResultDisplay
import com.example.ptitjob.ui.screen.candidate.utilities.models.BHXHPeriod
import com.example.ptitjob.ui.screen.candidate.utilities.models.BHXHResult
import com.example.ptitjob.ui.theme.*

// --- 1. DATA MODELS ---

// --- 2. MAIN SCREEN COMPONENT ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BHXHCalculatorScreen(
    onBack: () -> Unit = {}
) {
    // --- State Management cho UI ---
    var currentTab by remember { mutableIntStateOf(0) }
    var mandatoryResult by remember { mutableStateOf<BHXHResult?>(null) }
    var voluntaryResult by remember { mutableStateOf<BHXHResult?>(null) }
    var bothResult by remember { mutableStateOf<BHXHResult?>(null) }

    val currentResult = when (currentTab) {
        0 -> mandatoryResult
        1 -> voluntaryResult
        else -> bothResult
    }

    // Hàm callback để cập nhật kết quả từ máy tính
    val handleCalculate = { result: BHXHResult ->
        when (currentTab) {
            0 -> mandatoryResult = result
            1 -> voluntaryResult = result
            2 -> bothResult = result
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tính BHXH một lần",
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
                BHXHHeaderBanner()
            }

            // --- Calculator Tabs ---
            item {
                BHXHCalculatorTabs(
                    currentTab = currentTab,
                    onTabChange = { currentTab = it }
                )
            }

            // --- Calculator Section ---
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn()
                ) {
                    BHXHCalculatorSection(
                        currentTab = currentTab,
                        onCalculate = handleCalculate
                    )
                }
            }

            // --- Result Section ---
            item {
                AnimatedVisibility(
                    visible = currentResult != null,
                    enter = slideInVertically() + fadeIn()
                ) {
                    currentResult?.let { result ->
                        BHXHResultSection(
                            result = result,
                            currentTab = currentTab
                        )
                    }
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


// --- 3. CHILD COMPOSABLES (Đầy đủ) ---

// --- BHXHCalculator Component ---

// --- BHXHResultDisplay Component ---

// --- Các Composable con và Helper của màn hình chính ---

@Composable
private fun BHXHHeaderBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.md
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.xl)
        ) {
            // Background decoration circle
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PTITPrimary.copy(alpha = 0.1f))
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconXxl),
                    shape = CircleShape,
                    color = PTITPrimary
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(PTITSize.iconXl)
                        )
                    }
                }
                
                Text(
                    text = "Công cụ tính bảo hiểm xã hội một lần",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    ),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Miễn phí 2025",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = PTITPrimary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BHXHCalculatorTabs(currentTab: Int, onTabChange: (Int) -> Unit) {
    val tabs = listOf(
        "BHXH bắt buộc" to Icons.Default.Work,
        "BHXH tự nguyện" to Icons.Default.VolunteerActivism,
        "Cả hai loại" to Icons.Default.Checklist
    )
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.md)
        ) {
            tabs.forEachIndexed { index, (title, icon) ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = PTITSpacing.xs),
                    shape = PTITCornerRadius.md,
                    color = if (currentTab == index) PTITPrimary else Color.Transparent,
                    onClick = { onTabChange(index) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PTITSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (currentTab == index) Color.White else PTITTextSecondary,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (currentTab == index) Color.White else PTITTextPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BHXHCalculatorSection(
    currentTab: Int,
    onCalculate: (BHXHResult) -> Unit
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
                    imageVector = Icons.Default.Calculate,
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
            
            val calculatorType = when (currentTab) {
                0 -> "mandatory"
                1 -> "voluntary"
                else -> "both"
            }
            BHXHCalculator(
                type = calculatorType,
                onCalculate = onCalculate,
            )
        }
    }
}

@Composable
private fun BHXHResultSection(
    result: BHXHResult,
    currentTab: Int
) {
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
                    imageVector = Icons.Default.Assessment,
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
            
            val resultType = when (currentTab) {
                0 -> "mandatory"
                1 -> "voluntary"
                else -> "both"
            }
            BHXHResultDisplay(
                result = result,
                type = resultType
            )
        }
    }
}

@Composable
private fun BHXHInformationSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.md
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
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Thông tin về bảo hiểm xã hội một lần",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            
            BHXHInfoCard(
                title = "Bảo hiểm xã hội một lần là gì?",
                content = "Căn cứ theo Điều 3 Luật Bảo hiểm xã hội 2014, bảo hiểm xã hội một lần là sự bù đắp một phần thu nhập dành cho người lao động khi người lao động bị ốm đau, thai sản hay bị tai nạn lao động, bệnh nghề nghiệp, hết tuổi lao động hoặc chết.",
                icon = Icons.Default.Description
            )
            
            BHXHInfoCard(
                title = "Điều kiện hưởng BHXH một lần",
                content = "",
                icon = Icons.Default.Checklist
            )
            
            BHXHConditionCard(
                title = "1. Đối với người lao động đã tham gia BHXH trước ngày 01/7/2025:",
                items = listOf(
                    "Đã nghỉ việc ít nhất 12 tháng và không tiếp tục tham gia đóng BHXH.",
                    "Chưa đủ 20 năm đóng BHXH.",
                    "Có đơn yêu cầu được hưởng BHXH một lần."
                )
            )
            
            BHXHConditionCard(
                title = "2. Đối với người lao động bắt đầu tham gia BHXH từ ngày 01/7/2025 trở đi:",
                items = listOf(
                    "Đủ tuổi nghỉ hưu nhưng thời gian đóng BHXH dưới 15 năm.",
                    "Ra nước ngoài để định cư.",
                    "Mắc các bệnh hiểm nghèo như: Ung thư, bại liệt, xơ gan mất bù, lao nặng, AIDS.",
                    "Suy giảm khả năng lao động từ 81% trở lên."
                )
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.md,
                color = PTITWarning.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "* Thông tin trên chỉ mang tính chất tham khảo. Vui lòng tham khảo thêm các quy định pháp luật chính thức được ban hành.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = PTITWarning
                    ),
                    modifier = Modifier.padding(PTITSpacing.md)
                )
            }
        }
    }
}

@Composable
private fun BHXHInfoCard(
    title: String,
    content: String,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        color = PTITInfo.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            
            if (content.isNotEmpty()) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun BHXHConditionCard(
    title: String,
    items: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        color = PTITSecondary.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                items.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            tint = PTITSecondary,
                            modifier = Modifier
                                .size(PTITSize.iconXs)
                                .padding(top = PTITSpacing.xs)
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITTextSecondary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}


// --- 4. HELPER FUNCTIONS ---


// --- 5. PREVIEW ---

@Preview(showBackground = true, widthDp = 1440, heightDp = 1800)
@Composable
fun BHXHCalculatorScreenPreview() {
    MaterialTheme {
        BHXHCalculatorScreen()
    }
}

// --- 6. SAMPLE DATA FOR PREVIEW ---
private fun getSampleResult() = BHXHResult(
    totalAmount = 150_789_456.0,
    averageSalary = 7_500_000.0,
    totalMonths = 125,
    periods = listOf(
        BHXHPeriod("1", 2020, 2022, 24, 6000000L),
        BHXHPeriod("2", 2023, null, 101, 8000000L)
    )
)