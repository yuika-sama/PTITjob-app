package com.example.ptitjob.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.SharedPreferences
import com.google.gson.Gson
import com.example.ptitjob.data.api.dto.UserDto
import com.example.ptitjob.data.model.Company
import com.example.ptitjob.data.model.Job
import com.example.ptitjob.data.repository.*
import com.example.ptitjob.ui.navigation.DashboardSearchPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application-wide ViewModel
 * Manages global state, navigation data, and cross-screen functionality
 */
@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val authRepository: AuthRepository,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : ViewModel() {

    private val _appState = MutableStateFlow(ApplicationState())
    val appState: StateFlow<ApplicationState> = _appState.asStateFlow()

    // Navigation state
    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    // Search payload for cross-screen navigation
    private val _searchPayload = MutableStateFlow<DashboardSearchPayload?>(null)
    val searchPayload: StateFlow<DashboardSearchPayload?> = _searchPayload.asStateFlow()

    init {
        // Check if user has valid token on app start
        val hasToken = !sharedPreferences.getString("accessToken", null).isNullOrEmpty()
        if (hasToken) {
            loadUserProfile()
        }
    }

    /**
     * Load current user profile
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            // First try to get user from stored data (faster, no network call)
            val storedUser = getStoredUser()
            if (storedUser != null) {
                _appState.value = _appState.value.copy(
                    currentUser = storedUser,
                    isAuthenticated = true
                )
            }
            
            // Then try to refresh from server (can fail without affecting auth state)
            val result = authRepository.getCurrentUser()
            result.fold(
                onSuccess = { response ->
                    _appState.value = _appState.value.copy(
                        currentUser = response.data,
                        isAuthenticated = response.data != null
                    )
                },
                onFailure = {
                    // If we have stored user, don't set isAuthenticated = false
                    // Only set false if we don't have any user data
                    if (storedUser == null) {
                        _appState.value = _appState.value.copy(
                            currentUser = null,
                            isAuthenticated = false
                        )
                    }
                    // If we have stored user but API fails, keep authenticated state
                }
            )
        }
    }
    
    /**
     * Get user from SharedPreferences
     */
    private fun getStoredUser(): UserDto? {
        return try {
            val userJson = sharedPreferences.getString("ptitjob_user", null)
            if (!userJson.isNullOrEmpty()) {
                gson.fromJson(userJson, UserDto::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Navigate with search payload
     */
    fun navigateWithSearch(payload: DashboardSearchPayload) {
        _searchPayload.value = payload
    }

    /**
     * Clear search payload after consumption
     */
    fun clearSearchPayload() {
        _searchPayload.value = null
    }

    /**
     * Set current job for detail view
     */
    fun setCurrentJob(jobId: String) {
        viewModelScope.launch {
            try {
                val result = jobRepository.getJobById(jobId)
                result.fold(
                    onSuccess = { response ->
                        response.data?.let { _ ->
                            // You might want to convert JobDto to Job model here
                            _navigationState.value = _navigationState.value.copy(
                                currentJobId = jobId
                            )
                        }
                    },
                    onFailure = { /* Handle error */ }
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Set current company for detail view
     */
    fun setCurrentCompany(companyId: String) {
        viewModelScope.launch {
            try {
                val result = companyRepository.getCompanyById(companyId)
                result.fold(
                    onSuccess = { response ->
                        response.data?.let { _ ->
                            _navigationState.value = _navigationState.value.copy(
                                currentCompanyId = companyId
                            )
                        }
                    },
                    onFailure = { /* Handle error */ }
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Add job to favorites
     */
    fun addToFavorites(jobId: String) {
        viewModelScope.launch {
            try {
                // Implement API call to add job to favorites
                val currentFavorites = _appState.value.favoriteJobIds.toMutableSet()
                currentFavorites.add(jobId)
                _appState.value = _appState.value.copy(
                    favoriteJobIds = currentFavorites
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Remove job from favorites
     */
    fun removeFromFavorites(jobId: String) {
        viewModelScope.launch {
            try {
                // Implement API call to remove job from favorites
                val currentFavorites = _appState.value.favoriteJobIds.toMutableSet()
                currentFavorites.remove(jobId)
                _appState.value = _appState.value.copy(
                    favoriteJobIds = currentFavorites
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Apply to job
     */
    fun applyToJob(jobId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Implement job application logic here
                // This would typically involve:
                // 1. Check if user has complete profile
                // 2. Submit application via JobApplicationRepository
                // 3. Handle response
                
                val currentApplications = _appState.value.appliedJobIds.toMutableSet()
                currentApplications.add(jobId)
                _appState.value = _appState.value.copy(
                    appliedJobIds = currentApplications
                )
                
                onResult(true, "Ứng tuyển thành công!")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Ứng tuyển thất bại")
            }
        }
    }

    /**
     * Update navigation history
     */
    fun updateNavigationHistory(route: String) {
        val currentHistory = _navigationState.value.navigationHistory.toMutableList()
        currentHistory.add(route)
        
        // Keep only last 10 routes
        if (currentHistory.size > 10) {
            currentHistory.removeAt(0)
        }
        
        _navigationState.value = _navigationState.value.copy(
            navigationHistory = currentHistory,
            currentRoute = route
        )
    }

    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            try {
                // Get refresh token from SharedPreferences
                val refreshToken = sharedPreferences.getString("refreshToken", null)
                
                if (!refreshToken.isNullOrEmpty()) {
                    // Call logout API with refresh token
                    authRepository.logout(refreshToken)
                }
                
                // Clear stored tokens and user data regardless of API result
                sharedPreferences.edit().apply {
                    remove("accessToken")
                    remove("refreshToken")
                    remove("ptitjob_user")
                    apply()
                }
                
                // Reset application state
                _appState.value = ApplicationState() // Reset to default state
                _navigationState.value = NavigationState() // Reset navigation state
                
            } catch (e: Exception) {
                // Even if logout API fails, clear local data
                sharedPreferences.edit().apply {
                    remove("accessToken")
                    remove("refreshToken")
                    remove("ptitjob_user")
                    apply()
                }
                _appState.value = ApplicationState()
                _navigationState.value = NavigationState()
            }
        }
    }

    /**
     * Check if job is favorited
     */
    fun isJobFavorited(jobId: String): Boolean {
        return _appState.value.favoriteJobIds.contains(jobId)
    }

    /**
     * Check if already applied to job
     */
    fun hasAppliedToJob(jobId: String): Boolean {
        return _appState.value.appliedJobIds.contains(jobId)
    }

    /**
     * Update app theme or settings
     */
    fun updateAppSettings(settings: AppSettings) {
        _appState.value = _appState.value.copy(
            appSettings = settings
        )
    }

    /**
     * Set app loading state
     */
    fun setGlobalLoading(isLoading: Boolean) {
        _appState.value = _appState.value.copy(
            isGlobalLoading = isLoading
        )
    }

    /**
     * Set global error message
     */
    fun setGlobalError(message: String?) {
        _appState.value = _appState.value.copy(
            globalErrorMessage = message
        )
    }
}

/**
 * Application state data class
 */
data class ApplicationState(
    val currentUser: UserDto? = null,
    val isAuthenticated: Boolean = false,
    val favoriteJobIds: Set<String> = emptySet(),
    val appliedJobIds: Set<String> = emptySet(),
    val appSettings: AppSettings = AppSettings(),
    val isGlobalLoading: Boolean = false,
    val globalErrorMessage: String? = null
)

/**
 * Navigation state data class
 */
data class NavigationState(
    val currentRoute: String = "",
    val navigationHistory: List<String> = emptyList(),
    val currentJobId: String? = null,
    val currentCompanyId: String? = null
)

/**
 * App settings data class
 */
data class AppSettings(
    val isDarkMode: Boolean = false,
    val language: String = "vi",
    val notificationsEnabled: Boolean = true,
    val autoRefreshEnabled: Boolean = true
)