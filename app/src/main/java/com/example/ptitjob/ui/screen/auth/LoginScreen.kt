package com.example.ptitjob.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ptitjob.R
import com.example.ptitjob.ui.component.*
import com.example.ptitjob.ui.theme.*

// Data class để mô phỏng state UI
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = true,
    val showPassword: Boolean = false,
    val loading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onToggleShowPassword: () -> Unit,
    onSubmit: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            
            // App Logo and Welcome Text
            LoginHeader()
            
            Spacer(modifier = Modifier.height(PTITSpacing.xl))
            
            // Login Form Card
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
                        text = "Đăng nhập",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITNeutral900
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    // Login Form
                    LoginForm(
                        uiState = uiState,
                        onEmailChange = onEmailChange,
                        onPasswordChange = onPasswordChange,
                        onRememberMeChange = onRememberMeChange,
                        onToggleShowPassword = onToggleShowPassword,
                        onSubmit = onSubmit,
                        onForgotPasswordClick = onForgotPasswordClick
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(PTITSpacing.xl))
            
            // Sign Up Section
            LoginFooter(onSignUpClick)
            
            Spacer(modifier = Modifier.height(PTITSpacing.xl))
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
    ) {
        // App Icon with PTIT logo
        Surface(
            modifier = Modifier.size(PTITSize.cardLg),
            shape = PTITCornerRadius.md,
            color = Color.White
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "PTIT Logo",
                modifier = Modifier.fillMaxSize().padding(PTITSpacing.sm),
                contentScale = ContentScale.Fit
            )
        }
        
        // Welcome text
        Text(
            text = "Chào mừng trở lại!",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = PTITNeutral900
            ),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Kết nối với cơ hội nghề nghiệp tại PTIT",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = PTITNeutral700
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoginForm(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onToggleShowPassword: () -> Unit,
    onSubmit: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
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
        
        // Password Field
        AuthTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = "Mật khẩu",
            placeholder = "Nhập mật khẩu của bạn",
            leadingIcon = Icons.Default.Lock,
//            keyboardType = KeyboardType.Password,
            isPassword = true,
//            showPassword = uiState.showPassword,
//            onTogglePasswordVisibility = onToggleShowPassword,
            isError = uiState.validationErrors.containsKey("password"),
//            errorText = uiState.validationErrors["password"],
//            enabled = !uiState.loading
        )
        
        // Remember Me & Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Checkbox(
                    checked = uiState.rememberMe,
                    onCheckedChange = onRememberMeChange,
                    enabled = !uiState.loading,
                    colors = CheckboxDefaults.colors(
                        checkedColor = PTITPrimary,
                        uncheckedColor = PTITNeutral400
                    )
                )
                Text(
                    text = "Ghi nhớ tài khoản",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PTITNeutral700
                    )
                )
            }
            
            TextButton(
                onClick = onForgotPasswordClick,
                enabled = !uiState.loading
            ) {
                Text(
                    text = "Quên mật khẩu?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PTITPrimary
                    )
                )
            }
        }
        
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
            text = "Đăng nhập",
//            loading = uiState.loading,
            enabled = !uiState.loading && uiState.email.isNotBlank() && uiState.password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LoginFooter(onSignUpClick: () -> Unit) {
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
                text = "Bạn chưa có tài khoản? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITNeutral700
                )
            )
            TextButton(
                onClick = onSignUpClick,
                contentPadding = PaddingValues(horizontal = PTITSpacing.sm)
            ) {
                Text(
                    text = "Đăng ký ngay",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
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
private fun PreviewLoginScreen() {
    PtitjobTheme {
        var uiState by remember { 
            mutableStateOf(
                LoginUiState(
                    email = "",
                    password = ""
                )
            ) 
        }

        LoginScreen(
            uiState = uiState,
            onEmailChange = { newEmail -> uiState = uiState.copy(email = newEmail) },
            onPasswordChange = { newPassword -> uiState = uiState.copy(password = newPassword) },
            onRememberMeChange = { newState -> uiState = uiState.copy(rememberMe = newState) },
            onToggleShowPassword = { uiState = uiState.copy(showPassword = !uiState.showPassword) },
            onSubmit = { uiState = uiState.copy(loading = true) },
            onForgotPasswordClick = { /* Navigate to forgot password */ },
            onSignUpClick = { /* Navigate to sign up */ }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewLoginScreenWithError() {
    PtitjobTheme {
        var uiState by remember {
            mutableStateOf(
                LoginUiState(
                    email = "invalid-email",
                    password = "123",
                    validationErrors = mapOf(
                        "email" to "Email không đúng định dạng",
                        "password" to "Mật khẩu phải có ít nhất 6 ký tự",
                        "general" to "Email hoặc mật khẩu không chính xác. Vui lòng thử lại."
                    )
                )
            )
        }

        LoginScreen(
            uiState = uiState,
            onEmailChange = { newEmail -> uiState = uiState.copy(email = newEmail) },
            onPasswordChange = { newPassword -> uiState = uiState.copy(password = newPassword) },
            onRememberMeChange = { newState -> uiState = uiState.copy(rememberMe = newState) },
            onToggleShowPassword = { uiState = uiState.copy(showPassword = !uiState.showPassword) },
            onSubmit = { /* Handle submit */ },
            onForgotPasswordClick = { /* Navigate to forgot password */ },
            onSignUpClick = { /* Navigate to sign up */ }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewLoginScreenLoading() {
    PtitjobTheme {
        var uiState by remember {
            mutableStateOf(
                LoginUiState(
                    email = "test@ptit.edu.vn",
                    password = "Password123",
                    loading = true
                )
            )
        }

        LoginScreen(
            uiState = uiState,
            onEmailChange = { newEmail -> uiState = uiState.copy(email = newEmail) },
            onPasswordChange = { newPassword -> uiState = uiState.copy(password = newPassword) },
            onRememberMeChange = { newState -> uiState = uiState.copy(rememberMe = newState) },
            onToggleShowPassword = { uiState = uiState.copy(showPassword = !uiState.showPassword) },
            onSubmit = { /* Handle submit */ },
            onForgotPasswordClick = { /* Navigate to forgot password */ },
            onSignUpClick = { /* Navigate to sign up */ }
        )
    }
}
