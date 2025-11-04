package com.example.ptitjob.ui.screen.candidate.aiService

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ptitjob.ui.theme.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider

// --- Data Models (Giả định) ---
data class ChatMessage(val sender: String, val text: String)
data class CVAnalysisResult(val score: Int, val candidateName: String, val matchedSkills: List<String>)
data class InterviewResult(val finalScore: Int, val cvScore: Int, val interviewScore: Int, val improvements: List<Pair<String, String>>)

// --- Component Màn hình Chính ---
@Composable
fun InterviewEmulateScreen() {
    // --- State Management cho UI ---
    var activeStep by remember { mutableIntStateOf(0) }
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var cvAnalysis by remember { mutableStateOf<CVAnalysisResult?>(null) }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var isUploading by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var interviewResult by remember { mutableStateOf<InterviewResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

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
            InterviewHeader()

            // Error handling
            error?.let {
                ErrorState(message = it, onRetry = { error = null })
            }

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
                    InterviewStepProgressIndicator(activeStep = activeStep)

                    // Main Stepper Content
                    InterviewStepper(
                        activeStep = activeStep,
                        onFileSelected = {
                            selectedFile = it
                            isUploading = true
                            scope.launch {
                                delay(1500)
                                cvAnalysis = CVAnalysisResult(
                                    85,
                                    "Nguyễn Văn A",
                                    listOf("Kotlin", "Jetpack Compose", "Android")
                                )
                                isUploading = false
                                activeStep = 1
                            }
                        },
                        cvAnalysis = cvAnalysis,
                        onStartInterview = {
                            messages = listOf(
                                ChatMessage(
                                    "ai",
                                    "Xin chào! Hãy giới thiệu về bản thân bạn và kinh nghiệm lập trình Android của bạn."
                                )
                            )
                            activeStep = 1
                        },
                        messages = messages,
                        isFinished = isFinished,
                        onFinishInterview = {
                            interviewResult = InterviewResult(
                                78, 85, 75,
                                listOf(
                                    "Giao tiếp" to "Cần nói rõ hơn về các dự án đã thực hiện, nhấn mạnh vào kết quả đạt được.",
                                    "Kỹ thuật" to "Nên ôn lại về các nguyên tắc SOLID và cách áp dụng trong Jetpack Compose."
                                )
                            )
                            isFinished = true
                            activeStep = 2
                        },
                        interviewResult = interviewResult,
                        isUploading = isUploading
                    )
                }
            }

            // CV Info Card (nếu có)
            cvAnalysis?.let { analysis ->
                CvInfoCard(analysis)
            }

            // Instructions Card
            InterviewInstructionsCard()

            Spacer(modifier = Modifier.height(PTITSpacing.xl))
        }
    }
}

// --- Các Composable con và Section ---

@Composable
private fun InterviewHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // App Icon with gradient background
        Surface(
            modifier = Modifier.size(PTITSize.iconXxxl + PTITSpacing.lg),
            shape = PTITCornerRadius.md,
            color = PTITInfo
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Interview AI",
                    modifier = Modifier.size(PTITSize.iconXxxl),
                    tint = Color.White
                )
            }
        }

        // Title
        Text(
            text = "Giả lập phỏng vấn AI",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Trải nghiệm phỏng vấn thông minh với AI dựa trên CV của bạn",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.9f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InterviewStepProgressIndicator(activeStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InterviewStepIndicator(
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

        InterviewStepIndicator(
            stepNumber = 2,
            title = "Phỏng vấn",
            isActive = activeStep >= 1,
            isCompleted = activeStep > 1
        )

        // Connector line
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = if (activeStep > 1) PTITPrimary else PTITGray400
        )

        InterviewStepIndicator(
            stepNumber = 3,
            title = "Kết quả",
            isActive = activeStep >= 2,
            isCompleted = false
        )
    }
}

@Composable
private fun InterviewStepIndicator(
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
private fun InterviewStepper(
    activeStep: Int,
    onFileSelected: (String) -> Unit,
    cvAnalysis: CVAnalysisResult?,
    onStartInterview: () -> Unit,
    messages: List<ChatMessage>,
    isFinished: Boolean,
    onFinishInterview: () -> Unit,
    interviewResult: InterviewResult?,
    isUploading: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)
    ) {
        // Step 1: Upload CV
        AnimatedVisibility(
            visible = activeStep == 0,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            InterviewStepContent(
                title = "Bước 1: Tải lên CV của bạn",
                icon = Icons.Default.CloudUpload,
                content = {
                    if (cvAnalysis == null) {
                        UploadBox(onFileSelected = onFileSelected, isUploading = isUploading)
                    } else {
                        CvAnalysisResultSummary(cvAnalysis, onStartInterview)
                    }
                }
            )
        }

        // Step 2: Interview
        AnimatedVisibility(
            visible = activeStep == 1,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            InterviewStepContent(
                title = "Bước 2: Phỏng vấn với AI",
                icon = Icons.Default.Psychology,
                content = {
                    ChatSection(messages, isFinished, onFinishInterview)
                }
            )
        }

        // Step 3: Results
        AnimatedVisibility(
            visible = activeStep == 2 && interviewResult != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            InterviewStepContent(
                title = "Bước 3: Kết quả đánh giá",
                icon = Icons.Default.Assessment,
                content = {
                    interviewResult?.let { InterviewResultDisplay(result = it) }
                }
            )
        }
    }
}

@Composable
private fun InterviewStepContent(
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
private fun UploadBox(onFileSelected: (String) -> Unit, isUploading: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PTITCornerRadius.lg),
        color = PTITSurfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.xl),
            contentAlignment = Alignment.Center
        ) {
            if (isUploading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    CircularProgressIndicator(
                        color = PTITPrimary,
                        modifier = Modifier.size(PTITSize.iconXl)
                    )
                    Text(
                        text = "Đang phân tích CV...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PTITTextSecondary
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                ) {
                    Surface(
                        modifier = Modifier.size(PTITSize.iconXxl),
                        shape = CircleShape,
                        color = PTITPrimary.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = PTITPrimary,
                                modifier = Modifier.size(PTITSize.iconXl)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        Text(
                            text = "Kéo thả hoặc chọn file CV",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITTextPrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "PDF, tối đa 10MB",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITTextSecondary
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = { onFileSelected("my_cv.pdf") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITPrimary
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null
                            )
                            Text(
                                text = "Chọn file CV",
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
}

@Composable
private fun CvAnalysisResultSummary(cvAnalysis: CVAnalysisResult, onStartInterview: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = PTITSuccess.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
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
                    text = "Phân tích CV thành công!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITSuccess
                    )
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.md,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    Text(
                        text = "Điểm CV ước tính",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                    Text(
                        text = "${cvAnalysis.score}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITPrimary
                        )
                    )
                }
            }

            Button(
                onClick = onStartInterview,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITPrimary
                ),
                shape = PTITCornerRadius.md
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null
                    )
                    Text(
                        text = "Bắt đầu phỏng vấn",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatSection(messages: List<ChatMessage>, isFinished: Boolean, onFinishInterview: () -> Unit) {
    var input by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp),
        shape = PTITCornerRadius.lg,
        color = PTITSurfaceVariant.copy(alpha = 0.5f),
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Chat Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PTITPrimary,
                shape = PTITCornerRadius.lg.copy(
                    bottomStart = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            ) {
                Row(
                    modifier = Modifier.padding(PTITSpacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                    Text(
                        text = "Phỏng vấn với AI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            // Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(PTITSpacing.md),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                items(messages) { msg ->
                    MessageItem(msg = msg)
                }
            }

            // Input Section
            if (!isFinished) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(PTITSpacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    text = "Nhập câu trả lời...",
                                    color = PTITTextSecondary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PTITPrimary,
                                unfocusedBorderColor = PTITTextSecondary.copy(alpha = 0.3f)
                            ),
                            shape = PTITCornerRadius.md
                        )

                        IconButton(
                            onClick = onFinishInterview,
                            modifier = Modifier
                                .background(
                                    color = PTITPrimary,
                                    shape = CircleShape
                                )
                                .size(PTITSize.iconXl)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PTITSuccess.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(PTITSpacing.lg),
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
                            text = "Phỏng vấn hoàn tất",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITSuccess
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Avatar(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(999.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(Modifier.padding(6.dp), contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
private fun MessageItem(msg: ChatMessage) {
    val isUser = msg.sender == "user"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PTITSpacing.xs),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            // Avatar AI ở bên trái
            if (!isUser) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconLg),
                    shape = CircleShape,
                    color = PTITPrimary
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                    }
                }
            }

            // Message bubble
            val backgroundColor = if (isUser) PTITPrimary else Color.White
            val textColor = if (isUser) Color.White else PTITTextPrimary

            Surface(
                shape = PTITCornerRadius.lg.copy(
                    bottomEnd = if (isUser) CornerSize(PTITSpacing.xs) else CornerSize(PTITSpacing.md),
                    bottomStart = if (!isUser) CornerSize(PTITSpacing.xs) else CornerSize(PTITSpacing.md)
                ),
                color = backgroundColor,
                modifier = Modifier.widthIn(max = 280.dp),
                tonalElevation = if (!isUser) PTITElevation.xs else 0.dp
            ) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textColor
                    ),
                    modifier = Modifier.padding(PTITSpacing.md)
                )
            }

            // Avatar người dùng ở bên phải
            if (isUser) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconLg),
                    shape = CircleShape,
                    color = PTITSecondary
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InterviewResultDisplay(result: InterviewResult) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        // Overall Score Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.lg,
            color = PTITPrimary
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Text(
                    text = "${result.finalScore}%",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Kết quả: Tốt",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White.copy(0.9f)
                    )
                )
            }
        }

        // Score Breakdown
        Row(
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = PTITCornerRadius.md,
                color = PTITInfo.copy(alpha = 0.1f),
                tonalElevation = PTITElevation.sm
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                ) {
                    Text(
                        text = "${result.cvScore}%",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITInfo
                        )
                    )
                    Text(
                        text = "Điểm CV",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = PTITCornerRadius.md,
                color = PTITSecondary.copy(alpha = 0.1f),
                tonalElevation = PTITElevation.sm
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                ) {
                    Text(
                        text = "${result.interviewScore}%",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITSecondary
                        )
                    )
                    Text(
                        text = "Điểm phỏng vấn",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                }
            }
        }

        // Improvements Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.lg,
            color = PTITWarning.copy(alpha = 0.1f),
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
                        imageVector = Icons.Default.TipsAndUpdates,
                        contentDescription = null,
                        tint = PTITWarning,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                    Text(
                        text = "Điểm cần cải thiện",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                ) {
                    result.improvements.forEach { (area, tip) ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = PTITCornerRadius.md,
                            color = Color.White,
                            tonalElevation = PTITElevation.xs
                        ) {
                            Row(
                                modifier = Modifier.padding(PTITSpacing.md),
                                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Circle,
                                    contentDescription = null,
                                    tint = PTITWarning,
                                    modifier = Modifier.size(PTITSize.iconXs)
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                                ) {
                                    Text(
                                        text = area,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = PTITTextPrimary
                                        )
                                    )
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = PTITTextSecondary
                                        )
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

@Composable
private fun InstructionsSidebar() {
    OutlinedCard {
        Column(Modifier.padding(16.dp)) {
            Text("💡 Hướng dẫn sử dụng", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("1. Tải lên file CV định dạng PDF.", style = MaterialTheme.typography.bodyMedium)
            Text("2. Chờ AI phân tích và tạo câu hỏi.", style = MaterialTheme.typography.bodyMedium)
            Text("3. Trả lời các câu hỏi phỏng vấn.", style = MaterialTheme.typography.bodyMedium)
            Text("4. Nhận kết quả đánh giá chi tiết.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CvInfoSidebar(cvAnalysis: CVAnalysisResult) {
    OutlinedCard {
        Column(Modifier.padding(16.dp)) {
            Text("📊 Thông tin CV", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Ứng viên: ${cvAnalysis.candidateName}", fontWeight = FontWeight.SemiBold)
            Text("Điểm CV: ${cvAnalysis.score}%")
            Spacer(Modifier.height(8.dp))
            Text("Kỹ năng phù hợp:", fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                cvAnalysis.matchedSkills.forEach { skill ->
                    SuggestionChip(onClick = {}, label = { Text(skill) })
                }
            }
        }
    }
}

// --- Bổ sung: CvInfoCard (full width) & InterviewInstructionsCard ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CvInfoCard(cv: CVAnalysisResult) {
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
                Icon(Icons.Filled.Assessment, contentDescription = null, tint = PTITInfo)
                Text(
                    "Tổng quan CV",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg)) {
                Column(Modifier.weight(1f)) {
                    Text("Ứng viên", color = PTITTextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(cv.candidateName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Column(Modifier.weight(1f)) {
                    Text("Điểm CV", color = PTITTextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text("${cv.score}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = PTITPrimary)
                }
            }
            Text("Kỹ năng phù hợp", color = PTITTextSecondary, style = MaterialTheme.typography.bodySmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                cv.matchedSkills.forEach { s ->
                    AssistChip(onClick = {}, label = { Text(s) })
                }
            }
        }
    }
}

@Composable
private fun InterviewInstructionsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = PTITSurfaceVariant.copy(alpha = 0.5f),
        tonalElevation = PTITElevation.sm
    ) {
        Column(Modifier.padding(PTITSpacing.xl), verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = PTITPrimary)
                Text("Hướng dẫn", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Text("1) Tải CV PDF của bạn. 2) Bắt đầu phỏng vấn và trả lời trong khung chat. 3) Xem kết quả và gợi ý cải thiện.")
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PTITError.copy(alpha = 0.1f),
        shape = PTITCornerRadius.md
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Error, contentDescription = null, tint = PTITError)
            Text(message, color = PTITError, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onRetry) { Text("Thử lại") }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 1200, heightDp = 1200)
@Composable
fun InterviewEmulateScreenPreview() {
    MaterialTheme {
        InterviewEmulateScreen()
    }
}
