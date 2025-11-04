package com.example.ptitjob.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ptitjob.ui.screen.auth.ForgotPasswordRoute
import com.example.ptitjob.ui.screen.auth.LoginRoute
import com.example.ptitjob.ui.screen.auth.SignUpRoute

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    startDestination: String = AuthRoutes.Login,
    onAuthenticated: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AuthRoutes.Login) {
            LoginRoute(
                onNavigateToSignUp = { navController.navigate(AuthRoutes.SignUp) },
                onNavigateToForgot = { navController.navigate(AuthRoutes.ForgotPassword) },
                onAuthenticated = onAuthenticated
            )
        }

        composable(AuthRoutes.SignUp) {
            SignUpRoute(
                onNavigateToLogin = { navController.popBackStack() },
                onRegistrationDone = { navController.navigate(AuthRoutes.Login) { popUpTo(AuthRoutes.Login) { inclusive = false } } }
            )
        }

        composable(AuthRoutes.ForgotPassword) {
            ForgotPasswordRoute(
                onBackToLogin = { navController.popBackStack() }
            )
        }
    }
}


