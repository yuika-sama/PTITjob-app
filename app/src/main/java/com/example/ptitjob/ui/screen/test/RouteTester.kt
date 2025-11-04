package com.example.ptitjob.ui.screen.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.navigation.AuthRoutes
import com.example.ptitjob.ui.navigation.CandidateRoutes

data class RouteItem(val label: String, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteTesterScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val routesGroups: List<Pair<String, List<RouteItem>>> = listOf(
        "Authentication" to listOf(
            RouteItem("Login", AuthRoutes.Login),
            RouteItem("Sign Up", AuthRoutes.SignUp),
            RouteItem("Forgot Password", AuthRoutes.ForgotPassword)
        ),
        "Dashboard" to listOf(
            RouteItem("Dashboard", CandidateRoutes.Dashboard.route)
        ),
        "Jobs" to listOf(
            RouteItem("Jobs List", CandidateRoutes.JobsList.route),
            RouteItem("Job Search", CandidateRoutes.JobSearch.route),
            RouteItem("Best Jobs", CandidateRoutes.BestJobs.route),
            RouteItem("Attractive Jobs", CandidateRoutes.AttractiveJobs.route),
            RouteItem("Job Detail (sample id)", CandidateRoutes.JobDetail.createRoute("sample-job"))
        ),
        "Companies" to listOf(
            RouteItem("Companies", CandidateRoutes.Companies.route),
            RouteItem("Top Companies", CandidateRoutes.TopCompanies.route),
            RouteItem("Company Detail (sample id)", CandidateRoutes.CompanyDetail.createRoute("sample-company"))
        ),
        "AI Services" to listOf(
            RouteItem("AI Services Menu", CandidateRoutes.AIServicesMenu.route),
            RouteItem("CV Evaluation", CandidateRoutes.CVEvaluation.route),
            RouteItem("Interview Emulate", CandidateRoutes.InterviewEmulate.route)
        ),
        "Utilities" to listOf(
            RouteItem("Utilities Menu", CandidateRoutes.UtilitiesMenu.route),
            RouteItem("BHXH Calculator", CandidateRoutes.BHXHCalculator.route),
            RouteItem("Personal Income Tax", CandidateRoutes.PersonalIncomeTax.route),
            RouteItem("Salary Calculator", CandidateRoutes.SalaryCalculator.route),
            RouteItem("Unemployment Insurance", CandidateRoutes.UnemploymentInsurance.route),
            RouteItem("Compound Interest", CandidateRoutes.CompoundInterest.route)
        ),
        "Profile" to listOf(
            RouteItem("Profile", CandidateRoutes.Profile.route)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Tester", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(routesGroups) { (group, items) ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = group,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    items.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { onNavigate(item.route) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors()
                            ) {
                                Text(item.label)
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }
}


