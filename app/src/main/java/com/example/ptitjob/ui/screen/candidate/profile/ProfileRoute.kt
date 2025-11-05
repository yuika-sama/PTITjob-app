package com.example.ptitjob.ui.screen.candidate.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileRoute(
    onBack: () -> Unit = {},
    vm: ProfileViewModel = hiltViewModel()
) {
    val user by vm.userState.collectAsState(initial = null)
    val isLoading by vm.isLoading.collectAsState(initial = false)

    // Provide callbacks to the UI which delegate to the ViewModel
    val handleUpdate: (EditFormData, (Boolean, String?) -> Unit) -> Unit = { editData, cb ->
        vm.updateProfile(editData) { success, message ->
            cb(success, message)
        }
    }

    val handleChangePassword: (PasswordFormData, (Boolean, String?) -> Unit) -> Unit =
        { passData, cb ->
            vm.changePassword(passData) { success, message ->
                cb(success, message)
            }
        }

    // Render the ProfileScreen with real callbacks
    ProfileScreen(
        user = user ?: UserProfile(
            id = "",
            fullName = "",
            email = "",
            phoneNumber = null,
            studentId = null,
            major = null,
            graduationYear = null,
            role = "",
            isActive = false,
            createdAt = "",
            avatarUrl = null,
            bio = null,
            skills = emptyList(),
            achievements = emptyList()
        ),
        onBack = onBack,
        onUpdateProfile = { editData, callback -> handleUpdate(editData, callback) },
        onChangePassword = { passData, callback -> handleChangePassword(passData, callback) },
        onSettingsNavigate = { /* TODO: route to settings screen if exists */ }
    )
}
