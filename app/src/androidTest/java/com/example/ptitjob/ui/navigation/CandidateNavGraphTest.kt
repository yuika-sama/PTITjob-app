package com.example.ptitjob.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class CandidateNavGraphTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun withNavController(content: (TestNavHostController) -> Unit) {
        composeRule.setContent {
            val context = LocalContext.current
            val navController = TestNavHostController(context).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            CandidateNavGraph(navController = navController)
            content(navController)
        }
    }

    @Test
    fun startDestination_isDashboard() {
        withNavController { navController ->
            assert(navController.currentDestination?.route == CandidateRoutes.Dashboard.route)
        }
    }

    @Test
    fun navigate_toJobSearch_changesRoute() {
        withNavController { navController ->
            navController.navigate(CandidateRoutes.JobSearch.route)
            assert(navController.currentDestination?.route == CandidateRoutes.JobSearch.route)
        }
    }

    @Test
    fun navigate_toBestJobs_changesRoute() {
        withNavController { navController ->
            navController.navigate(CandidateRoutes.BestJobs.route)
            assert(navController.currentDestination?.route == CandidateRoutes.BestJobs.route)
        }
    }

    @Test
    fun navigate_toAttractiveJobs_changesRoute() {
        withNavController { navController ->
            navController.navigate(CandidateRoutes.AttractiveJobs.route)
            assert(navController.currentDestination?.route == CandidateRoutes.AttractiveJobs.route)
        }
    }

    @Test
    fun navigate_toCompanies_and_TopCompanies_changesRoute() {
        withNavController { navController ->
            navController.navigate(CandidateRoutes.Companies.route)
            assert(navController.currentDestination?.route == CandidateRoutes.Companies.route)

            navController.navigate(CandidateRoutes.TopCompanies.route)
            assert(navController.currentDestination?.route == CandidateRoutes.TopCompanies.route)
        }
    }

    @Test
    fun navigate_toUtilitiesMenu_changesRoute() {
        withNavController { navController ->
            navController.navigate(CandidateRoutes.UtilitiesMenu.route)
            assert(navController.currentDestination?.route == CandidateRoutes.UtilitiesMenu.route)
        }
    }

    @Test
    fun navigate_toProfile_changesRoute() {
        withNavController { navController ->
            navController.navigate(CandidateRoutes.Profile.route)
            assert(navController.currentDestination?.route == CandidateRoutes.Profile.route)
        }
    }
}


