package com.example.ptitjob.ui.navigation

import androidx.compose.runtime.Composable
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
    PTITAppContainer(
        navController = navController,
        hasGradientBackground = false // Let individual screens handle their own backgrounds
    ) {
        CandidateNavGraph(navController = navController)
    }
}