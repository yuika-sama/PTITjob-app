package com.example.ptitjob.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

/**
 * PTIT Themed Scaffold with Bottom Navigation
 * Provides consistent layout structure for all candidate screens
 */
@Composable
fun PTITScaffold(
    navController: NavController,
    modifier: Modifier = Modifier,
    showBottomNav: Boolean = true,
    containerColor: Color = Color.Transparent,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = containerColor,
        bottomBar = {
            if (showBottomNav) {
                PTITBottomNavigation(navController = navController)
            }
        }
    ) { paddingValues ->
        content(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Utility function to determine if bottom navigation should be shown
 * based on the current route
 */
fun shouldShowBottomNav(currentRoute: String?): Boolean {
    if (currentRoute == null) return false
    
    // Hide bottom nav for auth screens
    val authRoutes = listOf("login", "signup", "forgot-password")
    if (authRoutes.any { currentRoute.contains(it, ignoreCase = true) }) {
        return false
    }
    
    // Hide bottom nav for specific detail screens where user needs full focus
    val hideNavRoutes = listOf(
        "route_tester" // Hide for testing screen
    )
    
    return !hideNavRoutes.any { currentRoute.contains(it, ignoreCase = true) }
}