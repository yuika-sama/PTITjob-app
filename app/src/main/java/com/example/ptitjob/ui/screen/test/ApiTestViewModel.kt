package com.example.ptitjob.ui.screen.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.api.dto.UserRole
import com.example.ptitjob.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiTestViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val jobApplicationRepository: JobApplicationRepository,
    private val resumeRepository: ResumeRepository,
    private val jobCategoryRepository: JobCategoryRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ApiTestState>(ApiTestState.Idle)
    val uiState: StateFlow<ApiTestState> = _uiState

    private val _testResults = MutableStateFlow<List<TestResult>>(emptyList())
    val testResults: StateFlow<List<TestResult>> = _testResults

    // Auth APIs
    fun testLogin(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = authRepository.login(email, password)
            
            result.onSuccess { response ->
                addTestResult("Login", true, "Success: ${response.message}")
                _uiState.value = ApiTestState.Success(response.message)
            }.onFailure { error ->
                addTestResult("Login", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun testRegister(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = authRepository.register(
                email = email,
                password = password,
                fullName = fullName,
                role = "candidate"
            )
            
            result.onSuccess { response ->
                addTestResult("Register", true, "Success: ${response.message}")
                _uiState.value = ApiTestState.Success(response.message)
            }.onFailure { error ->
                addTestResult("Register", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun testGetCurrentUser() {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = authRepository.getCurrentUser()
            
            result.onSuccess { response ->
                addTestResult("Get Current User", true, "Success: ${response.data?.fullName}")
                _uiState.value = ApiTestState.Success("User: ${response.data?.fullName}")
            }.onFailure { error ->
                addTestResult("Get Current User", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    // User APIs
    fun testGetAllUsers() {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = userRepository.getAllUsers()
            
            result.onSuccess { response ->
                addTestResult("Get All Users", true, "Success: ${response.data?.size} users")
                _uiState.value = ApiTestState.Success("Found ${response.data?.size} users")
            }.onFailure { error ->
                addTestResult("Get All Users", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    // Job APIs
    fun testGetAllJobs() {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = jobRepository.getAllJobs()
            
            result.onSuccess { response ->
                addTestResult("Get All Jobs", true, "Success: ${response.data?.size} jobs")
                _uiState.value = ApiTestState.Success("Found ${response.data?.size} jobs")
            }.onFailure { error ->
                addTestResult("Get All Jobs", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun testSearchJobs(title: String) {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = jobRepository.searchJobs(title = title)
            
            result.onSuccess { response ->
                addTestResult("Search Jobs", true, "Success: ${response.data?.size} jobs found")
                _uiState.value = ApiTestState.Success("Found ${response.data?.size} jobs")
            }.onFailure { error ->
                addTestResult("Search Jobs", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    // Company APIs
    fun testGetAllCompanies() {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = companyRepository.getAllCompanies()
            
            result.onSuccess { response ->
                addTestResult("Get All Companies", true, "Success: ${response.data?.size} companies")
                _uiState.value = ApiTestState.Success("Found ${response.data?.size} companies")
            }.onFailure { error ->
                addTestResult("Get All Companies", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    // Category APIs
    fun testGetAllCategories() {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = jobCategoryRepository.getAllCategories()
            
            result.onSuccess { response ->
                addTestResult("Get All Categories", true, "Success: ${response.data?.size} categories")
                _uiState.value = ApiTestState.Success("Found ${response.data?.size} categories")
            }.onFailure { error ->
                addTestResult("Get All Categories", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    // Location APIs
    fun testGetAllLocations() {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = locationRepository.getAllLocations()
            
            result.onSuccess { response ->
                addTestResult("Get All Locations", true, "Success: ${response.data?.size} locations")
                _uiState.value = ApiTestState.Success("Found ${response.data?.size} locations")
            }.onFailure { error ->
                addTestResult("Get All Locations", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun testSearchLocations(query: String) {
        viewModelScope.launch {
            _uiState.value = ApiTestState.Loading
            val result = locationRepository.searchLocations(query)
            
            result.onSuccess { response ->
                addTestResult("Search Locations", true, "Success: ${response.data?.size} locations")
                _uiState.value = ApiTestState.Success("Found ${response.data?.size} locations")
            }.onFailure { error ->
                addTestResult("Search Locations", false, "Error: ${error.message}")
                _uiState.value = ApiTestState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun clearResults() {
        _testResults.value = emptyList()
        _uiState.value = ApiTestState.Idle
    }

    private fun addTestResult(apiName: String, success: Boolean, message: String) {
        val result = TestResult(
            apiName = apiName,
            success = success,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        _testResults.value = _testResults.value + result
    }
}

sealed class ApiTestState {
    object Idle : ApiTestState()
    object Loading : ApiTestState()
    data class Success(val message: String) : ApiTestState()
    data class Error(val message: String) : ApiTestState()
}

data class TestResult(
    val apiName: String,
    val success: Boolean,
    val message: String,
    val timestamp: Long
)
