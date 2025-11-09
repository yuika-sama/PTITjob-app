package com.example.ptitjob.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ptitjob.data.model.Company
import com.example.ptitjob.data.repository.AuthRepository
import com.example.ptitjob.ui.screen.auth.ForgotPasswordRoute
import com.example.ptitjob.ui.screen.auth.LoginRoute
import com.example.ptitjob.ui.screen.auth.SignUpRoute
import com.example.ptitjob.ui.screen.candidate.aiService.AIServicesMenu
import com.example.ptitjob.ui.screen.candidate.aiService.InterviewEmulateRoute
import com.example.ptitjob.ui.screen.candidate.aiService.CvEvaluationRoute
import com.example.ptitjob.ui.screen.candidate.companies.CompaniesScreen
import com.example.ptitjob.ui.screen.candidate.companies.TopCompaniesScreen
import com.example.ptitjob.ui.screen.candidate.companies.companyDetail.CompanyDetailScreen
import com.example.ptitjob.ui.screen.candidate.home.CandidateDashboardRoute
import com.example.ptitjob.ui.screen.candidate.home.QuickToolType
import com.example.ptitjob.ui.screen.candidate.jobs.AttractiveJobsRoute
import com.example.ptitjob.ui.screen.candidate.jobs.BestJobsRoute
import com.example.ptitjob.ui.screen.candidate.jobs.CandidateJobListRoute
import com.example.ptitjob.ui.screen.candidate.jobs.JobSearchRoute
import com.example.ptitjob.ui.screen.candidate.jobs.jobDetail.JobDetailsRoute
import com.example.ptitjob.ui.screen.candidate.profile.ProfileRoute
import com.example.ptitjob.ui.screen.candidate.experience3d.CareerFair3DScreen
import com.example.ptitjob.ui.screen.candidate.utilities.BHXHCalculatorScreen
import com.example.ptitjob.ui.screen.candidate.utilities.CompoundInterestScreen
import com.example.ptitjob.ui.screen.candidate.utilities.PersonalIncomeTaxScreen
import com.example.ptitjob.ui.screen.candidate.utilities.SalaryCalculatorScreen
import com.example.ptitjob.ui.screen.candidate.utilities.UnemploymentInsuranceScreen
import com.example.ptitjob.ui.screen.candidate.utilities.UtilitiesMenu
import com.example.ptitjob.ui.screen.candidate.utilities.UtilitiesViewModel
import com.example.ptitjob.ui.viewmodel.ApplicationViewModel

/**
 * Navigation Graph cho Candidate
 * Quản lý tất cả routes và navigation cho ứng viên với authentication check
 */
@Composable
fun CandidateNavGraph(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    // Inject ApplicationViewModel to manage app-wide state
    val applicationViewModel: ApplicationViewModel = hiltViewModel()
    val appState by applicationViewModel.appState.collectAsStateWithLifecycle()
    
    // Use app state instead of local state for authentication
    val isAuthenticated = appState.isAuthenticated
    
    // Initialize app state on startup if needed
    LaunchedEffect(Unit) {
        // Only load if we don't have user data yet
        if (appState.currentUser == null) {
            applicationViewModel.loadUserProfile()
        }
    }
    
    // Xác định start destination dựa trên trạng thái đăng nhập
    val startDestination = when (isAuthenticated) {
        true -> CandidateRoutes.Dashboard.route
        false -> AuthRoutes.Login
        else -> AuthRoutes.Login // Default to login if unknown state
    }

    // Create navigation helper with ApplicationViewModel
    val navigationHelper = navController.createNavigationHelper(applicationViewModel)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ===== AUTH SCREENS =====
        composable(AuthRoutes.Login) {
            LoginRoute(
                onNavigateToSignUp = {
                    navController.navigate(AuthRoutes.SignUp) { launchSingleTop = true }
                },
                onNavigateToForgot = {
                    navController.navigate(AuthRoutes.ForgotPassword) { launchSingleTop = true }
                },
                onAuthenticated = {
                    // Khi đăng nhập thành công, set authenticated trước rồi mới load profile
                    // Để tránh bị chuyển về login nếu getCurrentUser thất bại
                    navController.navigate(CandidateRoutes.Dashboard.route) {
                        popUpTo(0) { inclusive = true } // Clear toàn bộ back stack
                        launchSingleTop = true
                    }
                    // Load profile sau khi đã navigate (không blocking)
                    applicationViewModel.loadUserProfile()
                }
            )
        }

        composable(AuthRoutes.SignUp) {
            SignUpRoute(
                onNavigateToLogin = { navController.popBackStack() },
                onRegistrationDone = {
                    navController.navigate(AuthRoutes.Login) {
                        popUpTo(AuthRoutes.Login) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AuthRoutes.ForgotPassword) {
            ForgotPasswordRoute(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // ===== PROTECTED ROUTES (Yêu cầu đăng nhập) =====
        
        // ===== HOME/DASHBOARD =====
        composable(CandidateRoutes.Dashboard.route) {
            // Kiểm tra authentication trước khi vào dashboard
            LaunchedEffect(Unit) {
                if (isAuthenticated != true) {
                    navController.navigate(AuthRoutes.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            
            CandidateDashboardRoute(
                onNavigateToJobSearch = { payload ->
                    val handle = navController.currentBackStackEntry?.savedStateHandle
                    if (payload != null) {
                        handle?.set(DASHBOARD_JOB_SEARCH_PAYLOAD, payload)
                    } else {
                        handle?.remove<DashboardSearchPayload>(DASHBOARD_JOB_SEARCH_PAYLOAD)
                    }
                    navController.navigate(CandidateRoutes.JobSearch.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToJobDetail = { jobId ->
                    navController.navigate(CandidateRoutes.JobDetail.createRoute(jobId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCompanyDetail = { companyId ->
                    navController.navigate(CandidateRoutes.CompanyDetail.createRoute(companyId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCompanies = {
                    navController.navigate(CandidateRoutes.Companies.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToIndustries = {
                    navController.navigate(CandidateRoutes.JobSearch.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToTool = { tool ->
                    when (tool) {
                        QuickToolType.SALARY_CALCULATOR -> navController.navigate(CandidateRoutes.SalaryCalculator.route) {
                            launchSingleTop = true
                        }
                        QuickToolType.SOCIAL_INSURANCE -> navController.navigate(CandidateRoutes.BHXHCalculator.route) {
                            launchSingleTop = true
                        }
                        QuickToolType.PERSONAL_INCOME_TAX -> navController.navigate(CandidateRoutes.PersonalIncomeTax.route) {
                            launchSingleTop = true
                        }
                        QuickToolType.COMPOUND_INTEREST -> navController.navigate(CandidateRoutes.CompoundInterest.route) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // ===== JOBS SECTION =====
        composable(CandidateRoutes.JobsList.route) {
            ProtectedRoute(isAuthenticated, navController) {
                CandidateJobListRoute(
                    onJobSelected = { jobId ->
                        navController.navigate(CandidateRoutes.JobDetail.createRoute(jobId)) {
                            launchSingleTop = true
                        }
                    },
                    onCompanySelected = { companyId ->
                        navController.navigate(CandidateRoutes.CompanyDetail.createRoute(companyId)) {
                            launchSingleTop = true
                        }
                    },
                    onCategorySelected = { category ->
                        val handle = navController.currentBackStackEntry?.savedStateHandle
                        handle?.set(
                            DASHBOARD_JOB_SEARCH_PAYLOAD,
                            DashboardSearchPayload(
                                keyword = null,
                                categoryId = category.backendId,
                                categoryName = category.name
                            )
                        )
                        navController.navigate(CandidateRoutes.JobSearch.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }

        composable(CandidateRoutes.JobSearch.route) {
            ProtectedRoute(isAuthenticated, navController) {
                val previousEntry = navController.previousBackStackEntry
                val savedStateHandle = previousEntry?.savedStateHandle
                val presetPayload = savedStateHandle?.get<DashboardSearchPayload>(DASHBOARD_JOB_SEARCH_PAYLOAD)

                // Clear the dashboard-provided payload once the screen consumes it to avoid stale state.
                LaunchedEffect(presetPayload) {
                    if (presetPayload != null) {
                        savedStateHandle?.remove<DashboardSearchPayload>(DASHBOARD_JOB_SEARCH_PAYLOAD)
                    }
                }

                JobSearchRoute(
                    presetPayload = presetPayload,
                    onBack = { navController.popBackStack() },
                    onNavigateToJob = { jobId ->
                        navController.navigate(CandidateRoutes.JobDetail.createRoute(jobId)) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToCompany = { companyId ->
                        navController.navigate(CandidateRoutes.CompanyDetail.createRoute(companyId)) {
                            launchSingleTop = true
                        }
                    },
                    onApplyToJob = { jobId ->
                        navController.navigate(CandidateRoutes.JobDetail.createRoute(jobId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(CandidateRoutes.BestJobs.route) {
            ProtectedRoute(isAuthenticated, navController) {
                BestJobsRoute(
                    onBack = { navController.popBackStack() },
                    onJobSelected = { jobId: String ->
                        navController.navigate(CandidateRoutes.JobDetail.createRoute(jobId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(CandidateRoutes.AttractiveJobs.route) {
            ProtectedRoute(isAuthenticated, navController) {
                AttractiveJobsRoute(
                    onBack = { navController.popBackStack() },
                    onJobSelected = { jobId: String ->
                        navController.navigate(CandidateRoutes.JobDetail.createRoute(jobId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(
            route = CandidateRoutes.JobDetail.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            ProtectedRoute(isAuthenticated, navController) {
                val currentJobId = backStackEntry.arguments?.getString("jobId")

                JobDetailsRoute(
                    onBack = { navController.popBackStack() },
                    onNavigateToCompany = { companyId ->
                        navController.navigate(CandidateRoutes.CompanyDetail.createRoute(companyId)) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToJob = { jobId ->
                        if (jobId != currentJobId) {
                            navController.navigate(CandidateRoutes.JobDetail.createRoute(jobId)) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        // ===== COMPANIES SECTION =====
        composable(CandidateRoutes.Companies.route) {
            ProtectedRoute(isAuthenticated, navController) {
                CompaniesScreen()
            }
        }

        composable(CandidateRoutes.TopCompanies.route) {
            ProtectedRoute(isAuthenticated, navController) {
                TopCompaniesScreen()
            }
        }

        composable(
            route = CandidateRoutes.CompanyDetail.route,
            arguments = listOf(navArgument("companyId") { type = NavType.StringType })
        ) { backStackEntry ->
            ProtectedRoute(isAuthenticated, navController) {
                CompanyDetailScreen(
                    company = Company(
                        id = "",
                        name = "",
                        logo = "",
                        industry = "",
                        size = null,
                        address = null,
                        jobCount = 0,
                        jobs = emptyList()
                    ),
                    onBack = { navController.navigateUp() }
                )
            }
        }

        // ===== AI SERVICES SECTION =====
        composable(CandidateRoutes.AIServicesMenu.route) {
            ProtectedRoute(isAuthenticated, navController) {
                AIServicesMenu(
                    onNavigateToCVEvaluation = {
                        navController.navigate(CandidateRoutes.CVEvaluation.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToInterviewEmulate = {
                        navController.navigate(CandidateRoutes.InterviewEmulate.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onBack = { navController.navigateUp() }
                )
            }
        }

        composable(CandidateRoutes.CVEvaluation.route) {
            ProtectedRoute(isAuthenticated, navController) {
                CvEvaluationRoute(onBack = { navController.popBackStack() })
            }
        }

        composable(CandidateRoutes.InterviewEmulate.route) {
            ProtectedRoute(isAuthenticated, navController) {
                InterviewEmulateRoute(onBack = { navController.popBackStack() })
            }
        }

        // ===== UTILITIES/CALCULATORS SECTION =====
        composable(CandidateRoutes.UtilitiesMenu.route) {
            ProtectedRoute(isAuthenticated, navController) {
                UtilitiesMenu(
                    onNavigateToBHXH = {
                        navController.navigate(CandidateRoutes.BHXHCalculator.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToPersonalIncomeTax = {
                        navController.navigate(CandidateRoutes.PersonalIncomeTax.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSalaryCalculator = {
                        navController.navigate(CandidateRoutes.SalaryCalculator.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToUnemploymentInsurance = {
                        navController.navigate(CandidateRoutes.UnemploymentInsurance.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToCompoundInterest = {
                        navController.navigate(CandidateRoutes.CompoundInterest.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToCareerFair3D = {
                        navController.navigate(CandidateRoutes.CareerFair3D.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToCVEvaluation = {
                        navController.navigate(CandidateRoutes.CVEvaluation.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToInterviewEmulate = {
                        navController.navigate(CandidateRoutes.InterviewEmulate.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onBack = { navController.navigateUp() }
                )
            }
        }

        composable(CandidateRoutes.CareerFair3D.route) {
            ProtectedRoute(isAuthenticated, navController) {
                CareerFair3DScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(CandidateRoutes.BHXHCalculator.route) {
            ProtectedRoute(isAuthenticated, navController) {
                BHXHCalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(CandidateRoutes.PersonalIncomeTax.route) {
            ProtectedRoute(isAuthenticated, navController) {
                PersonalIncomeTaxScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(CandidateRoutes.SalaryCalculator.route) {
            ProtectedRoute(isAuthenticated, navController) {
                val viewModel: UtilitiesViewModel = hiltViewModel()
                val uiState by viewModel.salaryState.collectAsStateWithLifecycle()
                SalaryCalculatorScreen(
                    uiState = uiState,
                    onInputChange = viewModel::updateSalaryInput,
                    onCalculate = viewModel::calculateSalary,
                    onClearResult = viewModel::clearSalaryResult,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(CandidateRoutes.UnemploymentInsurance.route) {
            ProtectedRoute(isAuthenticated, navController) {
                UnemploymentInsuranceScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(CandidateRoutes.CompoundInterest.route) {
            ProtectedRoute(isAuthenticated, navController) {
                CompoundInterestScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // ===== PROFILE =====
        composable(CandidateRoutes.Profile.route) {
            ProtectedRoute(isAuthenticated, navController) {
                ProfileRoute(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        // Trigger logout via ApplicationViewModel to clear all state
                        applicationViewModel.logout()
                        navController.navigate(AuthRoutes.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

/**
 * Wrapper composable để bảo vệ các route cần authentication
 */
@Composable
private fun ProtectedRoute(
    isAuthenticated: Boolean?,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated == false) {
            navController.navigate(AuthRoutes.Login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    
    // Only render content if authenticated
    if (isAuthenticated == true) {
        content()
    }
}

private const val DASHBOARD_JOB_SEARCH_PAYLOAD = "dashboard_job_search_payload"