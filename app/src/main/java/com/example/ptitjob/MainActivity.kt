package com.example.ptitjob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.ptitjob.data.repository.AuthRepository
import com.example.ptitjob.ui.component.PTITAppContainer
import com.example.ptitjob.ui.navigation.CandidateNavGraph
import com.example.ptitjob.ui.theme.PtitjobTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PtitjobTheme {
                MainContent()
            }
        }
    }

    @Composable
    private fun MainContent() {
        val navController = rememberNavController()

        PTITAppContainer(navController) {
            CandidateNavGraph(
                navController = navController,
                authRepository = authRepository
            )
        }
    }
}