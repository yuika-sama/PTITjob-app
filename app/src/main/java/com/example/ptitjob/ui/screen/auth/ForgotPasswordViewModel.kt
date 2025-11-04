package com.example.ptitjob.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun updateEmail(v: String) { _uiState.value = _uiState.value.copy(email = v, errorMessage = null, validationError = null) }
    fun updateToken(v: String) { _uiState.value = _uiState.value.copy(token = v, errorMessage = null, validationError = null) }
    fun updateNewPassword(v: String) { _uiState.value = _uiState.value.copy(newPassword = v, errorMessage = null, validationError = null) }
    fun updateConfirmPassword(v: String) { _uiState.value = _uiState.value.copy(confirmPassword = v, errorMessage = null, validationError = null) }

    fun requestResetLink() {
        val email = _uiState.value.email.trim()
        if (!EMAIL_REGEX.matches(email)) {
            _uiState.value = _uiState.value.copy(validationError = "Email không đúng định dạng")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, validationError = null)
            val result = authRepository.forgotPassword(email)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(loading = false, currentStep = ForgotPasswordStep.TOKEN)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(loading = false, errorMessage = e.message)
            }
        }
    }

    fun verifyToken() {
        val token = _uiState.value.token.trim()
        if (token.isBlank()) {
            _uiState.value = _uiState.value.copy(validationError = "Vui lòng nhập mã xác thực")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, validationError = null)
            val result = authRepository.verifyResetToken(token)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(loading = false, currentStep = ForgotPasswordStep.PASSWORD, emailFromTokenVerification = _uiState.value.email)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(loading = false, errorMessage = e.message)
            }
        }
    }

    fun resetPassword() {
        val newPass = _uiState.value.newPassword
        val confirm = _uiState.value.confirmPassword
        if (newPass.length < 6) {
            _uiState.value = _uiState.value.copy(validationError = "Mật khẩu phải có ít nhất 6 ký tự")
            return
        }
        if (newPass != confirm) {
            _uiState.value = _uiState.value.copy(validationError = "Mật khẩu xác nhận không khớp")
            return
        }
        val token = _uiState.value.token
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, validationError = null)
            val result = authRepository.resetPassword(token, newPass)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(loading = false, passwordResetSuccess = true, redirectCountdown = 3)
                viewModelScope.launch {
                    var count = 3
                    while (count > 0) {
                        delay(1000)
                        count -= 1
                        _uiState.value = _uiState.value.copy(redirectCountdown = count)
                    }
                }
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(loading = false, errorMessage = e.message)
            }
        }
    }

    fun resendEmail() { requestResetLink() }
    fun backToEmailStep() { _uiState.value = _uiState.value.copy(currentStep = ForgotPasswordStep.EMAIL, token = "", errorMessage = null, validationError = null) }
    fun resetForm() { _uiState.value = ForgotPasswordUiState() }

    companion object { private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") }
}


