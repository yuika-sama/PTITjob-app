package com.example.ptitjob.ui.screen.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ptitjob.ui.component.AuthTextField
import com.example.ptitjob.ui.component.PrimaryButton
import com.example.ptitjob.ui.component.SecondaryButton
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITError
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITInfo
import com.example.ptitjob.ui.theme.PTITNeutral50
import com.example.ptitjob.ui.theme.PTITNeutral600
import com.example.ptitjob.ui.theme.PTITNeutral700
import com.example.ptitjob.ui.theme.PTITNeutral900
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITPrimaryDark
import com.example.ptitjob.ui.theme.PTITSecondary
import com.example.ptitjob.ui.theme.PTITSize
import com.example.ptitjob.ui.theme.PTITSpacing
import com.example.ptitjob.ui.theme.PTITSuccess
import com.example.ptitjob.ui.theme.PTITWarning
import com.example.ptitjob.ui.theme.PtitjobTheme

enum class ForgotPasswordStep {
    EMAIL, TOKEN, PASSWORD
}

data class ForgotPasswordUiState(
    val email: String = "",
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.EMAIL,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val validationError: String? = null,
    val passwordResetSuccess: Boolean = false,
    val redirectCountdown: Int? = null,
    val emailFromTokenVerification: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onTokenChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRequestResetLink: () -> Unit,
    onVerifyToken: () -> Unit,
    onResetPassword: () -> Unit,
    onBackToLogin: () -> Unit,
    onResendEmail: () -> Unit,
    onBackToEmailStep: () -> Unit,
    onResetForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(PTITSpacing.xl))

            // App Logo
            ForgotPasswordHeader()

            Spacer(modifier = Modifier.height(PTITSpacing.xl))

            // Main Content Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PTITCornerRadius.lg),
                color = PTITNeutral50.copy(alpha = 0.95f),
                shadowElevation = PTITElevation.md,
                tonalElevation = PTITElevation.sm
            ) {
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        (slideInVertically(
                            animationSpec = tween(300),
                            initialOffsetY = { it / 2 }
                        ) + fadeIn(animationSpec = tween(300))) togetherWith
                                (slideOutVertically(
                                    animationSpec = tween(300),
                                    targetOffsetY = { -it / 2 }
                                ) + fadeOut(animationSpec = tween(300)))
                    },
                    label = "forgot_password_step_transition"
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PTITSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                    ) {
                        when (step) {
                            ForgotPasswordStep.EMAIL -> {
                                EmailInputStep(
                                    email = uiState.email,
                                    onEmailChange = onEmailChange,
                                    onRequestResetLink = onRequestResetLink,
                                    onBackToLogin = onBackToLogin,
                                    loading = uiState.loading,
                                    errorMessage = uiState.errorMessage ?: uiState.validationError
                                )
                            }
                            ForgotPasswordStep.TOKEN -> {
                                TokenVerificationStep(
                                    email = uiState.emailFromTokenVerification ?: uiState.email,
                                    token = uiState.token,
                                    onTokenChange = onTokenChange,
                                    onVerifyToken = onVerifyToken,
                                    onResendEmail = onResendEmail,
                                    onBackToEmailStep = onBackToEmailStep,
                                    loading = uiState.loading,
                                    errorMessage = uiState.errorMessage ?: uiState.validationError
                                )
                            }
                            ForgotPasswordStep.PASSWORD -> {
                                PasswordResetStep(
                                    email = uiState.emailFromTokenVerification ?: uiState.email,
                                    newPassword = uiState.newPassword,
                                    onNewPasswordChange = onNewPasswordChange,
                                    confirmPassword = uiState.confirmPassword,
                                    onConfirmPasswordChange = onConfirmPasswordChange,
                                    onResetPassword = onResetPassword,
                                    onBackToLogin = onBackToLogin,
                                    loading = uiState.loading,
                                    errorMessage = uiState.errorMessage ?: uiState.validationError,
                                    passwordResetSuccess = uiState.passwordResetSuccess,
                                    redirectCountdown = uiState.redirectCountdown,
                                    onGoToLoginManually = onBackToLogin,
                                    onResetForm = onResetForm
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(PTITSpacing.xl))
        }
    }
}

@Composable
private fun ForgotPasswordHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // App Icon with gradient background
        Surface(
            modifier = Modifier.size(PTITSize.cardLg),
            shape = PTITCornerRadius.md,
            color = Color.White
        ) {
            Image(
                painter = painterResource(com.example.ptitjob.R.drawable.logo),
                contentDescription = "PTIT Logo",
                modifier = Modifier.fillMaxSize().padding(PTITSpacing.sm),
                contentScale = ContentScale.Fit
            )
        }

        // Welcome text
        Text(
            text = "Đặt lại mật khẩu",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Khôi phục tài khoản PTIT Job của bạn",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.9f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmailInputStep(
    email: String,
    onEmailChange: (String) -> Unit,
    onRequestResetLink: () -> Unit,
    onBackToLogin: () -> Unit,
    loading: Boolean,
    errorMessage: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {

        // Step Title and Description
        Text(
            text = "Nhập email của bạn",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PTITNeutral900
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Chúng tôi sẽ gửi link đặt lại mật khẩu đến email của bạn",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PTITNeutral700
            ),
            textAlign = TextAlign.Center
        )

        // Error Message
        if (errorMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PTITError.copy(alpha = 0.1f),
                shape = PTITCornerRadius.sm
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PTITSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = PTITError,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITError
                        )
                    )
                }
            }
        }

        // Email Input
        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "Nhập email của bạn",
            leadingIcon = Icons.Default.Email,
            isError = errorMessage != null,
        )

        // Submit Button
        PrimaryButton(
            onClick = onRequestResetLink,
            text = "Gửi link đặt lại mật khẩu",
            enabled = !loading && email.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        // Back to Login
        TextButton(
            onClick = onBackToLogin,
            enabled = !loading
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(PTITSize.iconSm),
                tint = PTITPrimary
            )
            Spacer(modifier = Modifier.width(PTITSpacing.sm))
            Text(
                text = "Về trang trước",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = PTITPrimary
                )
            )
        }
    }
}

@Composable
private fun TokenVerificationStep(
    email: String,
    token: String,
    onTokenChange: (String) -> Unit,
    onVerifyToken: () -> Unit,
    onResendEmail: () -> Unit,
    onBackToEmailStep: () -> Unit,
    loading: Boolean,
    errorMessage: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {

        // Step Title and Description
        Text(
            text = "Kiểm tra email",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PTITNeutral900
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Chúng tôi đã gửi mã xác thực đến:",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PTITNeutral700
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = PTITPrimary
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Vui lòng kiểm tra email và nhập mã xác thực bên dưới. Nếu không thấy email, hãy kiểm tra thư mục spam.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = PTITNeutral600
            ),
            textAlign = TextAlign.Center
        )

        // Error Message
        if (errorMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PTITError.copy(alpha = 0.1f),
                shape = PTITCornerRadius.sm
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PTITSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = PTITError,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITError
                        )
                    )
                }
            }
        }

        // Token Input
        AuthTextField(
            value = token,
            onValueChange = onTokenChange,
            label = "Mã xác thực",
            placeholder = "Nhập mã xác thực từ email",
            leadingIcon = Icons.Default.Pin,
            isError = errorMessage != null,
        )

        // Verify Button
        PrimaryButton(
            onClick = onVerifyToken,
            text = "Xác thực mã",
            enabled = !loading && token.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        // Resend Email Button
        SecondaryButton(
            onClick = onResendEmail,
            text = "Gửi lại email",
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        )

        // Back Button
        TextButton(
            onClick = onBackToEmailStep,
            enabled = !loading
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(PTITSize.iconSm),
                tint = PTITPrimary
            )
            Spacer(modifier = Modifier.width(PTITSpacing.sm))
            Text(
                text = "Về trang trước",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = PTITPrimary
                )
            )
        }
    }
}

@Composable
private fun PasswordResetStep(
    email: String,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onResetPassword: () -> Unit,
    onBackToLogin: () -> Unit,
    loading: Boolean,
    errorMessage: String?,
    passwordResetSuccess: Boolean,
    redirectCountdown: Int?,
    onGoToLoginManually: () -> Unit,
    onResetForm: () -> Unit
) {
    if (passwordResetSuccess) {
        // Success State
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Success Icon
            Surface(
                modifier = Modifier.size(PTITSize.cardLg),
                shape = PTITCornerRadius.md,
                color = PTITPrimaryDark
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(PTITSize.iconXl),
                        tint = Color.White
                    )
                }
            }

            Text(
                text = "Thành công!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITPrimary
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Mật khẩu của bạn đã được đặt lại thành công!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITNeutral700
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Email xác nhận đã được gửi đến $email.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITNeutral600
                ),
                textAlign = TextAlign.Center
            )

            redirectCountdown?.let {
                Text(
                    text = "Chuyển hướng trong $it giây...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITNeutral600
                    ),
                    textAlign = TextAlign.Center
                )
            } ?: run {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(PTITSize.avatarSm),
                        color = PTITPrimary,
                        strokeWidth = 3.dp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                PrimaryButton(
                    onClick = onGoToLoginManually,
                    text = "Đăng nhập",
                    modifier = Modifier.weight(1f)
                )

                SecondaryButton(
                    onClick = onResetForm,
                    text = "Đặt lại khác",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    } else {
        // Password Reset Form
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            // Step Title and Description
            Text(
                text = "Đặt mật khẩu mới",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITNeutral900
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Nhập mật khẩu mới cho tài khoản $email",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITNeutral700
                ),
                textAlign = TextAlign.Center
            )

            // Error Message
            if (errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PTITError.copy(alpha = 0.1f),
                    shape = PTITCornerRadius.sm
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PTITSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = PTITError,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITError
                            )
                        )
                    }
                }
            }

            // New Password Input
            AuthTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                label = "Mật khẩu mới",
                placeholder = "Nhập mật khẩu mới",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isError = errorMessage != null && errorMessage.contains("mật khẩu"),
            )

            // Confirm Password Input
            AuthTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Xác nhận mật khẩu",
                placeholder = "Nhập lại mật khẩu mới",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
            )

            // Reset Password Button
            PrimaryButton(
                onClick = onResetPassword,
                text = "Đặt lại mật khẩu",
                enabled = !loading && newPassword.isNotBlank() && confirmPassword.isNotBlank() && newPassword == confirmPassword,
                modifier = Modifier.fillMaxWidth()
            )

            // Back to Login
            TextButton(
                onClick = onBackToLogin,
                enabled = !loading,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(PTITSize.iconSm),
                    tint = PTITPrimary
                )
                Spacer(modifier = Modifier.width(PTITSpacing.sm))
                Text(
                    text = "Về trang trước",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = PTITPrimary
                    )
                )
            }
        }
    }
}

// Preview Composables
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewForgotPasswordScreenEmailStep() {
    PtitjobTheme {
        ForgotPasswordScreen(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.EMAIL,
                email = "test@ptit.edu.vn"
            ),
            onEmailChange = {},
            onTokenChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onRequestResetLink = {},
            onVerifyToken = {},
            onResetPassword = {},
            onBackToLogin = {},
            onResendEmail = {},
            onBackToEmailStep = {},
            onResetForm = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewForgotPasswordScreenTokenStep() {
    PtitjobTheme {
        ForgotPasswordScreen(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.TOKEN,
                email = "user@ptit.edu.vn",
                token = "123456",
                emailFromTokenVerification = "user@ptit.edu.vn"
            ),
            onEmailChange = {},
            onTokenChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onRequestResetLink = {},
            onVerifyToken = {},
            onResetPassword = {},
            onBackToLogin = {},
            onResendEmail = {},
            onBackToEmailStep = {},
            onResetForm = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewForgotPasswordScreenPasswordStep() {
    PtitjobTheme {
        ForgotPasswordScreen(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.PASSWORD,
                email = "user@ptit.edu.vn",
                emailFromTokenVerification = "user@ptit.edu.vn",
                newPassword = "NewPassword123!",
                confirmPassword = "NewPassword123!"
            ),
            onEmailChange = {},
            onTokenChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onRequestResetLink = {},
            onVerifyToken = {},
            onResetPassword = {},
            onBackToLogin = {},
            onResendEmail = {},
            onBackToEmailStep = {},
            onResetForm = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewForgotPasswordScreenSuccess() {
    PtitjobTheme {
        ForgotPasswordScreen(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.PASSWORD,
                passwordResetSuccess = true,
                redirectCountdown = 3,
                email = "user@ptit.edu.vn",
                emailFromTokenVerification = "user@ptit.edu.vn"
            ),
            onEmailChange = {},
            onTokenChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onRequestResetLink = {},
            onVerifyToken = {},
            onResetPassword = {},
            onBackToLogin = {},
            onResendEmail = {},
            onBackToEmailStep = {},
            onResetForm = {}
        )
    }
}
