package com.example.ptitjob.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ForgotPasswordRoute(
    onBackToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ForgotPasswordScreen(
        uiState = uiState,
        onEmailChange = viewModel::updateEmail,
        onTokenChange = viewModel::updateToken,
        onNewPasswordChange = viewModel::updateNewPassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onRequestResetLink = viewModel::requestResetLink,
        onVerifyToken = viewModel::verifyToken,
        onResetPassword = viewModel::resetPassword,
        onBackToLogin = onBackToLogin,
        onResendEmail = viewModel::resendEmail,
        onBackToEmailStep = viewModel::backToEmailStep,
        onResetForm = viewModel::resetForm
    )
}


