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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
        clearFieldError("email")
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
        clearFieldError("password")
    }

    fun updateRememberMe(remember: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = remember)
    }

    fun toggleShowPassword() {
        _uiState.value = _uiState.value.copy(showPassword = !_uiState.value.showPassword)
    }

    fun fillTestCredentials(email: String, password: String) {
        _uiState.value = _uiState.value.copy(email = email, password = password, validationErrors = emptyMap())
    }

    private fun setFieldError(field: String, message: String) {
        val current = _uiState.value.validationErrors.toMutableMap()
        current[field] = message
        _uiState.value = _uiState.value.copy(validationErrors = current)
    }

    private fun clearFieldError(field: String) {
        val current = _uiState.value.validationErrors.toMutableMap()
        current.remove(field)
        _uiState.value = _uiState.value.copy(validationErrors = current)
    }

    private fun validate(): Boolean {
        var ok = true
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (!EMAIL_REGEX.matches(email)) {
            setFieldError("email", "Email không đúng định dạng")
            ok = false
        }
        if (password.length < 6) {
            setFieldError("password", "Mật khẩu phải có ít nhất 6 ký tự")
            ok = false
        }
        return ok
    }

    fun submit(onAuthenticated: () -> Unit) {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val email = _uiState.value.email.trim()
            val password = _uiState.value.password
            val result = authRepository.login(email, password)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(loading = false, isAuthenticated = true)
                onAuthenticated()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(loading = false)
                setFieldError("general", e.message ?: "Đăng nhập thất bại")
            }
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}


