package com.example.ptitjob.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.example.ptitjob.ui.screen.candidate.profile.UserProfile
import com.example.ptitjob.ui.screen.candidate.utilities.BHXHCalculatorScreen
import com.example.ptitjob.ui.screen.candidate.utilities.CompoundInterestScreen
import com.example.ptitjob.ui.screen.candidate.utilities.PersonalIncomeTaxScreen
import com.example.ptitjob.ui.screen.candidate.utilities.SalaryCalculatorScreen
import com.example.ptitjob.ui.screen.candidate.utilities.UnemploymentInsuranceScreen
import com.example.ptitjob.ui.screen.candidate.utilities.UtilitiesMenu
import com.example.ptitjob.data.model.Company
import com.example.ptitjob.ui.screen.test.RouteTesterScreen
import com.example.ptitjob.ui.navigation.DashboardSearchPayload
import com.example.ptitjob.ui.screen.candidate.utilities.SalaryCalculatorRoute
import com.example.ptitjob.ui.screen.candidate.utilities.UtilitiesViewModel
import com.example.ptitjob.ui.screen.test.NavbarDemoScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Navigation Graph cho Candidate
 * Quản lý tất cả routes và navigation cho ứng viên với authentication check
 */
@Composable
fun CandidateNavGraph(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    // Kiểm tra trạng thái đăng nhập
    var isAuthenticated by remember { mutableStateOf<Boolean?>(null) }
    
    LaunchedEffect(Unit) {
        // Kiểm tra token có tồn tại và hợp lệ không
        authRepository.getCurrentUser().fold(
            onSuccess = { user ->
                isAuthenticated = user != null && user.data?.id!!.isNotBlank()
            },
            onFailure = {
                isAuthenticated = false
            }
        )
    }
    
    // Xác định start destination dựa trên trạng thái đăng nhập
    val startDestination = when (isAuthenticated) {
        true -> CandidateRoutes.Dashboard.route
        false -> AuthRoutes.Login
        null -> return // Đang loading, không render gì
    }

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
                    // Khi đăng nhập thành công, chuyển đến dashboard và xóa auth stack
                    navController.navigate(CandidateRoutes.Dashboard.route) {
                        popUpTo(0) { inclusive = true } // Clear toàn bộ back stack
                        launchSingleTop = true
                    }
                    isAuthenticated = true
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
        ) { _ ->
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
                    onBack = { navController.navigateUp() }
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
                        // Khi logout, chuyển về login và clear back stack
                        isAuthenticated = false
                        navController.navigate(AuthRoutes.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // ===== DEV/TEST ROUTES =====
        composable(CandidateRoutes.RouteTester.route) {
            ProtectedRoute(isAuthenticated, navController) {
                RouteTesterScreen(
                    onNavigate = { route ->
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onBack = { navController.navigateUp() }
                )
            }
        }

        composable(CandidateRoutes.NavbarDemo.route) {
            ProtectedRoute(isAuthenticated, navController) {
                NavbarDemoScreen(
                    onBack = { navController.navigateUp() }
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
            }
        }
    }
    
    if (isAuthenticated == true) {
        content()
    }
}

private const val DASHBOARD_JOB_SEARCH_PAYLOAD = "dashboard_job_search_payload"