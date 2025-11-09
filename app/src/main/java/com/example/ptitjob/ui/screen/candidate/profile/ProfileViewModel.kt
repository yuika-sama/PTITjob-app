package com.example.ptitjob.ui.screen.candidate.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.SharedPreferences
import com.google.gson.Gson
import com.example.ptitjob.data.api.dto.UserDto
import com.example.ptitjob.data.api.request.UpdateUserRequest
import com.example.ptitjob.data.repository.AuthRepository
import com.example.ptitjob.data.repository.ResumeRepository
import com.example.ptitjob.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val resumeRepository: ResumeRepository,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : ViewModel() {

    // UI state exposed to composables
    private val _userState = MutableStateFlow<UserProfile?>(null)
    val userState: StateFlow<UserProfile?> = _userState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Load user from SharedPreferences first (immediate display)
        loadUserFromStorage()
        // Then refresh from API
        loadCurrentUser()
    }
    
    /**
     * Load user from SharedPreferences for immediate display
     */
    private fun loadUserFromStorage() {
        try {
            val userJson = sharedPreferences.getString("ptitjob_user", null)
            if (!userJson.isNullOrEmpty()) {
                val userDto = gson.fromJson(userJson, UserDto::class.java)
                _userState.value = mapDtoToUi(userDto)
                _isLoading.value = false
            }
        } catch (e: Exception) {
            // If parsing fails, continue with API call
        }
    }

    private fun mapDtoToUi(dto: UserDto): UserProfile = UserProfile(
        id = dto.id,
        fullName = dto.fullName,
        email = dto.email,
        phoneNumber = dto.phoneNumber,
        studentId = null,
        major = null,
        graduationYear = null,
        role = dto.role.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
        isActive = dto.isActive,
        createdAt = dto.createdAt,
        avatarUrl = null,
        bio = null,
        skills = emptyList(),
        achievements = emptyList()
    )

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = userRepository.getCurrentUser()
                result.fold(onSuccess = { apiResp ->
                    val dto = apiResp.data
                    if (dto != null) {
                        _userState.value = mapDtoToUi(dto)
                        // Save updated user data to SharedPreferences
                        saveUserToStorage(dto)
                    } else {
                        _errorMessage.value = apiResp.message.ifBlank { "Không tìm thấy dữ liệu người dùng" }
                    }
                }, onFailure = { err ->
                    _errorMessage.value = err.message ?: "Lỗi khi tải người dùng"
                })
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Save user data to SharedPreferences
     */
    private fun saveUserToStorage(userDto: UserDto) {
        try {
            val userJson = gson.toJson(userDto)
            sharedPreferences.edit().apply {
                putString("ptitjob_user", userJson)
                apply()
            }
        } catch (e: Exception) {
            // Ignore storage errors
        }
    }

    fun updateProfile(edit: EditFormData, onResult: (Boolean, String?) -> Unit) {
        val currentUser = _userState.value ?: run {
            onResult(false, "Không có người dùng")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val req = UpdateUserRequest(
                    email = null,
                    passwordHash = null,
                    fullName = edit.fullName.ifBlank { null },
                    phoneNumber = edit.phoneNumber.ifBlank { null },
                    role = null,
                    companyId = null,
                    isActive = null
                )

                val result = userRepository.updateCurrentUser(req)
                result.fold(onSuccess = { apiResp ->
                    val updatedDto = apiResp.data
                    if (updatedDto != null) {
                        _userState.value = mapDtoToUi(updatedDto)
                        // Save updated user data to SharedPreferences
                        saveUserToStorage(updatedDto)
                        onResult(true, null)
                    } else {
                        onResult(false, apiResp.message.ifBlank { "Cập nhật hồ sơ thất bại" })
                    }
                }, onFailure = { err ->
                    onResult(false, err.message ?: "Cập nhật hồ sơ thất bại")
                })
            } catch (e: Exception) {
                onResult(false, e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changePassword(passwordForm: PasswordFormData, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = authRepository.changePassword(passwordForm.current, passwordForm.new)
                result.fold(onSuccess = { _ ->
                    onResult(true, null)
                }, onFailure = { err ->
                    onResult(false, err.message ?: "Đổi mật khẩu thất bại")
                })
            } catch (e: Exception) {
                onResult(false, e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = authRepository.logout()
                result.fold(
                    onSuccess = { 
                        // Clear user state
                        _userState.value = null
                        onResult(true, null)
                    },
                    onFailure = { err ->
                        onResult(false, err.message ?: "Đăng xuất thất bại")
                    }
                )
            } catch (e: Exception) {
                onResult(false, e.message ?: "Lỗi khi đăng xuất")
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getUserResumes(): Result<List<com.example.ptitjob.data.api.dto.ResumeDto>> {
        val currentUser = _userState.value ?: return Result.failure(Exception("Không có người dùng"))
        val userId = currentUser.id.toIntOrNull()
            ?: return Result.failure(Exception("User ID không hợp lệ"))

        return try {
            val resp = resumeRepository.getResumesByUser(userId)
            resp.fold(
                onSuccess = { apiResp ->
                    val resumes = apiResp.data ?: emptyList()
                    Result.success(resumes)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
