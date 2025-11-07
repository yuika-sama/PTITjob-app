package com.example.ptitjob.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ptitjob.ui.navigation.CandidateRoutes
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITElevation
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITNeutral50
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITSecondary
import com.example.ptitjob.ui.theme.PTITSize
import com.example.ptitjob.ui.theme.PTITSpacing
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITTextSecondary
import com.example.ptitjob.ui.theme.PtitjobTheme

/**
 * PTIT Themed Bottom Navigation Bar
 * Modern, animated navigation with gradient backgrounds and smooth transitions
 */
@Composable
fun PTITBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val navItems = listOf(
        BottomNavItem(
            route = CandidateRoutes.Dashboard.route,
            label = "Dashboard",
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard
        ),
        BottomNavItem(
            route = CandidateRoutes.JobsList.route,
            label = "Jobs",
            selectedIcon = Icons.Filled.Work,
            unselectedIcon = Icons.Outlined.Work
        ),
        BottomNavItem(
            route = CandidateRoutes.UtilitiesMenu.route,
            label = "Utilities",
            selectedIcon = Icons.Filled.Build,
            unselectedIcon = Icons.Outlined.Build
        ),
        BottomNavItem(
            route = CandidateRoutes.Companies.route,
            label = "Companies",
            selectedIcon = Icons.Filled.Business,
            unselectedIcon = Icons.Outlined.Business
        ),
        BottomNavItem(
            route = CandidateRoutes.Profile.route,
            label = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shadowElevation = PTITElevation.lg,
        tonalElevation = PTITElevation.md
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PTITNeutral50.copy(alpha = 0.95f),
                            PTITNeutral50
                        )
                    )
                )
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isSelected = isRouteSelected(currentDestination, item.route)
                    
                    PTITNavigationItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PTITNavigationItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val animatedIconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else PTITTextSecondary,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "iconColor"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) PTITPrimary else PTITTextSecondary,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "textColor"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = PTITSpacing.xs, vertical = PTITSpacing.sm)
            .scale(animatedScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
    ) {
        Box(
            modifier = Modifier.size(PTITSize.iconXl),
            contentAlignment = Alignment.Center
        ) {
            // Animated background for selected item
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(PTITSize.iconXl)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PTITPrimary, PTITSecondary),
                                radius = 60f
                            )
                        )
                )
            }

            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = animatedIconColor,
                modifier = Modifier.size(PTITSize.iconLg)
            )
        }

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = animatedTextColor
            ),
            maxLines = 1
        )
    }
}

/**
 * Helper function to determine if a route is currently selected
 * Handles nested routes and special cases
 */
private fun isRouteSelected(currentRoute: String?, targetRoute: String): Boolean {
    if (currentRoute == null) return false
    
    return when (targetRoute) {
        CandidateRoutes.Dashboard.route -> currentRoute == CandidateRoutes.Dashboard.route
        CandidateRoutes.JobsList.route -> currentRoute.startsWith("jobs") || 
                                         currentRoute == CandidateRoutes.JobsList.route ||
                                         currentRoute == CandidateRoutes.JobSearch.route ||
                                         currentRoute == CandidateRoutes.BestJobs.route ||
                                         currentRoute == CandidateRoutes.AttractiveJobs.route ||
                                         currentRoute.startsWith("job_detail")
        CandidateRoutes.UtilitiesMenu.route -> currentRoute.startsWith("utilities") ||
                                              currentRoute == CandidateRoutes.UtilitiesMenu.route ||
                                              currentRoute == CandidateRoutes.BHXHCalculator.route ||
                                              currentRoute == CandidateRoutes.PersonalIncomeTax.route ||
                                              currentRoute == CandidateRoutes.SalaryCalculator.route ||
                                              currentRoute == CandidateRoutes.UnemploymentInsurance.route ||
                                              currentRoute == CandidateRoutes.CompoundInterest.route ||
                                              currentRoute == CandidateRoutes.CareerFair3D.route ||
                                              currentRoute == CandidateRoutes.CVEvaluation.route ||
                                              currentRoute == CandidateRoutes.InterviewEmulate.route
        CandidateRoutes.Companies.route -> currentRoute.startsWith("compan") ||
                                         currentRoute == CandidateRoutes.Companies.route ||
                                         currentRoute == CandidateRoutes.TopCompanies.route ||
                                         currentRoute.startsWith("company_detail")
        CandidateRoutes.Profile.route -> currentRoute == CandidateRoutes.Profile.route
        else -> currentRoute == targetRoute
    }
}

/**
 * Data class for bottom navigation items
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Preview(showBackground = true)
@Composable
private fun PTITBottomNavigationPreview() {
    PtitjobTheme {
        // Note: This preview won't show animations since we don't have a real NavController
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PTITNeutral50
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Preview with one selected item
                listOf(
                    Triple("Dashboard", Icons.Filled.Dashboard, true),
                    Triple("Jobs", Icons.Outlined.Work, false),
                    Triple("Utilities", Icons.Outlined.Build, false),
                    Triple("Companies", Icons.Outlined.Business, false),
                    Triple("Profile", Icons.Outlined.Person, false)
                ).forEach { (label, icon, isSelected) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                    ) {
                        Box(
                            modifier = Modifier.size(PTITSize.iconXl),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(PTITSize.iconXl)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(PTITPrimary, PTITSecondary),
                                                radius = 60f
                                            )
                                        )
                                )
                            }

                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color.White else PTITTextSecondary,
                                modifier = Modifier.size(PTITSize.iconLg)
                            )
                        }

                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PTITPrimary else PTITTextSecondary
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}