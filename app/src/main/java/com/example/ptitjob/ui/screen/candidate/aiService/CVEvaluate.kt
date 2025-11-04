package com.example.ptitjob.ui.screen.candidate.aiService

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ptitjob.ui.theme.*

// --- Data Models (Giả định) ---
data class CVEvaluationData(
    val matchScore: Int, val strengths: List<String>, val improvements: List<String>, val recommendations: List<String>
)

// --- Component Màn hình Chính ---
@Composable
fun CvEvaluationScreen() {
    // --- State Management cho UI ---
    var activeStep by remember { mutableIntStateOf(0) }
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var cvData by remember { mutableStateOf<CVEvaluationData?>(null) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PTITGradientStart,
                        PTITGradientMiddle,
                        PTITGradientEnd
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(PTITSpacing.lg)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(PTITSpacing.xl))
            
            // Header
            CvEvaluationHeader()
            
            // Main Content Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PTITCornerRadius.lg),
                color = PTITNeutral50.copy(alpha = 0.95f),
                shadowElevation = PTITElevation.md,
                tonalElevation = PTITElevation.sm
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PTITSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)
                ) {
                    // Progress Steps
                    StepProgressIndicator(activeStep = activeStep)
                    
                    // Main Stepper Content
                    CvStepper(
                        activeStep = activeStep,
                        selectedFile = selectedFile,
                        isUploading = isUploading,
                        cvData = cvData,
                        onFileSelected = {
                            selectedFile = it
                            activeStep = 1
                            isUploading = true
                            // Simulate API call
                            MainScope().launch {
                                delay(2000)
                                isUploading = false
                                cvData = getSampleCvData()
                                activeStep = 2
                            }
                        }
                    )
                }
            }
            
            // Instructions Card
            InstructionsCard()
            
            Spacer(modifier = Modifier.height(PTITSpacing.xl))
        }
    }
}


// --- Các Composable con và Section ---

@Composable
private fun CvEvaluationHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // App Icon with gradient background
        Surface(
            modifier = Modifier.size(PTITSize.iconXxxl + PTITSpacing.lg),
            shape = PTITCornerRadius.md,
            color = PTITSecondary
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "CV Evaluation",
                    modifier = Modifier.size(PTITSize.iconXxxl),
                    tint = Color.White
                )
            }
        }
        
        // Title
        Text(
            text = "Đánh giá CV bằng AI",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Phân tích CV của bạn và nhận đánh giá chi tiết từ hệ thống AI thông minh",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.9f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StepProgressIndicator(activeStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepIndicator(
            stepNumber = 1,
            title = "Tải CV",
            isActive = activeStep >= 0,
            isCompleted = activeStep > 0
        )

        // Connector line
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = if (activeStep > 0) PTITPrimary else PTITGray400
        )

        StepIndicator(
            stepNumber = 2,
            title = "Phân tích",
            isActive = activeStep >= 1,
            isCompleted = activeStep > 1
        )

        // Connector line
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = if (activeStep > 1) PTITPrimary else PTITGray400
        )

        StepIndicator(
            stepNumber = 3,
            title = "Kết quả",
            isActive = activeStep >= 2,
            isCompleted = false
        )
    }
}

@Composable
private fun StepIndicator(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
    ) {
        Surface(
            modifier = Modifier.size(PTITSize.iconXl),
            shape = PTITCornerRadius.full,
            color = when {
                isCompleted -> PTITSuccess
                isActive -> PTITPrimary
                else -> PTITGray400
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                } else {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = if (isActive || isCompleted) PTITTextPrimary else PTITTextSecondary
            )
        )
    }
}

@Composable
private fun CvStepper(
    activeStep: Int,
    selectedFile: String?,
    isUploading: Boolean,
    cvData: CVEvaluationData?,
    onFileSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)
    ) {
        // Step 1: Upload
        AnimatedVisibility(
            visible = activeStep == 0,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 1: Tải lên CV của bạn",
                icon = Icons.Default.CloudUpload,
                content = {
                    UploadBox(onFileSelected = onFileSelected)
                }
            )
        }

        // Step 2: Analysis
        AnimatedVisibility(
            visible = activeStep == 1,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 2: Phân tích và đánh giá",
                icon = Icons.Default.Psychology,
                content = {
                    if (isUploading) {
                        AnalysisProgress()
                    } else {
                        AnalysisComplete()
                    }
                }
            )
        }

        // Step 3: Results
        AnimatedVisibility(
            visible = activeStep == 2 && cvData != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 3: Kết quả đánh giá",
                icon = Icons.Default.CheckCircle,
                content = {
                    cvData?.let { ResultDisplay(cvData = it) }
                }
            )
        }
    }
}

@Composable
private fun StepContent(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Surface(
                modifier = Modifier.size(PTITSize.iconXl),
                shape = PTITCornerRadius.sm,
                color = PTITPrimary
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(PTITSize.iconLg)
                    )
                }
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
        }
        
        content()
    }
}


@Composable
private fun UploadBox(onFileSelected: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(PTITCornerRadius.lg)
            .clickable { onFileSelected("my_cv.pdf") },
        color = PTITPrimary.copy(alpha = 0.05f),
        border = BorderStroke(2.dp, PTITPrimary.copy(alpha = 0.3f)),
        shape = PTITCornerRadius.lg
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(PTITSize.iconXxxl + PTITSpacing.md),
                shape = PTITCornerRadius.full,
                color = PTITPrimary.copy(alpha = 0.1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        modifier = Modifier.size(PTITSize.iconXxxl),
                        tint = PTITPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(PTITSpacing.md))
            
            Text(
                text = "Kéo thả hoặc chọn file CV",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PTITPrimary
                )
            )
            
            Text(
                text = "Định dạng PDF • Tối đa 10MB",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                )
            )
        }
    }
}

@Composable
private fun AnalysisProgress() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(PTITSize.iconXxxl),
            color = PTITPrimary,
            strokeWidth = 4.dp
        )
        
        Text(
            text = "Đang phân tích CV của bạn...",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = PTITTextPrimary
            ),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "AI đang đánh giá kỹ năng, kinh nghiệm và độ phù hợp với thị trường việc làm",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PTITTextSecondary
            ),
            textAlign = TextAlign.Center
        )
        
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = PTITPrimary,
            trackColor = PTITGray300
        )
    }
}

@Composable
private fun AnalysisComplete() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PTITSuccess.copy(alpha = 0.1f),
        shape = PTITCornerRadius.md
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = PTITSuccess,
                modifier = Modifier.size(PTITSize.iconLg)
            )
            
            Column {
                Text(
                    text = "Phân tích hoàn tất!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITSuccess
                    )
                )
                
                Text(
                    text = "AI đã hoàn tất đánh giá CV của bạn",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun ResultDisplay(cvData: CVEvaluationData) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        // Score Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PTITPrimary,
            shape = PTITCornerRadius.lg,
            shadowElevation = PTITElevation.md
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PTITSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Text(
                    text = "${cvData.matchScore}%",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                
                Text(
                    text = when {
                        cvData.matchScore >= 85 -> "Xuất sắc"
                        cvData.matchScore >= 70 -> "Tốt"
                        cvData.matchScore >= 50 -> "Khá"
                        else -> "Cần cải thiện"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(0.9f)
                    )
                )
                
                Text(
                    text = "Điểm đánh giá CV",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(0.8f)
                    )
                )
            }
        }
        
        // Analysis Results
        ResultSection("Điểm mạnh", cvData.strengths, PTITSuccess,
            Icons.AutoMirrored.Filled.TrendingUp
        )
        ResultSection("Cần cải thiện", cvData.improvements, PTITWarning, Icons.Default.Warning)
        ResultSection("Gợi ý phát triển", cvData.recommendations, PTITInfo, Icons.Default.Lightbulb)
    }
}

@Composable
private fun ResultSection(
    title: String,
    items: List<String>,
    color: Color,
    icon: ImageVector
) {
    val suggestions = items
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.05f),
        shape = PTITCornerRadius.md,
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(PTITSize.iconLg)
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                contentPadding = PaddingValues(horizontal = PTITSpacing.xs)
            ) {
                items(items = suggestions, key = { it }) { label ->
                    SuggestionChip(
                        onClick = { /* ... */ },
                        label = {
                            Text(text = label, style = MaterialTheme.typography.bodyMedium)
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = color.copy(alpha = 0.1f),
                            labelColor = color
                        ),
                        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PTITNeutral50,
        shape = PTITCornerRadius.lg,
        shadowElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconLg)
                )
                
                Text(
                    text = "Hướng dẫn sử dụng",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                InstructionItem("1", "Tải lên file CV định dạng PDF")
                InstructionItem("2", "Chờ AI phân tích CV của bạn")
                InstructionItem("3", "Xem kết quả đánh giá chi tiết")
                InstructionItem("4", "Áp dụng các gợi ý để cải thiện CV")
            }
        }
    }
}

@Composable
private fun InstructionItem(
    number: String,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(PTITSize.iconLg),
            shape = PTITCornerRadius.full,
            color = PTITPrimary.copy(alpha = 0.1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITPrimary
                    )
                )
            }
        }
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PTITTextSecondary
            )
        )
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun CvEvaluationScreenPreview() {
    PtitjobTheme {
        CvEvaluationScreen()
    }
}

// --- Sample Data ---
private fun getSampleCvData() = CVEvaluationData(
    matchScore = 85,
    strengths = listOf("Kinh nghiệm lập trình", "Kỹ năng Android", "Học vấn tốt", "Dự án thực tế"),
    improvements = listOf("Thiếu chứng chỉ", "Cần bổ sung soft skills", "Portfolio chưa đủ"),
    recommendations = listOf("Tham gia khóa học AI", "Xây dựng portfolio", "Học thêm Kotlin", "Thực hành dự án cá nhân")
)