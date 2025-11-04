package com.example.ptitjob.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * Main Container Screen cho Candidate
 * Chứa Scaffold với Bottom Navigation và NavHost
 */
@Composable
fun CandidateMainScreen(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CandidateTopBar(navController = navController)
        },
        bottomBar = {
            CandidateBottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CandidateNavGraph(navController = navController)
        }
    }
}
