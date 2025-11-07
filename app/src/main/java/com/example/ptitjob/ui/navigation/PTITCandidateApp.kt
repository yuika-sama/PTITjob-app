package com.example.ptitjob.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ptitjob.ui.component.PTITAppContainer

/**
 * PTIT App Main Screen với Bottom Navigation mới
 * Sử dụng PTITAppContainer để quản lý layout và navigation
 */
@Composable
fun PTITCandidateApp(
    navController: NavHostController = rememberNavController()
) {
    val viewModel: CandidateNavGraphViewModel = hiltViewModel()

    // Redirect to the new PTIT App Container for consistent theming
    PTITAppContainer(
        navController = navController,
        hasGradientBackground = false // Let     individual screens handle their backgrounds
    ) {
        CandidateNavGraph(
            navController = navController,
            authRepository = viewModel.authRepository
        )
    }
}