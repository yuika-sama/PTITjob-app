package com.example.ptitjob.ui.navigation

/**
 * Sealed class định nghĩa tất cả routes cho Candidate
 * Dựa theo React Router structure nhưng tối ưu cho Compose Navigation
 */
sealed class CandidateRoutes(val route: String) {
    // Main sections
    object Dashboard : CandidateRoutes("candidate/dashboard")
    
    // Jobs section
    object JobsList : CandidateRoutes("candidate/jobs")
    object JobSearch : CandidateRoutes("candidate/job-search")
    object BestJobs : CandidateRoutes("candidate/best-jobs")
    object AttractiveJobs : CandidateRoutes("candidate/attractive-jobs")
    object JobDetail : CandidateRoutes("candidate/job/{jobId}") {
        fun createRoute(jobId: String) = "candidate/job/$jobId"
    }
    
    // Companies section
    object Companies : CandidateRoutes("candidate/companies")
    object TopCompanies : CandidateRoutes("candidate/top-companies")
    object CompanyDetail : CandidateRoutes("candidate/company/{companyId}") {
        fun createRoute(companyId: String) = "candidate/company/$companyId"
    }
    
    // AI Services section
    object AIServicesMenu : CandidateRoutes("candidate/ai-services")
    object CVEvaluation : CandidateRoutes("candidate/cv-evaluation")
    object InterviewEmulate : CandidateRoutes("candidate/interview-emulate")
    
    // Utilities/Calculators section
    object UtilitiesMenu : CandidateRoutes("candidate/utilities")
    object BHXHCalculator : CandidateRoutes("candidate/bhxh-calculator")
    object PersonalIncomeTax : CandidateRoutes("candidate/personal-income-tax")
    object SalaryCalculator : CandidateRoutes("candidate/salary-calculator")
    object UnemploymentInsurance : CandidateRoutes("candidate/unemployment-insurance")
    object CompoundInterest : CandidateRoutes("candidate/compound-interest")
    
    // Profile
    object Profile : CandidateRoutes("candidate/profile")

    // Dev/Test
    object RouteTester : CandidateRoutes("candidate/route-tester")
}

/**
 * Enum class cho Bottom Navigation Items
 */
enum class CandidateBottomNavItem(
    val route: String,
    val title: String,
    val icon: String // Emoji icons cho PTIT theme
) {
    HOME(
        route = CandidateRoutes.Dashboard.route,
        title = "Trang chủ",
        icon = "🏠"
    ),
    JOBS(
        route = CandidateRoutes.JobsList.route,
        title = "Việc làm",
        icon = "💼"
    ),
    COMPANIES(
        route = CandidateRoutes.Companies.route,
        title = "Công ty",
        icon = "🏢"
    ),
    AI_SERVICES(
        route = CandidateRoutes.AIServicesMenu.route,
        title = "AI",
        icon = "🤖"
    ),
    UTILITIES(
        route = CandidateRoutes.UtilitiesMenu.route,
        title = "Công cụ",
        icon = "🧮"
    )
}
