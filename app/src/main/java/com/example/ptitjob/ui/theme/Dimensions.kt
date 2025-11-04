package com.example.ptitjob.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design System for PTIT Job App
 * Spacing, Corners, Elevations, Sizes, Dimensions, Durations
 */

// ========== SPACING SYSTEM ==========
object PTITSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val xxxxl = 40.dp
    val xxxxxl = 48.dp
    val xxxxxxl = 64.dp
}

// ========== CORNER RADIUS SYSTEM ==========
object PTITCornerRadius {
    val xs = RoundedCornerShape(4.dp)
    val sm = RoundedCornerShape(8.dp)
    val md = RoundedCornerShape(12.dp)
    val lg = RoundedCornerShape(16.dp)
    val xl = RoundedCornerShape(20.dp)
    val xxl = RoundedCornerShape(24.dp)
    val xxxl = RoundedCornerShape(28.dp)
    // 50% -> pill / full
    val full = RoundedCornerShape(50)
}

// ========== ELEVATION RAW TOKENS ==========
object PTITElevation {
    val none = 0.dp
    val xs = 1.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 6.dp
    val xl = 8.dp
    val xxl = 12.dp
    val xxxl = 16.dp
    val xxxxl = 24.dp
}

// ========== SIZE SYSTEM ==========
object PTITSize {
    // Icon sizes
    val iconXs = 12.dp
    val iconSm = 16.dp
    val iconMd = 20.dp
    val iconLg = 24.dp
    val iconXl = 32.dp
    val iconXxl = 40.dp
    val iconXxxl = 48.dp

    // Avatar sizes
    val avatarSm = 32.dp
    val avatarMd = 40.dp
    val avatarLg = 48.dp
    val avatarXl = 64.dp
    val avatarXxl = 80.dp
    val avatarXxxl = 120.dp

    // Button heights
    val buttonSm = 32.dp
    val buttonMd = 40.dp
    val buttonLg = 48.dp
    val buttonXl = 56.dp

    // Input field heights
    val inputSm = 32.dp
    val inputMd = 40.dp
    val inputLg = 48.dp
    val inputXl = 56.dp

    // Card minimum heights
    val cardSm = 80.dp
    val cardMd = 120.dp
    val cardLg = 160.dp
    val cardXl = 200.dp
}

// ========== CUSTOM COMPONENT DIMENSIONS ==========
object PTITDimensions {
    // Header heights
    val topBarHeight = 56.dp
    val navigationBarHeight = 80.dp

    // List item heights
    val listItemSm = 48.dp
    val listItemMd = 56.dp
    val listItemLg = 64.dp
    val listItemXl = 72.dp

    // Card dimensions
    val cardMinWidth = 280.dp
    val cardMaxWidth = 400.dp
    val jobCardHeight = 180.dp
    val companyCardHeight = 160.dp

    // Layout constraints
    val maxContentWidth = 1200.dp
    val sidebarWidth = 280.dp
    val sidebarWidthExpanded = 320.dp

    // Bottom sheet
    val bottomSheetPeekHeight = 128.dp
    val bottomSheetMaxHeight = 0.9f // 90% of screen height

    // Dialog dimensions
    val dialogMinWidth = 280.dp
    val dialogMaxWidth = 560.dp
    val dialogMaxHeight = 0.8f // 80% of screen height

    // Image dimensions
    val companyLogoSm = 32.dp
    val companyLogoMd = 48.dp
    val companyLogoLg = 64.dp
    val companyLogoXl = 80.dp
}

// ========== ANIMATION DURATIONS ==========
object PTITAnimationDuration {
    const val fast = 150
    const val normal = 300
    const val slow = 500
    const val extraSlow = 800
}

// ========== DIMENSIONS (CompositionLocal) ==========
data class ComponentDimensions(
    // Spacing
    val spacingXs: Dp = PTITSpacing.xs,
    val spacingSm: Dp = PTITSpacing.sm,
    val spacingMd: Dp = PTITSpacing.md,
    val spacingLg: Dp = PTITSpacing.lg,
    val spacingXl: Dp = PTITSpacing.xl,
    val spacingXxl: Dp = PTITSpacing.xxl,
    val spacingXxxl: Dp = PTITSpacing.xxxl,

    // Corner radius
    val cornerRadiusXs: RoundedCornerShape = PTITCornerRadius.xs,
    val cornerRadiusSm: RoundedCornerShape = PTITCornerRadius.sm,
    val cornerRadiusMd: RoundedCornerShape = PTITCornerRadius.md,
    val cornerRadiusLg: RoundedCornerShape = PTITCornerRadius.lg,
    val cornerRadiusXl: RoundedCornerShape = PTITCornerRadius.xl,
    val cornerRadiusFull: RoundedCornerShape = PTITCornerRadius.full,

    // Elevation (common)
    val elevationNone: Dp = PTITElevation.none,
    val elevationXs: Dp = PTITElevation.xs,
    val elevationSm: Dp = PTITElevation.sm,
    val elevationMd: Dp = PTITElevation.md,
    val elevationLg: Dp = PTITElevation.lg,
    val elevationXl: Dp = PTITElevation.xl,

    // Sizes
    val iconSm: Dp = PTITSize.iconSm,
    val iconMd: Dp = PTITSize.iconMd,
    val iconLg: Dp = PTITSize.iconLg,
    val iconXl: Dp = PTITSize.iconXl,

    val buttonSm: Dp = PTITSize.buttonSm,
    val buttonMd: Dp = PTITSize.buttonMd,
    val buttonLg: Dp = PTITSize.buttonLg,
    val buttonXl: Dp = PTITSize.buttonXl
) {
    // FIX: thêm companion object để dùng extension property `.current`
    companion object
}

val LocalComponentDimensions = staticCompositionLocalOf { ComponentDimensions() }

val ComponentDimensions.Companion.current: ComponentDimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalComponentDimensions.current

// ========== ELEVATIONS (CompositionLocal, chi tiết hơn theo use-case) ==========
data class PTITElevations(
    val level0: Dp = PTITElevation.none,   // flat
    val level1: Dp = PTITElevation.xs,     // hairline
    val level2: Dp = PTITElevation.sm,     // small cards
    val level3: Dp = PTITElevation.md,     // raised cards
    val level4: Dp = PTITElevation.lg,     // top bars, fab hover
    val level5: Dp = PTITElevation.xl,     // dialogs
    val level6: Dp = PTITElevation.xxl,    // modal sheets
    val level7: Dp = PTITElevation.xxxl,   // highest
    val level8: Dp = PTITElevation.xxxxl   // extreme (rare)
) {
    // Cho phép dùng PTITElevations.current
    companion object
}

val LocalPTITElevations = staticCompositionLocalOf { PTITElevations() }

val PTITElevations.Companion.current: PTITElevations
    @Composable
    @ReadOnlyComposable
    get() = LocalPTITElevations.current
