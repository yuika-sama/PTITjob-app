package com.example.ptitjob.ui.screen.candidate.jobs.jobDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ptitjob.data.model.JobDetailsPageData
import com.example.ptitjob.ui.component.JobApplicationDialog
import com.example.ptitjob.ui.component.JobInfo
import com.example.ptitjob.ui.component.JobListCard
import com.example.ptitjob.ui.component.JobListCardData
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITError
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITInfo
import com.example.ptitjob.ui.theme.PTITNeutral100
import com.example.ptitjob.ui.theme.PTITNeutral200
import com.example.ptitjob.ui.theme.PTITNeutral50
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITSecondary
import com.example.ptitjob.ui.theme.PTITSize
import com.example.ptitjob.ui.theme.PTITSpacing
import com.example.ptitjob.ui.theme.PTITSuccess
import com.example.ptitjob.ui.theme.PTITTextLight
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITTextSecondary
import com.example.ptitjob.ui.theme.PTITWarning

@Composable
fun JobDetailsRoute(
    onBack: () -> Unit,
    onNavigateToCompany: (String) -> Unit,
    onNavigateToJob: (String) -> Unit,
    viewModel: JobDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    JobDetailsScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::refresh,
        onToggleSave = viewModel::toggleSave,
        onOpenApply = viewModel::openApplicationDialog,
        onCloseApply = viewModel::closeApplicationDialog,
        onCompanySelected = onNavigateToCompany,
        onRelatedJobSelected = onNavigateToJob
    )
}

private const val RELATED_JOBS_DISPLAY_LIMIT = 3

@Composable
fun JobDetailsScreen(
    state: JobDetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggleSave: () -> Unit,
    onOpenApply: () -> Unit,
    onCloseApply: () -> Unit,
    onCompanySelected: (String) -> Unit,
    onRelatedJobSelected: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.job?.id) {
        isVisible = state.job != null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                    startY = 0f,
                    endY = 800f
                )
            )
    ) {
        val job = state.job
        when {
            state.isLoading && job == null -> JobDetailLoadingState()
            state.errorMessage != null && job == null -> JobDetailErrorState(
                message = state.errorMessage,
                onRetry = onRetry,
                onGoBack = onBack
            )
            job != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = PTITSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                ) {
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                        ) {
                            JobDetailHeader(
                                job = job,
                                isSaved = state.isSaved,
                                onSaveJob = onToggleSave,
                                onApplyJob = onOpenApply,
                                onGoBack = onBack
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                        ) {
                            JobQuickInfoSection(job = job)
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
                        ) {
                            JobDescriptionSection(job = job)
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn()
                        ) {
                            JobRequirementsSection(job = job)
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 5 }) + fadeIn()
                        ) {
                            JobBenefitsSection(job = job)
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 6 }) + fadeIn()
                        ) {
                            CompanyInfoSection(
                                job = job,
                                company = state.company,
                                onCompanySelected = onCompanySelected
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 7 }) + fadeIn()
                        ) {
                            RelatedJobsSection(
                                jobs = state.relatedJobs,
                                onJobSelected = { related -> onRelatedJobSelected(related.backendId) }
                            )
                        }
                    }
                }

                FloatingActionSection(
                    isSaved = state.isSaved,
                    onSaveJob = onToggleSave,
                    onApplyJob = onOpenApply
                )
            }
        }

        if (state.isApplicationDialogVisible && state.job != null) {
            val info = state.job
            JobApplicationDialog(
                open = true,
                onClose = onCloseApply,
                job = JobInfo(id = info.id, title = info.title, companyName = info.companyName)
            )
        }
    }
}

@Composable
private fun JobDetailHeader(
    job: JobDetailsPageData,
    isSaved: Boolean,
    onSaveJob: () -> Unit,
    onApplyJob: () -> Unit,
    onGoBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Back Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                IconButton(
                    onClick = onGoBack,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PTITTextLight
                    )
                }
                Text(
                    text = "Chi tiết việc làm",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextLight
                    )
                )
            }

            // Job Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PTITCornerRadius.lg,
                color = Color.White,
                shadowElevation = PTITElevation.lg
            ) {
                Column(
                    modifier = Modifier.padding(PTITSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.lg),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Company Logo
                        Surface(
                            shape = PTITCornerRadius.lg,
                            color = PTITNeutral50,
                            modifier = Modifier.size(PTITSize.avatarXl)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = null,
                                    tint = PTITTextSecondary,
                                    modifier = Modifier.size(PTITSize.iconXl)
                                )
                            }
                        }

                        // Job Details
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                        ) {
                            Text(
                                text = job.title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PTITTextPrimary
                                )
                            )
                            Text(
                                text = job.companyName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = PTITSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            // Job Tags (placeholder)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                            ) {
                                items(listOf("Full-time", "Remote", "Senior")) { tag ->
                                    Surface(
                                        shape = PTITCornerRadius.sm,
                                        color = PTITPrimary.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = PTITPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            modifier = Modifier.padding(
                                                horizontal = PTITSpacing.sm,
                                                vertical = PTITSpacing.xs
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Save Button
//                        IconButton(
//                            onClick = onSaveJob,
//                            modifier = Modifier
//                                .background(
//                                    if (isSaved) PTITError.copy(alpha = 0.1f) else PTITNeutral100,
//                                    CircleShape
//                                )
//                                .size(PTITSize.buttonMd)
//                        ) {
//                            Icon(
//                                if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
//                                contentDescription = if (isSaved) "Unsave" else "Save",
//                                tint = if (isSaved) PTITError else PTITTextSecondary
//                            )
//                        }
                    }

                    // Main Action Button
                    Button(
                        onClick = onApplyJob,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(PTITSize.buttonMd),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITPrimary
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(PTITSize.iconMd)
                        )
                        Spacer(Modifier.width(PTITSpacing.sm))
                        Text(
                            "Ứng tuyển ngay",
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

@Composable
private fun JobQuickInfoSection(job: JobDetailsPageData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.md
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Text(
                text = "Thông tin chi tiết",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickInfoItem(
                    icon = Icons.Default.AttachMoney,
                    title = "Mức lương",
                    value = (job.salary),
                    color = PTITSuccess
                )
                QuickInfoItem(
                    icon = Icons.Default.LocationOn,
                    title = "Địa điểm",
                    value = job.locationName,
                    color = PTITInfo
                )
                QuickInfoItem(
                    icon = Icons.Default.Work,
                    title = "Hình thức",
                    value = job.jobType,
                    color = PTITWarning
                )
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, color = PTITNeutral200)

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickInfoItem(
                    icon = Icons.Default.Schedule,
                    title = "Hạn nộp",
                    value = job.deadline,
                    color = PTITError
                )
                QuickInfoItem(
                    icon = Icons.Default.Category,
                    title = "Ngành nghề",
                    value = job.categoryName,
                    color = PTITSecondary
                )
                QuickInfoItem(
                    icon = Icons.Default.AccessTime,
                    title = "Đăng tuyển",
                    value = job.createdAt,
                    color = PTITInfo
                )
            }
        }
    }
}

@Composable
private fun QuickInfoItem(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f))
                .padding(vertical = PTITSpacing.md)
            ,
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.width(PTITSpacing.md).height(PTITSpacing.md))
        Text(title, style = MaterialTheme.typography.titleMedium, color = PTITTextSecondary)
        Spacer(modifier = Modifier.width(PTITSpacing.md).height(PTITSpacing.md))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = PTITTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTITSpacing.lg),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.md
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            content()
        }
    }
}

private fun toItems(text: String?): List<String> =
    text
        ?.split('\n', '•', '-', '–', ';')
        ?.map { it.trim().trimStart('•', '-', '–').trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

@Composable
private fun Bullet(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // điểm tròn
        Box(modifier = Modifier.padding(top = 7.dp)) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(PTITPrimary)
            )
        }
        // nội dung
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = PTITTextPrimary
        )
    }
}

@Composable
private fun JobDescriptionSection(job: JobDetailsPageData) {
    SectionCard(title = "Mô tả công việc") {
        val items = toItems(job.description)
        Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
            items.forEach { Bullet(it) }
        }
    }
}

@Composable
private fun JobRequirementsSection(job: JobDetailsPageData) {
    SectionCard(title = "Yêu cầu công việc") {
        val items = toItems(job.requirements)
        Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
            items.forEach { Bullet(it) }
        }
    }
}

@Composable
private fun JobBenefitsSection(job: JobDetailsPageData) {
    SectionCard(title = "Quyền lợi") {
        val items = toItems(job.benefits)
        Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
            items.forEach { Bullet(it) }
        }
    }
}

@Composable
private fun CompanyInfoSection(
    job: JobDetailsPageData,
    company: JobCompanyInfo?,
    onCompanySelected: (String) -> Unit
) {
    SectionCard(title = "Thông tin công ty") {
        Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
            Text(
                text = "Công ty: ${company?.name ?: job.companyName}",
                style = MaterialTheme.typography.bodyLarge
            )
            company?.size?.takeIf { it.isNotBlank() }?.let {
                Text("Quy mô: $it", style = MaterialTheme.typography.bodyLarge)
            }
            Text("Lĩnh vực: ${job.categoryName}", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Địa điểm: ${company?.address ?: job.locationName}",
                style = MaterialTheme.typography.bodyLarge
            )
            company?.website?.takeIf { it.isNotBlank() }?.let {
                Text("Website: $it", style = MaterialTheme.typography.bodyLarge)
            }
            company?.email?.takeIf { it.isNotBlank() }?.let {
                Text("Email: $it", style = MaterialTheme.typography.bodyLarge)
            }
            company?.description?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextSecondary)
                )
            }
            Spacer(modifier = Modifier.height(PTITSpacing.sm))
            OutlinedButton(onClick = { onCompanySelected(company?.id ?: job.companyId) }) {
                Icon(Icons.Default.Business, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Xem trang công ty")
            }
        }
    }
}

@Composable
private fun RelatedJobsSection(
    jobs: List<JobListCardData>,
    onJobSelected: (JobListCardData) -> Unit
) {
    SectionCard(title = "Công việc liên quan") {
        if (jobs.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🔍 Đang tìm kiếm công việc liên quan...\n\nChúng tôi sẽ gợi ý những công việc phù hợp với bạn!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = PTITTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)) {
                jobs.take(RELATED_JOBS_DISPLAY_LIMIT).forEach { relatedJob ->
                    JobListCard(
                        job = relatedJob,
                        onApply = { onJobSelected(relatedJob) },
                        onSave = { /* TODO: integrate saved jobs */ },
                        onCardClick = onJobSelected
                    )
                }
            }
        }
    }
}

// Loading, Error, and Floating Action Components
@Composable
private fun JobDetailLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            CircularProgressIndicator(color = PTITPrimary)
            Text(
                "⏳ Đang tải thông tin việc làm...\n\nVui lòng chờ một chút nhé!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextLight,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun JobDetailErrorState(
    message: String,
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.xl),
            shape = PTITCornerRadius.lg,
            color = Color.White,
            shadowElevation = PTITElevation.lg
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PTITError.copy(alpha = 0.1f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = PTITError,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Text(
                    text = "Không thể tải thông tin 😕",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )

                Text(
                    text = "$message\n\nĐừng lo! Hãy thử lại hoặc quay về trang trước nhé.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITTextSecondary,
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    OutlinedButton(
                        onClick = onGoBack,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PTITTextSecondary
                        ),
                        border = BorderStroke(1.dp, PTITNeutral200),
                        shape = PTITCornerRadius.md
                    ) {
                        Text("Quay lại", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITError
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Spacer(Modifier.width(PTITSpacing.sm))
                        Text("Thử lại", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingActionSection(
    isSaved: Boolean,
    onSaveJob: () -> Unit,
    onApplyJob: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.lg),
            shape = PTITCornerRadius.lg,
            color = Color.White,
            shadowElevation = PTITElevation.xl
        ) {
            Row(
                modifier = Modifier.padding(PTITSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                IconButton(
//                    onClick = onSaveJob,
//                    modifier = Modifier
//                        .background(
//                            if (isSaved) PTITError.copy(alpha = 0.1f) else PTITNeutral100,
//                            CircleShape
//                        )
//                        .size(PTITSize.buttonMd)
//                ) {
//                    Icon(
//                        if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
//                        contentDescription = if (isSaved) "Unsave" else "Save",
//                        tint = if (isSaved) PTITError else PTITTextSecondary
//                    )
//                }

                Button(
                    onClick = onApplyJob,
                    modifier = Modifier
                        .weight(1f)
                        .height(PTITSize.buttonMd),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PTITPrimary
                    ),
                    shape = PTITCornerRadius.md
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                    Spacer(Modifier.width(PTITSpacing.sm))
                    Text(
                        "Ứng tuyển ngay",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

