package com.example.ptitjob.ui.screen.candidate.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ptitjob.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- 1. DATA MODELS ---
data class UserProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val studentId: String?,
    val major: String?,
    val graduationYear: String?,
    val role: String,
    val isActive: Boolean,
    val createdAt: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val skills: List<String> = emptyList(),
    val achievements: List<String> = emptyList()
)

data class EditFormData(
    val fullName: String = "",
    val phoneNumber: String = "",
    val studentId: String = "",
    val major: String = "",
    val graduationYear: String = "",
    val bio: String = ""
)

data class PasswordFormData(
    val current: String = "",
    val new: String = "",
    val confirm: String = ""
)


// --- 2. MAIN SCREEN COMPONENT ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserProfile,
    background: Color = Color.White,
    onBack: () -> Unit = {},
    onUpdateProfile: (EditFormData, (Boolean, String?) -> Unit) -> Unit = { _, cb -> cb(true, null) },
    onChangePassword: (PasswordFormData, (Boolean, String?) -> Unit) -> Unit = { _, cb -> cb(true, null) },
    onSettingsNavigate: () -> Unit = {}
) {
    var editDialogOpen by remember { mutableStateOf(false) }
    var passwordDialogOpen by remember { mutableStateOf(false) }
    var settingsDialogOpen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showAnimations by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(300)
        showAnimations = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hồ sơ cá nhân",
                        color = PTITTextPrimary, // Changed for light background
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = PTITTextPrimary // Changed for light background
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { settingsDialogOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Cài đặt",
                            tint = PTITTextPrimary // Changed for light background
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Transparent to blend with screen background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PTITBackgroundLight) // Solid light background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(PTITSpacing.md),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                // Profile Header with animation
                item {
                    AnimatedVisibility(
                        visible = showAnimations,
                        enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn()
                    ) {
                        PTITProfileHeader(
                            user = user,
                            onEditClick = { editDialogOpen = true }
                        )
                    }
                }

                // Academic Information Card
                item {
                    AnimatedVisibility(
                        visible = showAnimations,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(600, delayMillis = 400)
                        ) + fadeIn()
                    ) {
                        PTITAcademicInfoCard(user = user)
                    }
                }

                // Skills & Achievements Card
                item {
                    AnimatedVisibility(
                        visible = showAnimations,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(600, delayMillis = 600)
                        ) + fadeIn()
                    ) {
                        PTITSkillsAchievementsCard(user = user)
                    }
                }

                // Account Status Card
                item {
                    AnimatedVisibility(
                        visible = showAnimations,
                        enter = scaleIn(
                            animationSpec = tween(500, delayMillis = 800)
                        ) + fadeIn()
                    ) {
                        PTITAccountStatusCard(user = user)
                    }
                }

                // Quick Actions Card
                item {
                    AnimatedVisibility(
                        visible = showAnimations,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(600, delayMillis = 1000)
                        ) + fadeIn()
                    ) {
                        PTITQuickActionsCard(
                            onEditProfile = { editDialogOpen = true },
                            onChangePassword = { passwordDialogOpen = true },
                            onSettings = { settingsDialogOpen = true },
                            isLoading = isLoading
                        )
                    }
                }

                // Footer spacing
                item {
                    Spacer(modifier = Modifier.height(PTITSpacing.xl))
                }
            }
        }
    }

    // Dialogs
    if (editDialogOpen) {
        PTITEditProfileDialog(
            user = user,
            onDismiss = { editDialogOpen = false },
            onSave = { editData ->
                // Delegate to provided callback (ViewModel route will pass real implementation)
                isLoading = true
                onUpdateProfile(editData) { success, message ->
                    scope.launch {
                        if (success) {
                            snackbarHostState.showSnackbar(message = "✅ Cập nhật hồ sơ thành công!", actionLabel = "OK")
                            editDialogOpen = false
                        } else {
                            snackbarHostState.showSnackbar(message = message ?: "Cập nhật thất bại", actionLabel = "OK")
                        }
                        isLoading = false
                    }
                }
            }
        )
    }

    if (passwordDialogOpen) {
        PTITChangePasswordDialog(
            onDismiss = { passwordDialogOpen = false },
            onSave = { passwordData ->
                isLoading = true
                onChangePassword(passwordData) { success, message ->
                    scope.launch {
                        if (success) {
                            snackbarHostState.showSnackbar(message = "🔒 Đổi mật khẩu thành công!", actionLabel = "OK")
                            passwordDialogOpen = false
                        } else {
                            snackbarHostState.showSnackbar(message = message ?: "Đổi mật khẩu thất bại", actionLabel = "OK")
                        }
                        isLoading = false
                    }
                }
            }
        )
    }

    if (settingsDialogOpen) {
        PTITSettingsDialog(
            onDismiss = { settingsDialogOpen = false }
        )
    }
}


// --- 3. CHILD COMPOSABLES ---

@Composable
private fun PTITProfileHeader(
    user: UserProfile,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.md)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Section
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Default Grey Avatar
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar mặc định",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // Edit Button
                FilledIconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = PTITPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Chỉnh sửa hồ sơ",
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                }
            }

            Spacer(modifier = Modifier.height(PTITSpacing.md))

            // User Info
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(PTITSpacing.xs))

            // Role and Status Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role Chip
                Surface(
                    shape = PTITCornerRadius.md,
                    color = PTITPrimary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = PTITPrimary,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = user.role.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = PTITPrimary
                            )
                        )
                    }
                }

                // Status Chip
                if (user.isActive) {
                    Surface(
                        shape = PTITCornerRadius.md,
                        color = PTITSuccess.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = PTITSuccess,
                                modifier = Modifier.size(PTITSize.iconSm)
                            )
                            Text(
                                text = "ĐANG HOẠT ĐỘNG",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PTITSuccess
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(PTITSpacing.lg))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = PTITSpacing.lg),
                color = PTITPrimary.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(PTITSpacing.lg))

            // Contact Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PTITSpacing.md),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                // Email Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Email",
                        tint = PTITTextSecondary,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                    Spacer(modifier = Modifier.width(PTITSpacing.md))
                    Column {
                        Text("Email", style = MaterialTheme.typography.labelMedium.copy(color = PTITTextSecondary))
                        Text(user.email, style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextPrimary, fontWeight = FontWeight.SemiBold))
                    }
                }

                // Phone Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = "Số điện thoại",
                        tint = PTITTextSecondary,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                    Spacer(modifier = Modifier.width(PTITSpacing.md))
                    Column {
                        Text("Số điện thoại", style = MaterialTheme.typography.labelMedium.copy(color = PTITTextSecondary))
                        Text(
                            text = user.phoneNumber ?: "Chưa cập nhật",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (user.phoneNumber != null) PTITTextPrimary else PTITWarning
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PTITAcademicInfoCard(user: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.md)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg)
        ) {
            PTITSectionHeader(
                title = "Thông tin học tập",
                icon = Icons.Default.School,
                iconColor = PTITSecondary
            )

            Spacer(modifier = Modifier.height(PTITSpacing.md))

            PTITInfoRow(
                icon = Icons.Default.Numbers,
                label = "Mã sinh viên",
                value = user.studentId ?: "Chưa cập nhật",
                valueColor = if (user.studentId != null) PTITTextPrimary else PTITWarning
            )

            PTITInfoRow(
                icon = Icons.Default.Category,
                label = "Ngành học",
                value = user.major ?: "Chưa cập nhật",
                valueColor = if (user.major != null) PTITTextPrimary else PTITWarning
            )

            PTITInfoRow(
                icon = Icons.Default.CalendarMonth,
                label = "Năm tốt nghiệp",
                value = user.graduationYear ?: "Chưa cập nhật",
                valueColor = if (user.graduationYear != null) PTITTextPrimary else PTITWarning
            )
        }
    }
}

@Composable
private fun PTITSkillsAchievementsCard(user: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.md)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg)
        ) {
            PTITSectionHeader(
                title = "Kỹ năng & Thành tích",
                icon = Icons.Default.Star,
                iconColor = PTITSecondary
            )

            Spacer(modifier = Modifier.height(PTITSpacing.md))

            // Skills Section
            if (user.skills.isNotEmpty()) {
                Text(
                    text = "Kỹ năng",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.sm))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                    contentPadding = PaddingValues(bottom = PTITSpacing.md)
                ) {
                    items(user.skills.size) { index ->
                        Surface(
                            shape = PTITCornerRadius.md,
                            color = PTITPrimary.copy(0.1f)
                        ) {
                            Text(
                                text = user.skills[index],
                                modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = PTITPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            } else {
                PTITEmptyState(
                    icon = Icons.Default.Psychology,
                    message = "Chưa có kỹ năng nào được thêm"
                )
            }

            // Achievements Section
            if (user.achievements.isNotEmpty()) {
                Text(
                    text = "Thành tích",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.sm))

                user.achievements.forEach { achievement ->
                    Row(
                        modifier = Modifier.padding(vertical = PTITSpacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = PTITBackgroundLight,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = achievement,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITTextSecondary
                            )
                        )
                    }
                }
            } else {
                PTITEmptyState(
                    icon = Icons.Default.EmojiEvents,
                    message = "Chưa có thành tích nào được thêm"
                )
            }
        }
    }
}

@Composable
private fun PTITAccountStatusCard(user: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.md)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg)
        ) {
            PTITSectionHeader(
                title = "Trạng thái tài khoản",
                icon = Icons.Default.Security,
                iconColor = PTITInfo
            )

            Spacer(modifier = Modifier.height(PTITSpacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Trạng thái hoạt động",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (user.isActive) PTITSuccess else PTITError)
                        )
                        Text(
                            text = if (user.isActive) "Đang hoạt động" else "Bị khóa",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (user.isActive) PTITSuccess else PTITError
                            )
                        )
                    }
                }

                Icon(
                    imageVector = if (user.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (user.isActive) PTITSuccess else PTITError,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = PTITSpacing.md),
                color = PTITPrimary
            )

            PTITInfoRow(
                icon = Icons.Default.CalendarToday,
                label = "Ngày tham gia",
                value = user.createdAt,
                valueColor = PTITTextPrimary
            )
        }
    }
}

@Composable
private fun PTITQuickActionsCard(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onSettings: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.md)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg)
        ) {
            PTITSectionHeader(
                title = "Thao tác nhanh",
                icon = Icons.Default.Speed,
                iconColor = PTITBackgroundLight
            )

            Spacer(modifier = Modifier.height(PTITSpacing.md))

            // Edit Profile Button
            Button(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITPrimary
                ),
                shape = PTITCornerRadius.md,
                contentPadding = PaddingValues(PTITSpacing.md)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(PTITSize.iconSm),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                }
                Spacer(modifier = Modifier.width(PTITSpacing.sm))
                Text(
                    text = if (isLoading) "Đang xử lý..." else "Chỉnh sửa hồ sơ",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(PTITSpacing.sm))

            // Change Password Button
            OutlinedButton(
                onClick = onChangePassword,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PTITSecondary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        listOf(PTITSecondary, PTITSecondary.copy(0.7f))
                    )
                ),
                shape = PTITCornerRadius.md,
                contentPadding = PaddingValues(PTITSpacing.md)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Spacer(modifier = Modifier.width(PTITSpacing.sm))
                Text(
                    text = "Đổi mật khẩu",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(PTITSpacing.sm))

            // Settings Button
            TextButton(
                onClick = onSettings,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = PTITInfo
                ),
                shape = PTITCornerRadius.md,
                contentPadding = PaddingValues(PTITSpacing.md)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                Spacer(modifier = Modifier.width(PTITSpacing.sm))
                Text(
                    text = "Cài đặt ứng dụng",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

// --- UTILITY COMPOSABLES ---

@Composable
private fun PTITSectionHeader(
    title: String,
    icon: ImageVector,
    iconColor: Color = PTITPrimary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
    ) {
        Surface(
            shape = CircleShape,
            color = iconColor.copy(0.1f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextPrimary
            )
        )
    }
}

@Composable
private fun PTITInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = PTITTextPrimary,
    maxLines: Int = 1
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PTITSpacing.xs),
        shape = PTITCornerRadius.md,
        color = PTITBackgroundLight,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PTITTextSecondary,
                modifier = Modifier.size(PTITSize.iconSm)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PTITTextSecondary
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = valueColor
                    ),
                    maxLines = maxLines
                )
            }
        }
    }
}

@Composable
private fun PTITEmptyState(
    icon: ImageVector,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PTITSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PTITTextSecondary.copy(0.5f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(PTITSpacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PTITTextSecondary,
                textAlign = TextAlign.Center
            )
        )
    }
}

// --- DIALOG COMPONENTS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PTITEditProfileDialog(
    user: UserProfile,
    onDismiss: () -> Unit,
    onSave: (EditFormData) -> Unit
) {
    var formData by remember {
        mutableStateOf(
            EditFormData(
                fullName = user.fullName,
                phoneNumber = user.phoneNumber ?: "",
                studentId = user.studentId ?: "",
                major = user.major ?: "",
                graduationYear = user.graduationYear ?: "",
                bio = user.bio ?: ""
            )
        )
    }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = PTITCornerRadius.lg,
            color = Color.White,
            tonalElevation = PTITElevation.lg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md)
        ) {
            Column(
                modifier = Modifier
                    .padding(PTITSpacing.lg)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chỉnh sửa hồ sơ",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = PTITTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PTITSpacing.lg))

                // Form Fields
                OutlinedTextField(
                    value = formData.fullName,
                    onValueChange = { formData = formData.copy(fullName = it) },
                    label = { Text("Họ và tên") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.md))

                OutlinedTextField(
                    value = formData.phoneNumber,
                    onValueChange = { formData = formData.copy(phoneNumber = it) },
                    label = { Text("Số điện thoại") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.md))

                OutlinedTextField(
                    value = formData.studentId,
                    onValueChange = { formData = formData.copy(studentId = it) },
                    label = { Text("Mã sinh viên") },
                    leadingIcon = {
                        Icon(Icons.Default.Numbers, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.md))

                OutlinedTextField(
                    value = formData.major,
                    onValueChange = { formData = formData.copy(major = it) },
                    label = { Text("Ngành học") },
                    leadingIcon = {
                        Icon(Icons.Default.School, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.md))

                OutlinedTextField(
                    value = formData.graduationYear,
                    onValueChange = { formData = formData.copy(graduationYear = it) },
                    label = { Text("Năm tốt nghiệp") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.md))

                OutlinedTextField(
                    value = formData.bio,
                    onValueChange = { formData = formData.copy(bio = it) },
                    label = { Text("Giới thiệu bản thân") },
                    leadingIcon = {
                        Icon(Icons.Default.Info, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.xl))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PTITTextSecondary
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        Text("Hủy")
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            onSave(formData)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && formData.fullName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITPrimary
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Lưu")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PTITChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: (PasswordFormData) -> Unit
) {
    var formData by remember { mutableStateOf(PasswordFormData()) }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = PTITCornerRadius.lg,
            color = Color.White,
            tonalElevation = PTITElevation.lg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md)
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Đổi mật khẩu",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = PTITTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PTITSpacing.lg))

                // Current Password
                OutlinedTextField(
                    value = formData.current,
                    onValueChange = { formData = formData.copy(current = it) },
                    label = { Text("Mật khẩu hiện tại") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                            Icon(
                                imageVector = if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showCurrentPassword) "Ẩn mật khẩu" else "Hiện mật khẩu"
                            )
                        }
                    },
                    visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.md))

                // New Password
                OutlinedTextField(
                    value = formData.new,
                    onValueChange = { formData = formData.copy(new = it) },
                    label = { Text("Mật khẩu mới") },
                    leadingIcon = {
                        Icon(Icons.Default.LockOpen, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                imageVector = if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showNewPassword) "Ẩn mật khẩu" else "Hiện mật khẩu"
                            )
                        }
                    },
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    )
                )

                Spacer(modifier = Modifier.height(PTITSpacing.md))

                // Confirm Password
                OutlinedTextField(
                    value = formData.confirm,
                    onValueChange = { formData = formData.copy(confirm = it) },
                    label = { Text("Xác nhận mật khẩu mới") },
                    leadingIcon = {
                        Icon(Icons.Default.LockOpen, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showConfirmPassword) "Ẩn mật khẩu" else "Hiện mật khẩu"
                            )
                        }
                    },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = PTITCornerRadius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PTITPrimary,
                        focusedLabelColor = PTITPrimary
                    ),
                    isError = formData.new.isNotBlank() && formData.confirm.isNotBlank() && formData.new != formData.confirm
                )

                if (formData.new.isNotBlank() && formData.confirm.isNotBlank() && formData.new != formData.confirm) {
                    Text(
                        text = "Mật khẩu xác nhận không khớp",
                        color = PTITError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = PTITSpacing.md, top = PTITSpacing.xs)
                    )
                }

                Spacer(modifier = Modifier.height(PTITSpacing.xl))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PTITTextSecondary
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        Text("Hủy")
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            onSave(formData)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading &&
                                formData.current.isNotBlank() &&
                                formData.new.isNotBlank() &&
                                formData.confirm.isNotBlank() &&
                                formData.new == formData.confirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITPrimary
                        ),
                        shape = PTITCornerRadius.md
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Đổi mật khẩu")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PTITSettingsDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = PTITCornerRadius.lg,
            color = Color.White,
            tonalElevation = PTITElevation.lg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.md)
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.lg)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cài đặt ứng dụng",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = PTITTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PTITSpacing.lg))

                // Settings Items
                PTITSettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Thông báo",
                    subtitle = "Cài đặt thông báo việc làm",
                    onClick = { /* TODO */ }
                )

                PTITSettingsItem(
                    icon = Icons.Default.Language,
                    title = "Ngôn ngữ",
                    subtitle = "Tiếng Việt",
                    onClick = { /* TODO */ }
                )

                PTITSettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "Giao diện",
                    subtitle = "Chế độ sáng/tối",
                    onClick = { /* TODO */ }
                )

                PTITSettingsItem(
                    icon = Icons.Default.Security,
                    title = "Bảo mật",
                    subtitle = "Cài đặt bảo mật tài khoản",
                    onClick = { /* TODO */ }
                )

                PTITSettingsItem(
                    icon = Icons.Default.Help,
                    title = "Trợ giúp",
                    subtitle = "Hướng dẫn sử dụng ứng dụng",
                    onClick = { /* TODO */ }
                )

                PTITSettingsItem(
                    icon = Icons.Default.Info,
                    title = "Về ứng dụng",
                    subtitle = "Phiên bản 1.0.0",
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
private fun PTITSettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = PTITCornerRadius.md,
        color = PTITBackgroundLight
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PTITPrimary,
                modifier = Modifier.size(PTITSize.iconMd)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITTextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PTITTextSecondary
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = PTITTextSecondary,
                modifier = Modifier.size(PTITSize.iconSm)
            )
        }
    }

    Spacer(modifier = Modifier.height(PTITSpacing.xs))
}


// --- 4. PREVIEW ---
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ProfileScreenPreview() {
    val sampleUser = UserProfile(
        id = "user123",
        fullName = "Nguyễn Văn An",
        email = "nguyen.an@ptit.edu.vn",
        phoneNumber = "0987654321",
        studentId = "B20DCCN001",
        major = "Công nghệ thông tin",
        graduationYear = "2024",
        role = "Sinh viên",
        isActive = true,
        createdAt = "01/01/2024",
        avatarUrl = null,
        bio = "Sinh viên năm cuối ngành CNTT tại PTIT, đam mê lập trình mobile và web development.",
        skills = listOf("Kotlin", "Java", "Android", "React", "Node.js"),
        achievements = listOf(
            "Giải nhất cuộc thi lập trình PTIT 2023",
            "Chứng chỉ Google Android Developer Associate",
            "Hoàn thành khóa học AWS Cloud Practitioner"
        )
    )

    PtitjobTheme {
        ProfileScreen(
            user = sampleUser,
            onBack = {}
        )
    }
}
