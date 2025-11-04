package com.example.ptitjob.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginRoute(
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgot: () -> Unit,
    onAuthenticated: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onRememberMeChange = viewModel::updateRememberMe,
        onToggleShowPassword = viewModel::toggleShowPassword,
        onSubmit = { viewModel.submit(onAuthenticated) },
        onForgotPasswordClick = onNavigateToForgot,
        onSignUpClick = onNavigateToSignUp,
        onFillTestCredentials = viewModel::fillTestCredentials
    )
}


