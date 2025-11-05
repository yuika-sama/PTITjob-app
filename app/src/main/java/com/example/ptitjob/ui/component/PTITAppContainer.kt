package com.example.ptitjob.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITNeutral50

/**
 * Full-screen container with PTIT theme background and bottom navigation
 * Handles safe area padding and navigation visibility automatically
 * Optimized for performance with memoized calculations
 */
@Composable
fun PTITAppContainer(
    navController: NavController,
    modifier: Modifier = Modifier,
    hasGradientBackground: Boolean = true,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Memoize navbar visibility calculation to reduce recomposition
    val showBottomNav = remember(currentRoute) {
        shouldShowBottomNav(currentRoute)
    }

    // Memoize background creation
    val backgroundBrush = remember(hasGradientBackground) {
        if (hasGradientBackground) {
            Brush.verticalGradient(
                colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd)
            )
        } else {
            Brush.verticalGradient(colors = listOf(PTITNeutral50, PTITNeutral50))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }

            // Bottom navigation
            if (showBottomNav) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent,
                    shadowElevation = 8.dp,
                    tonalElevation = 4.dp
                ) {
                    PTITBottomNavigation(
                        navController = navController,
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            }
        }
    }
}

/**
 * Simple page container without bottom navigation
 * Use this for auth screens or full-screen experiences
 */
@Composable
fun PTITPageContainer(
    modifier: Modifier = Modifier,
    hasGradientBackground: Boolean = true,
    hasPadding: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (hasGradientBackground) {
                    Brush.verticalGradient(
                        colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd)
                    )
                } else {
                    Brush.verticalGradient(colors = listOf(PTITNeutral50, PTITNeutral50))
                }
            )
            .statusBarsPadding()
            .let { if (hasPadding) it.navigationBarsPadding() else it }
    ) {
        content()
    }
}