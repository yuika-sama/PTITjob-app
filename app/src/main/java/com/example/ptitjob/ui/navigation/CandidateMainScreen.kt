package com.example.ptitjob.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ptitjob.data.repository.AuthRepository
import com.example.ptitjob.ui.component.PTITAppContainer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Main Container Screen cho Candidate
 * Sử dụng PTITAppContainer mới với Bottom Navigation và layout cải tiến
 */
@Composable
fun CandidateMainScreen(
    navController: NavHostController = rememberNavController()
) {
    // Wrapper để inject AuthRepository
    val viewModel: CandidateNavGraphViewModel = hiltViewModel()
    
    // Redirect to the new PTIT App Container for consistent theming
    PTITAppContainer(
        navController = navController,
        hasGradientBackground = false // Let individual screens handle their backgrounds
    ) {
        CandidateNavGraph(
            navController = navController,
            authRepository = viewModel.authRepository
        )
    }
}

/**
 * ViewModel đơn giản để inject AuthRepository
 */
@HiltViewModel
class CandidateNavGraphViewModel @Inject constructor(
    val authRepository: AuthRepository
) : androidx.lifecycle.ViewModel()
