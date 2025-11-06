package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Comprehensive navigation handler for all Utility Calculators
 * Manages navigation between different calculator screens with proper ViewModel integration
 */

// Navigation Routes Constants
object UtilityRoutes {
    const val UTILITIES_MENU = "utilities_menu"
    const val BHXH_CALCULATOR = "bhxh_calculator"
    const val PERSONAL_INCOME_TAX = "personal_income_tax"
    const val SALARY_CALCULATOR = "salary_calculator"
    const val UNEMPLOYMENT_INSURANCE = "unemployment_insurance"
    const val COMPOUND_INTEREST = "compound_interest"
    const val NOT_FOUND = "not_found"
}

@Composable
fun UtilitiesNavigation(
    navController: NavHostController = rememberNavController(),
    onBack: () -> Unit,
    startDestination: String = UtilityRoutes.UTILITIES_MENU
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Utilities Menu - Main landing page
        composable(UtilityRoutes.UTILITIES_MENU) {
            UtilitiesMenu(
                onNavigateToBHXH = {
                    navController.navigate(UtilityRoutes.BHXH_CALCULATOR)
                },
                onNavigateToPersonalIncomeTax = {
                    navController.navigate(UtilityRoutes.PERSONAL_INCOME_TAX)
                },
                onNavigateToSalaryCalculator = {
                    navController.navigate(UtilityRoutes.SALARY_CALCULATOR)
                },
                onNavigateToUnemploymentInsurance = {
                    navController.navigate(UtilityRoutes.UNEMPLOYMENT_INSURANCE)
                },
                onNavigateToCompoundInterest = {
                    navController.navigate(UtilityRoutes.COMPOUND_INTEREST)
                },
                onBack = onBack
            )
        }

        // BHXH Calculator
        composable(UtilityRoutes.BHXH_CALCULATOR) {
            BHXHCalculatorRoute(
                onBack = { navController.popBackStack() }
            )
        }

        // Personal Income Tax Calculator
        composable(UtilityRoutes.PERSONAL_INCOME_TAX) {
            PersonalIncomeTaxRoute(
                onBack = { navController.popBackStack() }
            )
        }

        // Salary Calculator
        composable(UtilityRoutes.SALARY_CALCULATOR) {
            SalaryCalculatorRoute(
                onBack = { navController.popBackStack() }
            )
        }

        // Unemployment Insurance Calculator
        composable(UtilityRoutes.UNEMPLOYMENT_INSURANCE) {
            UnemploymentInsuranceRoute(
                onBack = { navController.popBackStack() }
            )
        }

        // Compound Interest Calculator
        composable(UtilityRoutes.COMPOUND_INTEREST) {
            CompoundInterestRoute(
                onBack = { navController.popBackStack() }
            )
        }

        // 404 Not Found Page
        composable(UtilityRoutes.NOT_FOUND) {
            NotFound404Screen(
                onBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(UtilityRoutes.UTILITIES_MENU) {
                        popUpTo(UtilityRoutes.UTILITIES_MENU) { inclusive = true }
                    }
                }
            )
        }
    }
}

// Individual Route Components with ViewModel Integration

/**
 * Utility helper functions for navigation analytics and deep linking
 */
object UtilityNavigationHelper {
    
    /**
     * Get calculator type from route
     */
    fun getCalculatorType(route: String): String {
        return when (route) {
            UtilityRoutes.BHXH_CALCULATOR -> "BHXH"
            UtilityRoutes.PERSONAL_INCOME_TAX -> "PersonalIncomeTax"
            UtilityRoutes.SALARY_CALCULATOR -> "SalaryCalculator"
            UtilityRoutes.UNEMPLOYMENT_INSURANCE -> "UnemploymentInsurance"
            UtilityRoutes.COMPOUND_INTEREST -> "CompoundInterest"
            else -> "Unknown"
        }
    }

    /**
     * Get route from calculator type
     */
    fun getRoute(calculatorType: String): String {
        return when (calculatorType.uppercase()) {
            "BHXH" -> UtilityRoutes.BHXH_CALCULATOR
            "PERSONALINCOMETAX" -> UtilityRoutes.PERSONAL_INCOME_TAX
            "SALARYCALCULATOR" -> UtilityRoutes.SALARY_CALCULATOR
            "UNEMPLOYMENTINSURANCE" -> UtilityRoutes.UNEMPLOYMENT_INSURANCE
            "COMPOUNDINTEREST" -> UtilityRoutes.COMPOUND_INTEREST
            else -> UtilityRoutes.NOT_FOUND
        }
    }

    /**
     * Get calculator title for analytics
     */
    fun getCalculatorTitle(route: String): String {
        return when (route) {
            UtilityRoutes.BHXH_CALCULATOR -> "Tính BHXH"
            UtilityRoutes.PERSONAL_INCOME_TAX -> "Thuế thu nhập cá nhân"
            UtilityRoutes.SALARY_CALCULATOR -> "Tính lương NET/GROSS"
            UtilityRoutes.UNEMPLOYMENT_INSURANCE -> "Bảo hiểm thất nghiệp"
            UtilityRoutes.COMPOUND_INTEREST -> "Lãi suất kép"
            else -> "Calculator"
        }
    }

    /**
     * Navigate with analytics tracking
     */
    fun navigateWithTracking(
        navController: NavHostController,
        route: String,
        from: String? = null
    ) {
        // In real app, add analytics tracking here
        val calculatorType = getCalculatorType(route)
        val title = getCalculatorTitle(route)
        
        // Track navigation event
        // AnalyticsHelper.trackCalculatorOpen(calculatorType, from)
        
        navController.navigate(route)
    }

    /**
     * Generate deep link URL for calculator
     */
    fun generateDeepLink(calculatorType: String): String {
        val route = getRoute(calculatorType)
        return "ptitjob://utilities/$route"
    }

    /**
     * Generate share URL for calculator result
     */
    fun generateShareUrl(calculatorType: String, resultId: String): String {
        return "https://ptitjob.app/utilities/$calculatorType/result/$resultId"
    }
}