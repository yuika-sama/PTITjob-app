package com.example.ptitjob.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ptitjob.ui.component.AppTopBar

@Composable
fun CandidateTopBar(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route.orEmpty()

    val (title, showBack, centerAligned, showNotification) = when {
        route.startsWith(CandidateRoutes.Dashboard.route) ->
            Quadruple("PTIT Job", false, false, true)
        route.startsWith(CandidateRoutes.JobsList.route) ||
                route.startsWith(CandidateRoutes.JobSearch.route) ||
                route.startsWith(CandidateRoutes.BestJobs.route) ||
                route.startsWith(CandidateRoutes.AttractiveJobs.route) ->
            Quadruple("Việc làm", false, false, false)
        route.startsWith(CandidateRoutes.JobDetail.route) ->
            Quadruple("Chi tiết việc làm", true, true, false)
        route.startsWith(CandidateRoutes.Companies.route) ||
                route.startsWith(CandidateRoutes.TopCompanies.route) ->
            Quadruple("Công ty", true, false, false)
        route.startsWith(CandidateRoutes.CompanyDetail.route) ->
            Quadruple("Chi tiết công ty", true, true, false)
        route.startsWith(CandidateRoutes.AIServicesMenu.route) ->
            Quadruple("Dịch vụ AI", true, false, false)
        route.startsWith(CandidateRoutes.Profile.route) ->
            Quadruple("Hồ sơ", false, true, false)
        route.startsWith(CandidateRoutes.UtilitiesMenu.route) ->
            Quadruple("Tiện ích", true, false, false)
        else -> Quadruple("PTIT Job", false, false, false)
    }

    AppTopBar(
        title = title,
        showBack = showBack,
        onBack = { navController.navigateUp() },
        centerAligned = centerAligned,
        showNotification = showNotification,
        onNotificationClick = { /* TODO: Navigate to notifications */ }
    )
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)


