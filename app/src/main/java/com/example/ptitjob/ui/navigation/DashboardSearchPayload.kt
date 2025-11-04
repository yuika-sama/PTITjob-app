package com.example.ptitjob.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Cross-screen payload for pre-populating the candidate job search screen.
 */
@Parcelize
data class DashboardSearchPayload(
    val keyword: String? = null,
    val categoryId: String? = null,
    val categoryName: String? = null
) : Parcelable
