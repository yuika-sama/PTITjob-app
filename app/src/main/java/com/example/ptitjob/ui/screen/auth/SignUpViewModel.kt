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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun updateFullName(v: String) { _uiState.value = _uiState.value.copy(fullName = v); clearError("fullName") }
    fun updateEmail(v: String) { _uiState.value = _uiState.value.copy(email = v); clearError("email") }
    fun updatePassword(v: String) { _uiState.value = _uiState.value.copy(password = v); clearError("password") }
    fun updateConfirmPassword(v: String) { _uiState.value = _uiState.value.copy(confirmPassword = v); clearError("confirmPassword") }
    fun updatePhoneNumber(v: String) { _uiState.value = _uiState.value.copy(phoneNumber = v); clearError("phoneNumber") }
    fun updateAcceptTerms(v: Boolean) { _uiState.value = _uiState.value.copy(acceptTerms = v); clearError("acceptTerms") }
    fun toggleShowPassword() { _uiState.value = _uiState.value.copy(showPassword = !_uiState.value.showPassword) }
    fun toggleShowConfirmPassword() { _uiState.value = _uiState.value.copy(showConfirmPassword = !_uiState.value.showConfirmPassword) }

    private fun setError(field: String, message: String) {
        val m = _uiState.value.validationErrors.toMutableMap(); m[field] = message
        _uiState.value = _uiState.value.copy(validationErrors = m)
    }
    private fun clearError(field: String) {
        val m = _uiState.value.validationErrors.toMutableMap(); m.remove(field)
        _uiState.value = _uiState.value.copy(validationErrors = m)
    }

    fun calculatePasswordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength(0, "Nhập mật khẩu", PasswordStrengthLevel.WEAK)
        var score = 0
        if (password.length >= 8) score += 20
        if (password.matches(Regex(".*[a-z].*"))) score += 15
        if (password.matches(Regex(".*[A-Z].*"))) score += 15
        if (password.matches(Regex(".*\\d.*"))) score += 15
        if (password.matches(Regex(".*[!@#$%^&*(),.?\":{}|<>].*"))) score += 20
        if (!password.contains(" ")) score += 10
        if (password.length >= 12) score += 5
        return when {
            score < 40 -> PasswordStrength(score, "Mật khẩu yếu", PasswordStrengthLevel.WEAK)
            score < 70 -> PasswordStrength(score, "Mật khẩu trung bình", PasswordStrengthLevel.MEDIUM)
            score < 90 -> PasswordStrength(score, "Mật khẩu mạnh", PasswordStrengthLevel.STRONG)
            else -> PasswordStrength(score, "Mật khẩu rất mạnh", PasswordStrengthLevel.VERY_STRONG)
        }
    }

    private fun validate(): Boolean {
        var ok = true
        val s = _uiState.value
        if (s.fullName.trim().length < 2) { setError("fullName", "Họ và tên phải có ít nhất 2 ký tự"); ok = false }
        if (!EMAIL_REGEX.matches(s.email.trim())) { setError("email", "Email không đúng định dạng"); ok = false }
        if (s.password.length < 6) { setError("password", "Mật khẩu quá yếu"); ok = false }
        if (s.password != s.confirmPassword) { setError("confirmPassword", "Mật khẩu xác nhận không khớp"); ok = false }
        if (s.phoneNumber.isNotBlank() && !PHONE_REGEX.matches(s.phoneNumber)) { setError("phoneNumber", "Số điện thoại không đúng định dạng"); ok = false }
        if (!s.acceptTerms) { setError("acceptTerms", "Vui lòng đồng ý với các điều khoản dịch vụ"); ok = false }
        return ok
    }

    fun submit(onRegistrationDone: () -> Unit) {
        if (!validate()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val s = _uiState.value
            val result = authRepository.register(
                email = s.email.trim(),
                password = s.password,
                fullName = s.fullName.trim(),
                phoneNumber = if (s.phoneNumber.isBlank()) null else s.phoneNumber,
                role = "candidate"
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    registrationSuccess = true,
                    redirectCountdown = 3
                )
                // simple countdown then callback
                viewModelScope.launch {
                    var count = 3
                    while (count > 0) {
                        delay(1000)
                        count -= 1
                        _uiState.value = _uiState.value.copy(redirectCountdown = count)
                    }
                    onRegistrationDone()
                }
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(loading = false)
                setError("general", e.message ?: "Đăng ký thất bại")
            }
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        private val PHONE_REGEX = Regex("^(0|\\+84)(\\d){9}$")
    }
}


