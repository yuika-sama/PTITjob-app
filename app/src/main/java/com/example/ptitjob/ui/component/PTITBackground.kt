package com.example.ptitjob.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.ptitjob.ui.theme.*

/**
 * Performance-optimized background component
 * Reduces recomposition by memoizing gradient creation
 */
@Composable
fun PTITBackground(
    modifier: Modifier = Modifier,
    hasGradient: Boolean = true,
    content: @Composable () -> Unit
) {
    val backgroundBrush = remember(hasGradient) {
        if (hasGradient) {
            Brush.verticalGradient(
                colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd)
            )
        } else {
            Brush.verticalGradient(colors = listOf(PTITNeutral50, PTITNeutral50))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        content()
    }
}

/**
 * Lightweight container for navbar-aware layouts
 * Optimized for minimal recomposition
 */
@Composable
fun PTITNavAwareContainer(
    modifier: Modifier = Modifier,
    hasGradient: Boolean = true,
    content: @Composable () -> Unit
) {
    PTITBackground(
        modifier = modifier,
        hasGradient = hasGradient
    ) {
        content()
    }
}