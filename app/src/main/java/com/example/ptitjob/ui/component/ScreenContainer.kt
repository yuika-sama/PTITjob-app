package com.example.ptitjob.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ptitjob.ui.theme.*

/**
 * Screen container utility cho các màn hình candidate
 * Tự động xử lý safe area padding và background phù hợp với bottom nav
 * Optimized for performance with memoized background creation
 */
@Composable
fun PTITScreenContainer(
    modifier: Modifier = Modifier,
    hasGradientBackground: Boolean = true,
    topBar: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit
) {
    // Memoize background to prevent unnecessary recomposition
    val backgroundBrush = remember(hasGradientBackground) {
        if (hasGradientBackground) {
            Brush.verticalGradient(
                colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                startY = 0f,
                endY = 800f
            )
        } else {
            Brush.verticalGradient(colors = listOf(PTITNeutral50, PTITNeutral50))
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    if (hasGradientBackground) {
                        Brush.verticalGradient(
                            colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                            startY = 0f,
                            endY = 800f
                        )
                    } else {
                        Brush.verticalGradient(colors = listOf(PTITNeutral50, PTITNeutral50))
                    }
                )
        ) {
            content()
        }
    }
}

/**
 * Simple page container cho các màn hình không cần TopBar
 */
@Composable
fun PTITPageContainer(
    modifier: Modifier = Modifier,
    hasGradientBackground: Boolean = true,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    if (hasGradientBackground) {
                        Brush.verticalGradient(
                            colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                            startY = 0f,
                            endY = 800f
                        )
                    } else {
                        Brush.verticalGradient(colors = listOf(PTITNeutral50, PTITNeutral50))
                    }
                )
        ) {
            content()
        }
    }
}

/**
 * Container cho các màn hình có fixed header và scrollable content
 */
@Composable
fun PTITScrollableScreen(
    modifier: Modifier = Modifier,
    hasGradientBackground: Boolean = true,
    topBar: @Composable () -> Unit = {},
    fixedHeader: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit
) {
    PTITScreenContainer(
        modifier = modifier,
        hasGradientBackground = hasGradientBackground,
        topBar = topBar,
        snackbarHostState = snackbarHostState
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            fixedHeader()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}