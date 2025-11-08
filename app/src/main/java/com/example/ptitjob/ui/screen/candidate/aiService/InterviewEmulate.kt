package com.example.ptitjob.ui.screen.candidate.aiService

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ptitjob.data.model.ChatSender
import com.example.ptitjob.data.model.InterviewMessage
import com.example.ptitjob.data.model.InterviewResult
import com.example.ptitjob.data.model.InterviewSession
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITError
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITGray400
import com.example.ptitjob.ui.theme.PTITInfo
import com.example.ptitjob.ui.theme.PTITNeutral50
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITSecondary
import com.example.ptitjob.ui.theme.PTITSize
import com.example.ptitjob.ui.theme.PTITSpacing
import com.example.ptitjob.ui.theme.PTITSuccess
import com.example.ptitjob.ui.theme.PTITSurfaceVariant
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITTextSecondary
import com.example.ptitjob.ui.theme.PTITWarning
import com.example.ptitjob.ui.theme.PtitjobTheme
import com.example.ptitjob.ui.screen.candidate.aiService.copyUriToTempFile
import com.example.ptitjob.ui.screen.candidate.aiService.AiErrorBanner
import kotlinx.coroutines.launch

@Composable
fun InterviewEmulateRoute(
    onBack: () -> Unit,
    viewModel: InterviewEmulateViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                runCatching { copyUriToTempFile(context, it, prefix = "interview_cv_") }
                    .onSuccess { (file, displayName) -> viewModel.startInterview(file, displayName) }
                    .onFailure { throwable -> viewModel.dismissError() }
            }
        }
    }

    InterviewEmulateScreen(
        state = state,
        onBack = onBack,
        onSelectFile = { documentLauncher.launch(arrayOf("application/pdf")) },
        onSendMessage = viewModel::sendMessage,
        onFinishInterview = viewModel::finishInterview,
        onRetry = viewModel::reset,
        onDismissError = viewModel::dismissError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InterviewEmulateScreen(
    state: InterviewUiState,
    onBack: () -> Unit,
    onSelectFile: () -> Unit,
    onSendMessage: (String) -> Unit,
    onFinishInterview: () -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit
) {
    val scrollState = rememberScrollState()
    val activeStep = when (state.step) {
        InterviewStep.Upload -> 0
        InterviewStep.Preparing, InterviewStep.Chatting -> 1
        InterviewStep.Result -> 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phỏng vấn AI", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        // Use PTITScreenContainer for consistent background
        com.example.ptitjob.ui.component.PTITScreenContainer(
            hasGradientBackground = true
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = PTITSpacing.lg)
                    .padding(bottom = PTITSpacing.xl)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InterviewHeader()

                state.errorMessage?.let { message ->
                    AiErrorBanner(
                        message = message,
                        onDismiss = onDismissError,
                        onRetry = onRetry,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                        StepProgressIndicator(activeStep = activeStep)
                        InterviewStepper(
                            state = state,
                            onSelectFile = onSelectFile,
                            onSendMessage = onSendMessage,
                            onFinishInterview = onFinishInterview
                        )
                    }
                }

                state.session?.let { session ->
                    CvAnalysisCard(session)
                }

                InstructionsCard()
            }
        }
    }
}

@Composable
private fun InterviewHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        Surface(
            modifier = Modifier.size(PTITSize.iconXxxl + PTITSpacing.lg),
            shape = PTITCornerRadius.md,
            color = PTITInfo
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(PTITSize.iconXxxl),
                    tint = Color.White
                )
            }
        }

        Text(
            text = "Phỏng vấn AI",
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
private fun StepProgressIndicator(activeStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepIndicator(1, "Tải CV", activeStep >= 0, activeStep > 0)

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = if (activeStep > 0) PTITPrimary else PTITGray400
        )

        StepIndicator(2, "Phỏng vấn", activeStep >= 1, activeStep > 1)

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp,
            color = if (activeStep > 1) PTITPrimary else PTITGray400
        )

        StepIndicator(3, "Kết quả", activeStep >= 2, false)
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
            shape = CircleShape,
            color = when {
                isCompleted -> PTITSuccess
                isActive -> PTITPrimary
                else -> PTITGray400
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
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
    state: InterviewUiState,
    onSelectFile: () -> Unit,
    onSendMessage: (String) -> Unit,
    onFinishInterview: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)) {
        AnimatedVisibility(
            visible = state.step == InterviewStep.Upload,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 1: Tải lên CV của bạn",
                icon = Icons.Default.CloudUpload
            ) {
                UploadSection(
                    selectedFileName = state.selectedFileName,
                    session = state.session,
                    isLoading = state.isLoading,
                    onSelectFile = onSelectFile
                )
            }
        }

        AnimatedVisibility(
            visible = state.step == InterviewStep.Chatting,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 2: Phỏng vấn với AI",
                icon = Icons.Default.Psychology
            ) {
                ChatSection(
                    messages = state.messages,
                    isSessionComplete = state.step == InterviewStep.Result,
                    isLoading = state.isSending,
                    onSendMessage = onSendMessage,
                    onFinishInterview = onFinishInterview
                )
            }
        }

        AnimatedVisibility(
            visible = state.step == InterviewStep.Result && state.result != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 3: Kết quả đánh giá",
                icon = Icons.Default.Assessment
            ) {
                state.result?.let { ResultDisplay(it) }
            }
        }
    }
}

@Composable
private fun StepContent(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Surface(
                modifier = Modifier.size(PTITSize.iconXl),
                shape = PTITCornerRadius.sm,
                color = PTITPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
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
private fun UploadSection(
    selectedFileName: String?,
    session: InterviewSession?,
    isLoading: Boolean,
    onSelectFile: () -> Unit
) {
    if (session != null) {
        // Show CV analysis result
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.lg,
            color = PTITSuccess.copy(alpha = 0.1f),
            tonalElevation = PTITElevation.sm,
            border = BorderStroke(1.dp, PTITSuccess.copy(alpha = 0.3f))
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
                    Row(
                        modifier = Modifier.padding(PTITSpacing.lg),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Điểm CV",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PTITTextSecondary
                                )
                            )
                            Text(
                                text = "${session.cvScore ?: 0}%",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PTITPrimary
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Kỹ năng phù hợp",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PTITTextSecondary
                                )
                            )
                            Text(
                                text = "${session.matchedSkills.size} kỹ năng",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PTITSecondary
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "AI đã sẵn sàng tạo câu hỏi phỏng vấn dựa trên CV của bạn. Cuộc phỏng vấn sẽ bắt đầu ngay.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // Show upload area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(PTITCornerRadius.lg),
            color = PTITPrimary.copy(alpha = 0.05f),
            border = BorderStroke(2.dp, PTITPrimary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = PTITPrimary,
                        modifier = Modifier.size(PTITSize.iconXl)
                    )
                    Spacer(modifier = Modifier.height(PTITSpacing.md))
                    Text(
                        text = "Đang phân tích CV...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PTITTextSecondary
                        ),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(PTITSize.iconXxxl + PTITSpacing.md),
                        shape = CircleShape,
                        color = PTITPrimary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
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
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Định dạng PDF • Tối đa 10MB",
                        style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(PTITSpacing.md))

                    Button(
                        onClick = onSelectFile,
                        colors = ButtonDefaults.buttonColors(containerColor = PTITPrimary),
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
                            Text("Chọn file CV")
                        }
                    }

                    selectedFileName?.takeIf { it.isNotBlank() }?.let { fileName ->
                        Spacer(modifier = Modifier.height(PTITSpacing.sm))
                        Text(
                            text = "Đã chọn: $fileName",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PTITTextPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatSection(
    messages: List<InterviewMessage>,
    isSessionComplete: Boolean,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onFinishInterview: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp),
        shape = PTITCornerRadius.lg,
        color = PTITSurfaceVariant.copy(alpha = 0.5f),
        tonalElevation = PTITElevation.sm
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                state = listState,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                items(messages, key = { "${it.sender}_${it.timestamp}" }) { message ->
                    MessageItem(message = message)
                }

                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                            ) {
                                // AI Avatar
                                Surface(
                                    modifier = Modifier.size(PTITSize.iconLg),
                                    shape = CircleShape,
                                    color = PTITPrimary
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.SmartToy,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(PTITSize.iconMd)
                                        )
                                    }
                                }

                                // Typing indicator
                                Surface(
                                    shape = PTITCornerRadius.lg,
                                    color = Color.White,
                                    tonalElevation = PTITElevation.xs
                                ) {
                                    Row(
                                        modifier = Modifier.padding(PTITSpacing.md),
                                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = PTITPrimary
                                        )
                                        Text(
                                            text = "AI đang soạn câu hỏi...",
                                            style = MaterialTheme.typography.bodyMedium.copy(
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

            // Input Section
            if (!isSessionComplete) {
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
                                    text = "Nhập câu trả lời của bạn...",
                                    color = PTITTextSecondary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PTITPrimary,
                                unfocusedBorderColor = PTITTextSecondary.copy(alpha = 0.3f)
                            ),
                            shape = PTITCornerRadius.md,
                            minLines = 1,
                            maxLines = 3
                        )

                        IconButton(
                            onClick = {
                                if (input.isNotBlank()) {
                                    onSendMessage(input.trim())
                                    input = ""
                                }
                            },
                            enabled = input.isNotBlank() && !isLoading,
                            modifier = Modifier
                                .background(
                                    color = if (input.isNotBlank() && !isLoading) PTITPrimary else PTITGray400,
                                    shape = CircleShape
                                )
                                .size(PTITSize.iconXl)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Gửi",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Finish Interview Button
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PTITWarning.copy(alpha = 0.1f)
                ) {
                    Button(
                        onClick = onFinishInterview,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PTITSpacing.md),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITWarning,
                            contentColor = Color.White
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        Text(
                            text = "Kết thúc phỏng vấn",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
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
                            text = "Phỏng vấn hoàn tất! Xem kết quả đánh giá bên dưới.",
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
private fun MessageItem(message: InterviewMessage) {
    val isUser = message.sender == ChatSender.USER

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
            // AI Avatar (left side)
            if (!isUser) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconLg),
                    shape = CircleShape,
                    color = PTITPrimary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
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
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                    modifier = Modifier.padding(PTITSpacing.md)
                )
            }

            // User Avatar (right side)
            if (isUser) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconLg),
                    shape = CircleShape,
                    color = PTITSecondary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
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
private fun ResultDisplay(result: InterviewResult) {
    Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)) {
        // Overall Score Card
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
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Text(
                    text = "${String.format("%.1f", result.finalScore ?: 0.0)}%",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = result.overallAssessment ?: "Cần đánh giá",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )

                Text(
                    text = "Kết quả phỏng vấn tổng quan",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }

        // Score Breakdown
        result.breakdown?.let { breakdown ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = PTITCornerRadius.md,
                    color = PTITInfo.copy(alpha = 0.1f),
                    tonalElevation = PTITElevation.sm,
                    border = BorderStroke(1.dp, PTITInfo.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(PTITSpacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                    ) {
                        Text(
                            text = "${String.format("%.1f", breakdown.cvScore ?: 0.0)}%",
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
                    tonalElevation = PTITElevation.sm,
                    border = BorderStroke(1.dp, PTITSecondary.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(PTITSpacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                    ) {
                        Text(
                            text = "${String.format("%.1f", breakdown.interviewScore ?: 0.0)}%",
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
        }

        // Recommendation
        result.recommendation?.takeIf { it.isNotBlank() }?.let { recommendation ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PTITSecondary.copy(alpha = 0.08f),
                shape = PTITCornerRadius.md
            ) {
                Text(
                    text = recommendation,
                    style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextPrimary),
                    modifier = Modifier.padding(PTITSpacing.lg)
                )
            }
        }

        // Improvements Section
        if (result.improvements.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PTITWarning.copy(alpha = 0.1f),
                shape = PTITCornerRadius.lg,
                border = BorderStroke(1.dp, PTITWarning.copy(alpha = 0.2f))
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
                                color = PTITWarning
                            )
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        result.improvements.forEach { improvement ->
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
                                            text = improvement.area,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = PTITTextPrimary
                                            )
                                        )
                                        Text(
                                            text = improvement.tip,
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
}

@Composable
private fun CvAnalysisCard(session: InterviewSession) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = PTITCornerRadius.lg,
        shadowElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Phân tích CV",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Điểm CV",
                        style = MaterialTheme.typography.bodySmall.copy(color = PTITTextSecondary)
                    )
                    Text(
                        text = "${session.cvScore ?: 0}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITPrimary
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kỹ năng phù hợp",
                        style = MaterialTheme.typography.bodySmall.copy(color = PTITTextSecondary)
                    )
                    Text(
                        text = "${session.matchedSkills.size} kỹ năng",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PTITSecondary
                        )
                    )
                }
            }
            
            // Show candidate name if available
            session.candidateName?.takeIf { it.isNotBlank() }?.let { name ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PTITInfo,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                    Text(
                        text = "Ứng viên: $name",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
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

            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
                InstructionItem(1, "Tải lên file CV định dạng PDF")
                InstructionItem(2, "AI sẽ phân tích CV và tạo câu hỏi phù hợp")
                InstructionItem(3, "Trả lời các câu hỏi phỏng vấn")
                InstructionItem(4, "Nhận kết quả đánh giá chi tiết và gợi ý cải thiện")
            }
        }
    }
}

@Composable
private fun InstructionItem(order: Int, description: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(PTITSize.iconLg),
            shape = CircleShape,
            color = PTITPrimary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = order.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITPrimary
                    )
                )
            }
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InterviewEmulatePreview() {
    PtitjobTheme {
        InterviewEmulateScreen(
            state = InterviewUiState(
                step = InterviewStep.Chatting,
                messages = listOf(
                    InterviewMessage(
                        sender = ChatSender.AI,
                        message = "Xin chào! Hãy giới thiệu về bản thân và kinh nghiệm lập trình của bạn."
                    ),
                    InterviewMessage(
                        sender = ChatSender.USER,
                        message = "Xin chào! Tôi có 3 năm kinh nghiệm phát triển Android với Kotlin và Java."
                    )
                ),
                session = InterviewSession(
                    cvScore = 85,
                    candidateName = "Test User",
                    matchedSkills = listOf("Kotlin", "Android", "Jetpack Compose"),
                    initialMessage = null
                )
            ),
            onBack = {},
            onSelectFile = {},
            onSendMessage = {},
            onFinishInterview = {},
            onRetry = {},
            onDismissError = {}
        )
    }
}