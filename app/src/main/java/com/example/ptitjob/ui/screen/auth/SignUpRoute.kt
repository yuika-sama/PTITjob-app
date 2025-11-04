package com.example.ptitjob.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SignUpRoute(
    onNavigateToLogin: () -> Unit,
    onRegistrationDone: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SignUpScreen(
        uiState = uiState,
        onFullNameChange = viewModel::updateFullName,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onPhoneNumberChange = viewModel::updatePhoneNumber,
        onAcceptTermsChange = viewModel::updateAcceptTerms,
        onToggleShowPassword = viewModel::toggleShowPassword,
        onToggleShowConfirmPassword = viewModel::toggleShowConfirmPassword,
        onSubmit = { viewModel.submit(onRegistrationDone) },
        onLoginClick = onNavigateToLogin,
        onViewTerms = { /* open terms */ },
        onViewPrivacy = { /* open privacy */ },
        calculatePasswordStrength = viewModel::calculatePasswordStrength
    )
}


