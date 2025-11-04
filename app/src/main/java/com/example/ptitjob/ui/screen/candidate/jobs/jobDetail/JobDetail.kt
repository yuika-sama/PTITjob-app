package com.example.ptitjob.ui.screen.candidate.jobs.jobDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.HorizontalDivider
import com.example.ptitjob.data.model.JobDetailsPageData
import com.example.ptitjob.ui.component.*
import com.example.ptitjob.ui.theme.*

@Composable
fun JobDetailsScreen(
    initialJob: JobDetailsPageData?,
    initialIsLoading: Boolean,
    initialError: String?
) {
    var job by remember { mutableStateOf(initialJob) }
    var isLoading by remember { mutableStateOf(initialIsLoading) }
    var error by remember { mutableStateOf(initialError) }
    var isSaved by remember { mutableStateOf(false) }
    var isApplicationModalOpen by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
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
        when {
            isLoading -> {
                JobDetailLoadingState()
            }
            error != null -> {
                JobDetailErrorState(
                    message = error.toString(),
                    onRetry = { error = null },
                    onGoBack = { /* TODO: Navigation logic */ }
                )
            }
            job != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = PTITSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                ) {
                    // Header Section
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                        ) {
                            JobDetailHeader(
                                job = job!!,
                                isSaved = isSaved,
                                onSaveJob = { isSaved = !isSaved },
                                onApplyJob = { isApplicationModalOpen = true },
                                onGoBack = { /* TODO: Navigation */ }
                            )
                        }
                    }

                    // Quick Info Cards
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                        ) {
                            JobQuickInfoSection(job = job!!)
                        }
                    }

                    // Job Description
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
                        ) {
                            JobDescriptionSection(job = job!!)
                        }
                    }

                    // Requirements
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn()
                        ) {
                            JobRequirementsSection(job = job!!)
                        }
                    }

                    // Benefits
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 5 }) + fadeIn()
                        ) {
                            JobBenefitsSection(job = job!!)
                        }
                    }

                    // Company Info
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 6 }) + fadeIn()
                        ) {
                            CompanyInfoSection(job = job!!)
                        }
                    }

                    // Related Jobs
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(initialOffsetY = { it / 7 }) + fadeIn()
                        ) {
                            RelatedJobsSection()
                        }
                    }
                }

                // Floating Action Buttons
                FloatingActionSection(
                    isSaved = isSaved,
                    onSaveJob = { isSaved = !isSaved },
                    onApplyJob = { isApplicationModalOpen = true }
                )
            }
        }

        // Application Modal
        if (isApplicationModalOpen && job != null) {
            JobApplicationDialog(
                open = true,
                onClose = { isApplicationModalOpen = false },
                job = JobInfo(id = job!!.id, title = job!!.title, companyName = job!!.companyName)
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
                        IconButton(
                            onClick = onSaveJob,
                            modifier = Modifier
                                .background(
                                    if (isSaved) PTITError.copy(alpha = 0.1f) else PTITNeutral100,
                                    CircleShape
                                )
                                .size(PTITSize.buttonMd)
                        ) {
                            Icon(
                                if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isSaved) "Unsave" else "Save",
                                tint = if (isSaved) PTITError else PTITTextSecondary
                            )
                        }
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

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickInfoItem(
                    icon = Icons.Default.AttachMoney,
                    title = "Mức lương",
                    value = job.salary,
                    color = PTITSuccess
                )
                QuickInfoItem(
                    icon = Icons.Default.LocationOn,
                    title = "Địa điểm",
                    value = job.locationName,
                    color = PTITInfo
                )
                QuickInfoItem(
                    icon = Icons.Default.AccessTime,
                    title = "Kinh nghiệm",
                    value = job.experience,
                    color = PTITWarning
                )
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, color = PTITNeutral200)

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickInfoItem(
                    icon = Icons.Default.Schedule,
                    title = "Hạn nộp",
                    value = job.deadline,
                    color = PTITError
                )
                QuickInfoItem(
                    icon = Icons.Default.Work,
                    title = "Cấp bậc",
                    value = "Senior",
                    color = PTITSecondary
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.height(PTITSpacing.xs))
        Text(title, style = MaterialTheme.typography.bodySmall, color = PTITTextSecondary)
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
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
private fun CompanyInfoSection(job: JobDetailsPageData) {
    SectionCard(title = "Thông tin công ty") {
        Column(verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)) {
            Text("Công ty: ${job.companyName}", style = MaterialTheme.typography.bodyLarge)
            Text("Lĩnh vực: ${job.categoryName}", style = MaterialTheme.typography.bodyLarge)
            Text("Địa điểm: ${job.locationName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(PTITSpacing.sm))
            OutlinedButton(onClick = { /* TODO: navigate to company profile */ }) {
                Icon(Icons.Default.Business, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Xem trang công ty")
            }
        }
    }
}

@Composable
private fun RelatedJobsSection() {
    SectionCard(title = "Công việc liên quan") {
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
                IconButton(
                    onClick = onSaveJob,
                    modifier = Modifier
                        .background(
                            if (isSaved) PTITError.copy(alpha = 0.1f) else PTITNeutral100,
                            CircleShape
                        )
                        .size(PTITSize.buttonMd)
                ) {
                    Icon(
                        if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isSaved) "Unsave" else "Save",
                        tint = if (isSaved) PTITError else PTITTextSecondary
                    )
                }

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

@Preview(showBackground = true, device = "spec:width=393dp,height=851dp,dpi=420")
@Composable
fun JobDetailsScreenPreview() {
    MaterialTheme {
        JobDetailsScreen(
            initialJob = sampleJobDetailsData,
            initialIsLoading = false,
            initialError = null
        )
    }
}

val sampleJobDetailsData = JobDetailsPageData(
    id = "1",
    title = "Senior Android Developer (Kotlin, Compose)",
    companyName = "Global Tech Solutions",
    companyId = "comp1",
    logoUrl = null,
    salary = "30 - 50 triệu VND",
    jobType = "Toàn thời gian",
    locationName = "Quận 1, TP. Hồ Chí Minh",
    categoryName = "Công nghệ thông tin",
    experience = "Trên 3 năm",
    createdAt = "25/10/2025",
    deadline = "25/11/2025",
    expiryDate = "25/11/2025",
    description = listOf(
        "Phát triển các ứng dụng Android gốc bằng Kotlin và Jetpack Compose",
        "Tham gia vào toàn bộ vòng đời phát triển sản phẩm từ ý tưởng đến triển khai",
        "Hợp tác chặt chẽ với team UI/UX để tạo ra trải nghiệm người dùng tốt nhất",
        "Đảm bảo chất lượng code thông qua code review và testing",
        "Nghiên cứu và áp dụng các công nghệ mới để cải thiện hiệu suất ứng dụng"
    ).joinToString("\n"),
    requirements = listOf(
        "Thành thạo Kotlin, Jetpack Compose, Coroutines, Flow",
        "Có kinh nghiệm với các kiến trúc MVVM, MVI",
        "Hiểu biết về Android SDK, APIs và Android Framework",
        "Kinh nghiệm làm việc với Git, CI/CD",
        "Khả năng làm việc nhóm và giao tiếp tốt"
    ).joinToString("\n"),
    benefits = listOf(
        "Mức lương cạnh tranh, thưởng dự án hấp dẫn",
        "Bảo hiểm sức khỏe cao cấp cho bản thân và gia đình",
        "Cơ hội làm việc với các công nghệ mới nhất",
        "Môi trường làm việc năng động, sáng tạo",
        "Cơ hội du lịch, team building định kỳ"
    ).joinToString("\n")
)
