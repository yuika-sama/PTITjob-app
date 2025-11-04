package com.example.ptitjob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ptitjob.ui.theme.PtitjobTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.rememberNavController
import com.example.ptitjob.ui.navigation.CandidateNavGraph
import com.example.ptitjob.ui.navigation.CandidateRoutes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PtitjobTheme {
                val navController = rememberNavController()
                CandidateNavGraph(
                    navController = navController,
                    startDestination = CandidateRoutes.RouteTester.route
                )
            }
        }
    }
}