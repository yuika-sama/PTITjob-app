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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ptitjob.data.model.CvEvaluationResult
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITGray300
import com.example.ptitjob.ui.theme.PTITGray400
import com.example.ptitjob.ui.theme.PTITInfo
import com.example.ptitjob.ui.theme.PTITNeutral50
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITSecondary
import com.example.ptitjob.ui.theme.PTITSize
import com.example.ptitjob.ui.theme.PTITSpacing
import com.example.ptitjob.ui.theme.PTITSuccess
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITTextSecondary
import com.example.ptitjob.ui.theme.PTITWarning
import com.example.ptitjob.ui.theme.PtitjobTheme
import com.example.ptitjob.ui.screen.candidate.aiService.copyUriToTempFile
import com.example.ptitjob.ui.screen.candidate.aiService.AiErrorBanner
import kotlinx.coroutines.launch

@Composable
fun CvEvaluationRoute(
    onBack: () -> Unit,
    viewModel: CvEvaluationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                scope.launch {
                    val result = runCatching { copyUriToTempFile(context, uri, prefix = "cv_") }
                    result.onSuccess { (file, displayName) ->
                        viewModel.evaluateCv(file, displayName)
                    }.onFailure { throwable ->
                        viewModel.reportError(throwable.message ?: "Không thể đọc file CV đã chọn.")
                    }
                }
            }
        }
    )

    CvEvaluationScreen(
        state = state,
        onBack = onBack,
        onSelectFile = {
            documentLauncher.launch(arrayOf("application/pdf"))
        },
        onRetry = viewModel::reset,
        onDismissError = viewModel::dismissError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CvEvaluationScreen(
    state: CvEvaluationUiState,
    onBack: () -> Unit,
    onSelectFile: () -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit
) {
    val scrollState = rememberScrollState()
    val activeStep = when (state.step) {
        CvEvaluationStep.Upload -> 0
        CvEvaluationStep.Analyzing -> 1
        CvEvaluationStep.Result -> 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đánh giá CV", fontWeight = FontWeight.SemiBold) },
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
                    .padding(bottom = PTITSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CvEvaluationHeader()

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
                        CvStepper(state = state, onSelectFile = onSelectFile)
                    }
                }

                InstructionsCard()
            }
        }
    }
}

@Composable
private fun CvEvaluationHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        Surface(
            modifier = Modifier.size(PTITSize.iconXxxl + PTITSpacing.lg),
            shape = PTITCornerRadius.md,
            color = PTITSecondary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    modifier = Modifier.size(PTITSize.iconXxxl),
                    tint = Color.White
                )
            }
        }

        Text(
            text = "Đánh giá CV bằng AI",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Phân tích CV và nhận đánh giá chi tiết từ hệ thống AI",
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

        StepIndicator(2, "Phân tích", activeStep >= 1, activeStep > 1)

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
private fun CvStepper(
    state: CvEvaluationUiState,
    onSelectFile: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.xl)) {
        AnimatedVisibility(
            visible = state.step == CvEvaluationStep.Upload,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 1: Tải lên CV của bạn",
                icon = Icons.Default.CloudUpload
            ) {
                UploadBox(
                    selectedFileName = state.selectedFileName,
                    onSelectFile = onSelectFile,
                    isLoading = state.isLoading
                )
            }
        }

        AnimatedVisibility(
            visible = state.step == CvEvaluationStep.Analyzing,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 2: Đang phân tích CV",
                icon = Icons.Default.Psychology
            ) {
                AnalysisProgress()
            }
        }

        AnimatedVisibility(
            visible = state.step == CvEvaluationStep.Result && state.evaluation != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            StepContent(
                title = "Bước 3: Kết quả đánh giá",
                icon = Icons.Default.CheckCircle
            ) {
                state.evaluation?.let { ResultDisplay(it) }
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
private fun UploadBox(
    selectedFileName: String?,
    onSelectFile: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(PTITCornerRadius.lg)
            .clickable(enabled = !isLoading) { onSelectFile() },
        color = PTITPrimary.copy(alpha = 0.05f),
        border = BorderStroke(2.dp, PTITPrimary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
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

            selectedFileName?.takeIf { it.isNotBlank() }?.let { fileName ->
                Surface(
                    color = Color.White,
                    border = BorderStroke(1.dp, PTITPrimary.copy(alpha = 0.2f)),
                    shape = PTITCornerRadius.md
                ) {
                    Text(
                        text = "Đã chọn: $fileName",
                        style = MaterialTheme.typography.bodySmall.copy(color = PTITTextPrimary),
                        modifier = Modifier.padding(horizontal = PTITSpacing.md, vertical = PTITSpacing.xs)
                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(color = PTITPrimary)
            }
        }
    }
}

@Composable
private fun AnalysisProgress() {
    Column(
        modifier = Modifier.fillMaxWidth(),
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
            text = "AI đang đánh giá kỹ năng, kinh nghiệm và phù hợp với thị trường việc làm",
            style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary),
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
private fun ResultDisplay(cvResult: CvEvaluationResult) {
    Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)) {
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
                    text = "${cvResult.score}%",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = when {
                        cvResult.score >= 85 -> "Xuất sắc"
                        cvResult.score >= 70 -> "Tốt"
                        cvResult.score >= 50 -> "Khá"
                        else -> "Cần cải thiện"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )

                Text(
                    text = "Điểm đánh giá tổng quan",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }

        cvResult.summary?.takeIf { it.isNotBlank() }?.let { summary ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PTITSecondary.copy(alpha = 0.08f),
                shape = PTITCornerRadius.md
            ) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextPrimary),
                    modifier = Modifier.padding(PTITSpacing.lg)
                )
            }
        }

        if (cvResult.strengths.isNotEmpty()) {
            ResultSection(
                title = "Điểm mạnh",
                items = cvResult.strengths,
                color = PTITSuccess,
                icon = Icons.AutoMirrored.Filled.TrendingUp
            )
        }

        if (cvResult.improvements.isNotEmpty()) {
            ResultSection(
                title = "Cần cải thiện",
                items = cvResult.improvements,
                color = PTITWarning,
                icon = Icons.Default.Warning
            )
        }

        if (cvResult.recommendations.isNotEmpty()) {
            ResultSection(
                title = "Gợi ý phát triển",
                items = cvResult.recommendations,
                color = PTITInfo,
                icon = Icons.Default.Lightbulb
            )
        }
    }
}

@Composable
private fun ResultSection(
    title: String,
    items: List<String>,
    color: Color,
    icon: ImageVector
) {
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
                items(items, key = { it }) { label ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = color.copy(alpha = 0.1f),
                            labelColor = color
                        ),
                        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
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
                InstructionItem(2, "Chờ hệ thống AI phân tích CV")
                InstructionItem(3, "Xem kết quả đánh giá chi tiết")
                InstructionItem(4, "Áp dụng gợi ý để cải thiện CV")
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
private fun CvEvaluationPreview() {
    PtitjobTheme {
        CvEvaluationScreen(
            state = CvEvaluationUiState(
                step = CvEvaluationStep.Result,
                evaluation = CvEvaluationResult(
                    score = 86,
                    summary = "CV của bạn thể hiện nền tảng kỹ thuật vững vàng và kinh nghiệm thực tế phong phú.",
                    strengths = listOf("Kinh nghiệm Android", "Kỹ năng Kotlin", "Dự án mã nguồn mở"),
                    improvements = listOf("Bổ sung chứng chỉ", "Nhấn mạnh kết quả định lượng"),
                    recommendations = listOf("Cập nhật portfolio", "Bổ sung phần kỹ năng mềm")
                )
            ),
            onBack = {},
            onSelectFile = {},
            onRetry = {},
            onDismissError = {}
        )
    }
}