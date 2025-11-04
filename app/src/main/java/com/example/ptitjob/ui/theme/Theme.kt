package com.example.ptitjob.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class CustomColorScheme(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color
)

private val LightCustomColors = CustomColorScheme(
    success = PTITSuccess,
    onSuccess = PTITOnSuccess,
    warning = PTITWarning,
    onWarning = PTITOnWarning
)

private val DarkCustomColors = CustomColorScheme(
    success = PTITSuccessDark,
    onSuccess = PTITOnSuccessDark,
    warning = PTITWarningDark,
    onWarning = PTITOnWarningDark
)

internal val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }

private val LightColorScheme = lightColorScheme(
    primary = PTITPrimary,
    onPrimary = PTITOnPrimary,
    primaryContainer = PTITPrimaryLight,
    onPrimaryContainer = PTITTextPrimary,
    secondary = PTITSecondary,
    onSecondary = PTITOnSecondary,
    secondaryContainer = PTITSecondaryLight,
    tertiary = PTITInfo,
    background = PTITBackgroundLight,
    onBackground = PTITTextPrimary,
    surface = PTITSurfaceLight,
    onSurface = PTITTextPrimary,
    surfaceVariant = PTITSurfaceVariant,
    onSurfaceVariant = PTITTextSecondary,
    error = PTITError,
    onError = PTITOnError,
    outline = PTITGray300,
    outlineVariant = PTITGray200
)

private val DarkColorScheme = darkColorScheme(
    primary = PTITPrimaryLight,
    onPrimary = PTITOnPrimary,
    primaryContainer = PTITPrimaryDark,
    onPrimaryContainer = PTITTextLight,
    secondary = PTITSecondaryLight,
    onSecondary = PTITOnSecondary,
    secondaryContainer = PTITSecondaryDark,
    tertiary = PTITInfo,
    background = PTITBackgroundDark,
    onBackground = PTITTextLight,
    surface = PTITSurfaceDark,
    onSurface = PTITTextLight,
    surfaceVariant = PTITGray800,
    onSurfaceVariant = PTITGray400,
    error = PTITError,
    onError = PTITOnError,
    outline = PTITGray600,
    outlineVariant = PTITGray700
)

private fun brandizeDynamic(
    base: androidx.compose.material3.ColorScheme,
    darkTheme: Boolean
): androidx.compose.material3.ColorScheme {
    return base.copy(
        primary = if (darkTheme) PTITPrimaryLight else PTITPrimary,
        onPrimary = PTITOnPrimary,
        primaryContainer = if (darkTheme) PTITPrimaryDark else PTITPrimaryLight,
        secondary = PTITSecondary,
        tertiary = PTITInfo
    )
}

@Composable
fun PtitjobTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val canDynamic = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val baseScheme = when {
        canDynamic && darkTheme -> dynamicDarkColorScheme(context)
        canDynamic && !darkTheme -> dynamicLightColorScheme(context)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val colorScheme = if (canDynamic) brandizeDynamic(baseScheme, darkTheme) else baseScheme

    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        SideEffect {
            window.statusBarColor = colorScheme.primary.toArgb()
            val isLightIcons = colorScheme.primary.luminance() < 0.5f
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isLightIcons

            window.navigationBarColor = colorScheme.surface.toArgb()
            val isLightNavIcons = colorScheme.surface.luminance() >= 0.5f
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isLightNavIcons
        }
    }

    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        // Hàm MaterialTheme như cũ
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

val MaterialTheme.customColors: CustomColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current