package com.example.ptitjob.ui.navigation

import androidx.navigation.NavController

/**
 * Extension functions giúp navigate dễ dàng hơn
 * Sử dụng: navController.navigateToJobDetail(jobId = "123")
 */

// Jobs
fun NavController.navigateToJobsList() = navigate(CandidateRoutes.JobsList.route)
fun NavController.navigateToJobSearch() = navigate(CandidateRoutes.JobSearch.route)
fun NavController.navigateToBestJobs() = navigate(CandidateRoutes.BestJobs.route)
fun NavController.navigateToAttractiveJobs() = navigate(CandidateRoutes.AttractiveJobs.route)
fun NavController.navigateToJobDetail(jobId: String) = navigate(CandidateRoutes.JobDetail.createRoute(jobId))

// Companies
fun NavController.navigateToCompanies() = navigate(CandidateRoutes.Companies.route)
fun NavController.navigateToTopCompanies() = navigate(CandidateRoutes.TopCompanies.route)
fun NavController.navigateToCompanyDetail(companyId: String) = navigate(CandidateRoutes.CompanyDetail.createRoute(companyId))

// AI Services
fun NavController.navigateToAIServicesMenu() = navigate(CandidateRoutes.AIServicesMenu.route)
fun NavController.navigateToCVEvaluation() = navigate(CandidateRoutes.CVEvaluation.route)
fun NavController.navigateToInterviewEmulate() = navigate(CandidateRoutes.InterviewEmulate.route)

// Utilities
fun NavController.navigateToUtilitiesMenu() = navigate(CandidateRoutes.UtilitiesMenu.route)
fun NavController.navigateToBHXHCalculator() = navigate(CandidateRoutes.BHXHCalculator.route)
fun NavController.navigateToPersonalIncomeTax() = navigate(CandidateRoutes.PersonalIncomeTax.route)
fun NavController.navigateToSalaryCalculator() = navigate(CandidateRoutes.SalaryCalculator.route)
fun NavController.navigateToUnemploymentInsurance() = navigate(CandidateRoutes.UnemploymentInsurance.route)
fun NavController.navigateToCompoundInterest() = navigate(CandidateRoutes.CompoundInterest.route)

// Profile
fun NavController.navigateToProfile() = navigate(CandidateRoutes.Profile.route)

// Dashboard
fun NavController.navigateToDashboard() = navigate(CandidateRoutes.Dashboard.route)
