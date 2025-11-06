package com.example.ptitjob.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.example.ptitjob.ui.viewmodel.ApplicationViewModel

/**
 * Navigation Helper
 * Provides high-level navigation functions with proper state management
 */
class NavigationHelper(
    private val navController: NavController,
    private val applicationViewModel: ApplicationViewModel
) {

    /**
     * Navigate to job detail with proper state management
     */
    fun navigateToJobDetail(
        jobId: String,
        builder: NavOptionsBuilder.() -> Unit = {}
    ) {
        applicationViewModel.setCurrentJob(jobId)
        applicationViewModel.updateNavigationHistory(CandidateRoutes.JobDetail.createRoute(jobId))
        navController.navigate(CandidateRoutes.JobDetail.createRoute(jobId), builder)
    }

    /**
     * Navigate to company detail with proper state management
     */
    fun navigateToCompanyDetail(
        companyId: String,
        builder: NavOptionsBuilder.() -> Unit = {}
    ) {
        applicationViewModel.setCurrentCompany(companyId)
        applicationViewModel.updateNavigationHistory(CandidateRoutes.CompanyDetail.createRoute(companyId))
        navController.navigate(CandidateRoutes.CompanyDetail.createRoute(companyId), builder)
    }

    /**
     * Navigate to job search with search payload
     */
    fun navigateToJobSearchWithPayload(
        payload: DashboardSearchPayload?,
        builder: NavOptionsBuilder.() -> Unit = {
            launchSingleTop = true
            restoreState = true
        }
    ) {
        payload?.let {
            applicationViewModel.navigateWithSearch(it)
        }
        applicationViewModel.updateNavigationHistory(CandidateRoutes.JobSearch.route)
        navController.navigate(CandidateRoutes.JobSearch.route, builder)
    }

    /**
     * Navigate to companies screen
     */
    fun navigateToCompanies(
        builder: NavOptionsBuilder.() -> Unit = {
            launchSingleTop = true
            restoreState = true
        }
    ) {
        applicationViewModel.updateNavigationHistory(CandidateRoutes.Companies.route)
        navController.navigate(CandidateRoutes.Companies.route, builder)
    }

    /**
     * Navigate to AI services
     */
    fun navigateToAIServices(
        builder: NavOptionsBuilder.() -> Unit = {
            launchSingleTop = true
            restoreState = true
        }
    ) {
        applicationViewModel.updateNavigationHistory(CandidateRoutes.AIServicesMenu.route)
        navController.navigate(CandidateRoutes.AIServicesMenu.route, builder)
    }

    /**
     * Navigate to CV evaluation
     */
    fun navigateToCVEvaluation(
        builder: NavOptionsBuilder.() -> Unit = {
            launchSingleTop = true
            restoreState = true
        }
    ) {
        applicationViewModel.updateNavigationHistory(CandidateRoutes.CVEvaluation.route)
        navController.navigate(CandidateRoutes.CVEvaluation.route, builder)
    }

    /**
     * Navigate to interview emulation
     */
    fun navigateToInterviewEmulate(
        builder: NavOptionsBuilder.() -> Unit = {
            launchSingleTop = true
            restoreState = true
        }
    ) {
        applicationViewModel.updateNavigationHistory(CandidateRoutes.InterviewEmulate.route)
        navController.navigate(CandidateRoutes.InterviewEmulate.route, builder)
    }

    /**
     * Navigate to profile
     */
    fun navigateToProfile(
        builder: NavOptionsBuilder.() -> Unit = {
            launchSingleTop = true
            restoreState = true
        }
    ) {
        applicationViewModel.updateNavigationHistory(CandidateRoutes.Profile.route)
        navController.navigate(CandidateRoutes.Profile.route, builder)
    }

    /**
     * Navigate to utilities/calculators
     */
    fun navigateToUtilities(
        builder: NavOptionsBuilder.() -> Unit = {
            launchSingleTop = true
            restoreState = true
        }
    ) {
        applicationViewModel.updateNavigationHistory(CandidateRoutes.UtilitiesMenu.route)
        navController.navigate(CandidateRoutes.UtilitiesMenu.route, builder)
    }

    /**
     * Navigate to specific calculator
     */
    fun navigateToCalculator(
        calculatorType: CalculatorType,
        builder: NavOptionsBuilder.() -> Unit = {
            launchSingleTop = true
        }
    ) {
        val route = when (calculatorType) {
            CalculatorType.SALARY -> CandidateRoutes.SalaryCalculator.route
            CalculatorType.BHXH -> CandidateRoutes.BHXHCalculator.route
            CalculatorType.PERSONAL_TAX -> CandidateRoutes.PersonalIncomeTax.route
            CalculatorType.COMPOUND_INTEREST -> CandidateRoutes.CompoundInterest.route
            CalculatorType.UNEMPLOYMENT -> CandidateRoutes.UnemploymentInsurance.route
        }
        
        applicationViewModel.updateNavigationHistory(route)
        navController.navigate(route, builder)
    }

    /**
     * Navigate back with state cleanup
     */
    fun navigateBack(): Boolean {
        applicationViewModel.clearSearchPayload()
        return navController.popBackStack()
    }

    /**
     * Navigate to dashboard (home)
     */
    fun navigateToDashboard(
        clearStack: Boolean = false,
        builder: NavOptionsBuilder.() -> Unit = {}
    ) {
        applicationViewModel.updateNavigationHistory(CandidateRoutes.Dashboard.route)
        
        navController.navigate(CandidateRoutes.Dashboard.route) {
            if (clearStack) {
                popUpTo(CandidateRoutes.Dashboard.route) {
                    inclusive = true
                }
            }
            launchSingleTop = true
            restoreState = true
            builder()
        }
    }

    /**
     * Apply to job with navigation handling
     */
    fun applyToJob(jobId: String, onResult: (Boolean, String?) -> Unit) {
        applicationViewModel.applyToJob(jobId) { success, message ->
            onResult(success, message)
            if (success) {
                // Optionally navigate to application confirmation or profile
                // navigateToProfile()
            }
        }
    }

    /**
     * Toggle job favorite status
     */
    fun toggleJobFavorite(jobId: String) {
        if (applicationViewModel.isJobFavorited(jobId)) {
            applicationViewModel.removeFromFavorites(jobId)
        } else {
            applicationViewModel.addToFavorites(jobId)
        }
    }

    /**
     * Get current route
     */
    fun getCurrentRoute(): String? {
        return navController.currentDestination?.route
    }

    /**
     * Check if can navigate back
     */
    fun canNavigateBack(): Boolean {
        return navController.previousBackStackEntry != null
    }
}

/**
 * Calculator types enum
 */
enum class CalculatorType {
    SALARY,
    BHXH,
    PERSONAL_TAX,
    COMPOUND_INTEREST,
    UNEMPLOYMENT
}

/**
 * Extension function to create NavigationHelper
 */
fun NavController.createNavigationHelper(applicationViewModel: ApplicationViewModel): NavigationHelper {
    return NavigationHelper(this, applicationViewModel)
}