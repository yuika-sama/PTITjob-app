package com.example.ptitjob.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ptitjob.ui.theme.*

/**
 * Bottom Navigation Bar cho Candidate
 * Hiển thị 5 main sections: Home, Jobs, Companies, AI Services, Profile
 */
@Composable
fun CandidateBottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = PTITElevation.md
    ) {
        CandidateBottomNavItem.entries.forEach { item ->
            val isSelected = currentRoute?.startsWith(item.route) == true
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination to avoid building up a large stack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Text(
                        text = item.icon,
                        fontSize = 24.sp,
                        modifier = Modifier.size(PTITSize.iconMd)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PTITPrimary,
                    selectedTextColor = PTITPrimary,
                    indicatorColor = PTITPrimary.copy(alpha = 0.12f),
                    unselectedIconColor = PTITTextSecondary,
                    unselectedTextColor = PTITTextSecondary
                )
            )
        }
    }
}
