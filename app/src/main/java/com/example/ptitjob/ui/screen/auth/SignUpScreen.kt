package com.example.ptitjob.ui.screen.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.component.*
import com.example.ptitjob.ui.theme.*

enum class PasswordStrengthLevel {
    WEAK, MEDIUM, STRONG, VERY_STRONG
}

data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phoneNumber: String = "",
    val acceptTerms: Boolean = false,
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val loading: Boolean = false,
    val registrationSuccess: Boolean = false,
    val redirectCountdown: Int? = null,
    val validationErrors: Map<String, String> = emptyMap()
)

data class PasswordStrength(
    val score: Int = 0, // 0-100
    val message: String = "Nhập mật khẩu",
    val level: PasswordStrengthLevel = PasswordStrengthLevel.WEAK
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onAcceptTermsChange: (Boolean) -> Unit,
    onToggleShowPassword: () -> Unit,
    onToggleShowConfirmPassword: () -> Unit,
    onSubmit: () -> Unit,
    onLoginClick: () -> Unit,
    onViewTerms: () -> Unit,
    onViewPrivacy: () -> Unit,
    calculatePasswordStrength: (String) -> PasswordStrength,
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
        AnimatedContent(
            targetState = uiState.registrationSuccess,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) with fadeOut(animationSpec = tween(600))
            },
            label = "registration_success_transition"
        ) { success ->
            if (success) {
                RegistrationSuccessScreen(
                    redirectCountdown = uiState.redirectCountdown,
                    onNavigateToDashboard = { /* Logic navigate to dashboard */ },
                    onNavigateToLogin = onLoginClick
                )
            } else {
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
                    
                    // App Logo and Welcome Text
                    SignUpHeader()
                    
                    Spacer(modifier = Modifier.height(PTITSpacing.xl))
                    
                    // Sign Up Form Card
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
                        ) {
                            // Form Title
                            Text(
                                text = "Tạo tài khoản mới",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PTITNeutral900
                                ),
                                textAlign = TextAlign.Center
                            )
                            
                            // Sign Up Form
                            SignUpForm(
                                uiState = uiState,
                                onFullNameChange = onFullNameChange,
                                onEmailChange = onEmailChange,
                                onPasswordChange = onPasswordChange,
                                onConfirmPasswordChange = onConfirmPasswordChange,
                                onPhoneNumberChange = onPhoneNumberChange,
                                onAcceptTermsChange = onAcceptTermsChange,
                                onToggleShowPassword = onToggleShowPassword,
                                onToggleShowConfirmPassword = onToggleShowConfirmPassword,
                                onSubmit = onSubmit,
                                onViewTerms = onViewTerms,
                                onViewPrivacy = onViewPrivacy,
                                calculatePasswordStrength = calculatePasswordStrength
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(PTITSpacing.xl))
                    
                    // Login Section
                    SignUpFooter(onLoginClick)
                    
                    Spacer(modifier = Modifier.height(PTITSpacing.xl))
                }
            }
        }
    }
}

@Composable
private fun SignUpHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // App Icon with gradient background
        Surface(
            modifier = Modifier.size(PTITSize.cardXl),
            shape = PTITCornerRadius.md,
            color = PTITSecondary
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Sign Up Icon",
                    modifier = Modifier.size(PTITSize.iconLg),
                    tint = Color.White
                )
            }
        }
        
        // Welcome text
        Text(
            text = "Tạo tài khoản mới",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Điền thông tin để tạo tài khoản PTIT Job của bạn",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.9f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SignUpForm(
    uiState: SignUpUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onAcceptTermsChange: (Boolean) -> Unit,
    onToggleShowPassword: () -> Unit,
    onToggleShowConfirmPassword: () -> Unit,
    onSubmit: () -> Unit,
    onViewTerms: () -> Unit,
    onViewPrivacy: () -> Unit,
    calculatePasswordStrength: (String) -> PasswordStrength
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        // Full Name Field
        AuthTextField(
            value = uiState.fullName,
            onValueChange = onFullNameChange,
            label = "Họ và tên",
            placeholder = "Nhập họ và tên của bạn",
            leadingIcon = Icons.Default.Person,
//            keyboardType = KeyboardType.Text,
            isError = uiState.validationErrors.containsKey("fullName"),
//            errorText = uiState.validationErrors["fullName"],
//            enabled = !uiState.loading
        )

        // Email Field
        AuthTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "Nhập email của bạn",
            leadingIcon = Icons.Default.Email,
//            keyboardType = KeyboardType.Email,
            isError = uiState.validationErrors.containsKey("email"),
//            errorText = uiState.validationErrors["email"],
//            enabled = !uiState.loading
        )

        // Phone Number Field
        AuthTextField(
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = "Số điện thoại",
            placeholder = "Nhập số điện thoại của bạn",
            leadingIcon = Icons.Default.Phone,
//            keyboardType = KeyboardType.Phone,
            isError = uiState.validationErrors.containsKey("phoneNumber"),
//            errorText = uiState.validationErrors["phoneNumber"],
//            enabled = !uiState.loading
        )

        // Password Field with Strength Indicator
        Column(
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            AuthTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Mật khẩu",
                placeholder = "Nhập mật khẩu của bạn",
                leadingIcon = Icons.Default.Lock,
//                keyboardType = KeyboardType.Password,
                isPassword = true,
//                showPassword = uiState.showPassword,
//                onTogglePasswordVisibility = onToggleShowPassword,
                isError = uiState.validationErrors.containsKey("password"),
//                errorText = uiState.validationErrors["password"],
//                enabled = !uiState.loading
            )
            
            // Password Strength Indicator
            if (uiState.password.isNotEmpty()) {
                PasswordStrengthIndicator(
                    passwordStrength = calculatePasswordStrength(uiState.password)
                )
            }
        }

        // Confirm Password Field
        AuthTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Xác nhận mật khẩu",
            placeholder = "Nhập lại mật khẩu của bạn",
            leadingIcon = Icons.Default.Lock,
//            keyboardType = KeyboardType.Password,
            isPassword = true,
//            showPassword = uiState.showConfirmPassword,
//            onTogglePasswordVisibility = onToggleShowConfirmPassword,
            isError = uiState.validationErrors.containsKey("confirmPassword"),
//            errorText = uiState.validationErrors["confirmPassword"],
//            enabled = !uiState.loading
        )

        // Terms and Conditions
        TermsAcceptanceSection(
            acceptTerms = uiState.acceptTerms,
            onAcceptTermsChange = onAcceptTermsChange,
            onViewTerms = onViewTerms,
            onViewPrivacy = onViewPrivacy,
            errorText = uiState.validationErrors["acceptTerms"],
            enabled = !uiState.loading
        )

        // General Error Alert
        AnimatedVisibility(
            visible = uiState.validationErrors.containsKey("general"),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            if (uiState.validationErrors.containsKey("general")) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PTITError.copy(alpha = 0.1f),
                    shape = (PTITCornerRadius.sm)
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
                            text = uiState.validationErrors["general"]!!,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITError
                            )
                        )
                    }
                }
            }
        }

        // Submit Button
        PrimaryButton(
            onClick = onSubmit,
            text = "Tạo tài khoản",
//            loading = uiState.loading,
            enabled = !uiState.loading && isFormValid(uiState),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PasswordStrengthIndicator(
    passwordStrength: PasswordStrength
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
    ) {
        LinearProgressIndicator(
            progress = passwordStrength.score / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(PTITSpacing.sm)
                .clip((PTITCornerRadius.sm)),
            color = getPasswordStrengthColor(passwordStrength.level),
            trackColor = PTITNeutral300
        )
        
        Text(
            text = passwordStrength.message,
            style = MaterialTheme.typography.bodySmall.copy(
                color = getPasswordStrengthColor(passwordStrength.level),
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun TermsAcceptanceSection(
    acceptTerms: Boolean,
    onAcceptTermsChange: (Boolean) -> Unit,
    onViewTerms: () -> Unit,
    onViewPrivacy: () -> Unit,
    errorText: String?,
    enabled: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
    ) {
        val annotatedText = buildAnnotatedString {
            append("Tôi đồng ý với các ")
            pushStringAnnotation(tag = "TERMS", annotation = "terms")
            withStyle(
                style = SpanStyle(
                    color = PTITPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append("điều khoản dịch vụ")
            }
            pop()
            append(" và ")
            pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
            withStyle(
                style = SpanStyle(
                    color = PTITPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append("chính sách bảo mật")
            }
            pop()
            append(" *")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = onAcceptTermsChange,
                enabled = enabled,
                colors = CheckboxDefaults.colors(
                    checkedColor = PTITPrimary,
                    uncheckedColor = PTITNeutral400
                )
            )
            
            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITNeutral700
                ),
                onClick = { offset ->
                    if (!enabled) return@ClickableText

                    val termClicked = annotatedText.getStringAnnotations(
                        tag = "TERMS", start = offset, end = offset
                    ).any()

                    val privacyClicked = annotatedText.getStringAnnotations(
                        tag = "PRIVACY", start = offset, end = offset
                    ).any()

                    when {
                        termClicked -> onViewTerms()
                        privacyClicked -> onViewPrivacy()
                        else -> onAcceptTermsChange(!acceptTerms)
                    }
                }
            )
        }
        
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PTITError
                ),
                modifier = Modifier.padding(start = PTITSize.buttonLg)
            )
        }
    }
}

@Composable
private fun SignUpFooter(onLoginClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        DividerWithText(text = "hoặc")
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bạn đã có tài khoản? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
            TextButton(
                onClick = onLoginClick,
                contentPadding = PaddingValues(horizontal = PTITSpacing.sm)
            ) {
                Text(
                    text = "Đăng nhập ngay",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun RegistrationSuccessScreen(
    redirectCountdown: Int?,
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTITSpacing.lg)
                .clip((PTITCornerRadius.lg)),
            color = PTITNeutral50.copy(alpha = 0.95f),
            shadowElevation = PTITElevation.md,
            tonalElevation = PTITElevation.sm
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PTITSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                // Success Icon
                Surface(
                    modifier = Modifier.size(PTITSize.cardXl * 1.5f),
                    shape = (PTITCornerRadius.md),
                    color = PTITSuccess
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
                    text = "Đăng ký thành công!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITSuccess
                    ),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Chào mừng bạn đến với PTIT Job! Tài khoản của bạn đã được tạo thành công.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PTITNeutral700
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(PTITSize.iconMd),
                        color = PTITPrimary,
                        strokeWidth = 3.dp
                    )
                }

                Spacer(modifier = Modifier.height(PTITSpacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                ) {
                    PrimaryButton(
                        onClick = onNavigateToDashboard,
                        text = "Vào Dashboard",
                        modifier = Modifier.weight(1f)
                    )
                    
                    SecondaryButton(
                        onClick = onNavigateToLogin,
                        text = "Đăng nhập",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Utility Functions
private fun getPasswordStrengthColor(level: PasswordStrengthLevel): Color {
    return when (level) {
        PasswordStrengthLevel.WEAK -> PTITError
        PasswordStrengthLevel.MEDIUM -> PTITWarning
        PasswordStrengthLevel.STRONG -> PTITInfo
        PasswordStrengthLevel.VERY_STRONG -> PTITSuccess
    }
}

private fun isFormValid(uiState: SignUpUiState): Boolean {
    return uiState.fullName.isNotBlank() &&
            uiState.email.isNotBlank() &&
            uiState.password.isNotBlank() &&
            uiState.confirmPassword.isNotBlank() &&
            uiState.acceptTerms &&
            uiState.validationErrors.isEmpty()
}

// Hàm giả định tính sức mạnh mật khẩu (để dùng trong Preview)
private fun calculatePasswordStrengthPreview(password: String): PasswordStrength {
    if (password.isEmpty()) {
        return PasswordStrength(0, "Nhập mật khẩu", PasswordStrengthLevel.WEAK)
    }

    var score = 0
    if (password.length >= 8) score += 20
    if (password.matches(".*[a-z].*".toRegex())) score += 15
    if (password.matches(".*[A-Z].*".toRegex())) score += 15
    if (password.matches(".*\\d.*".toRegex())) score += 15
    if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*".toRegex())) score += 20
    if (!password.contains(" ")) score += 10
    if (password.length >= 12) score += 5

    return when {
        score < 40 -> PasswordStrength(score, "Mật khẩu yếu", PasswordStrengthLevel.WEAK)
        score < 70 -> PasswordStrength(score, "Mật khẩu trung bình", PasswordStrengthLevel.MEDIUM)
        score < 90 -> PasswordStrength(score, "Mật khẩu mạnh", PasswordStrengthLevel.STRONG)
        else -> PasswordStrength(score, "Mật khẩu rất mạnh", PasswordStrengthLevel.VERY_STRONG)
    }
}

// Preview Composables
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewSignUpScreen() {
    PtitjobTheme {
        var uiState by remember { 
            mutableStateOf(
                SignUpUiState(
                    fullName = "",
                    email = "",
                    password = "",
                    confirmPassword = "",
                    phoneNumber = ""
                )
            ) 
        }

        SignUpScreen(
            uiState = uiState,
            onFullNameChange = { newName -> uiState = uiState.copy(fullName = newName) },
            onEmailChange = { newEmail -> uiState = uiState.copy(email = newEmail) },
            onPasswordChange = { newPassword -> uiState = uiState.copy(password = newPassword) },
            onConfirmPasswordChange = { newConfirmPassword -> uiState = uiState.copy(confirmPassword = newConfirmPassword) },
            onPhoneNumberChange = { newPhone -> uiState = uiState.copy(phoneNumber = newPhone) },
            onAcceptTermsChange = { newAccept -> uiState = uiState.copy(acceptTerms = newAccept) },
            onToggleShowPassword = { uiState = uiState.copy(showPassword = !uiState.showPassword) },
            onToggleShowConfirmPassword = { uiState = uiState.copy(showConfirmPassword = !uiState.showConfirmPassword) },
            onSubmit = { uiState = uiState.copy(loading = true) },
            onLoginClick = { /* Navigate to login */ },
            onViewTerms = { /* Show terms */ },
            onViewPrivacy = { /* Show privacy */ },
            calculatePasswordStrength = ::calculatePasswordStrengthPreview
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewSignUpScreenWithError() {
    PtitjobTheme {
        var uiState by remember {
            mutableStateOf(
                SignUpUiState(
                    fullName = "A",
                    email = "invalid-email",
                    password = "123",
                    confirmPassword = "1234",
                    phoneNumber = "abc",
                    acceptTerms = false,
                    validationErrors = mapOf(
                        "fullName" to "Họ và tên phải có ít nhất 2 ký tự",
                        "email" to "Email không đúng định dạng",
                        "password" to "Mật khẩu quá yếu",
                        "confirmPassword" to "Mật khẩu xác nhận không khớp",
                        "phoneNumber" to "Số điện thoại không đúng định dạng",
                        "acceptTerms" to "Vui lòng đồng ý với các điều khoản dịch vụ",
                        "general" to "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại."
                    )
                )
            )
        }

        SignUpScreen(
            uiState = uiState,
            onFullNameChange = { newName -> uiState = uiState.copy(fullName = newName) },
            onEmailChange = { newEmail -> uiState = uiState.copy(email = newEmail) },
            onPasswordChange = { newPassword -> uiState = uiState.copy(password = newPassword) },
            onConfirmPasswordChange = { newConfirmPassword -> uiState = uiState.copy(confirmPassword = newConfirmPassword) },
            onPhoneNumberChange = { newPhone -> uiState = uiState.copy(phoneNumber = newPhone) },
            onAcceptTermsChange = { newAccept -> uiState = uiState.copy(acceptTerms = newAccept) },
            onToggleShowPassword = { uiState = uiState.copy(showPassword = !uiState.showPassword) },
            onToggleShowConfirmPassword = { uiState = uiState.copy(showConfirmPassword = !uiState.showConfirmPassword) },
            onSubmit = { /* Handle submit */ },
            onLoginClick = { /* Navigate to login */ },
            onViewTerms = { /* Show terms */ },
            onViewPrivacy = { /* Show privacy */ },
            calculatePasswordStrength = ::calculatePasswordStrengthPreview
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewSignUpScreenLoading() {
    PtitjobTheme {
        var uiState by remember {
            mutableStateOf(
                SignUpUiState(
                    fullName = "Nguyễn Văn A",
                    email = "test@ptit.edu.vn",
                    password = "Password123!",
                    confirmPassword = "Password123!",
                    phoneNumber = "0901234567",
                    acceptTerms = true,
                    loading = true
                )
            )
        }

        SignUpScreen(
            uiState = uiState,
            onFullNameChange = { /* no-op */ },
            onEmailChange = { /* no-op */ },
            onPasswordChange = { /* no-op */ },
            onConfirmPasswordChange = { /* no-op */ },
            onPhoneNumberChange = { /* no-op */ },
            onAcceptTermsChange = { /* no-op */ },
            onToggleShowPassword = { /* no-op */ },
            onToggleShowConfirmPassword = { /* no-op */ },
            onSubmit = { /* no-op */ },
            onLoginClick = { /* Navigate to login */ },
            onViewTerms = { /* Show terms */ },
            onViewPrivacy = { /* Show privacy */ },
            calculatePasswordStrength = ::calculatePasswordStrengthPreview
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewSignUpScreenSuccess() {
    PtitjobTheme {
        var uiState by remember {
            mutableStateOf(
                SignUpUiState(
                    registrationSuccess = true,
                    redirectCountdown = 5
                )
            )
        }

        SignUpScreen(
            uiState = uiState,
            onFullNameChange = { /* no-op */ },
            onEmailChange = { /* no-op */ },
            onPasswordChange = { /* no-op */ },
            onConfirmPasswordChange = { /* no-op */ },
            onPhoneNumberChange = { /* no-op */ },
            onAcceptTermsChange = { /* no-op */ },
            onToggleShowPassword = { /* no-op */ },
            onToggleShowConfirmPassword = { /* no-op */ },
            onSubmit = { /* no-op */ },
            onLoginClick = { /* Navigate to login */ },
            onViewTerms = { /* Show terms */ },
            onViewPrivacy = { /* Show privacy */ },
            calculatePasswordStrength = ::calculatePasswordStrengthPreview
        )
    }
}