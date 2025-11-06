package com.example.ptitjob.ui.screen.candidate.utilities.calculations

import com.example.ptitjob.ui.screen.candidate.utilities.models.*

// Local constants/helpers for unemployment calculation (kept here to avoid editing shared models)
private object UnemploymentConstants {
    const val BENEFIT_RATE = 0.6 // 60% of average salary
    const val MIN_CONTRIBUTION_MONTHS = 12
    const val MAX_AGE = 60

    fun getMinimumBenefit(regionLevel: Int): Long {
        val base = UtilityConstants.REGIONAL_MINIMUM_WAGES[regionLevel] ?: UtilityConstants.REGIONAL_MINIMUM_WAGES[4]!!
        return (base * 0.3).toLong()
    }

    fun getMaximumBenefit(regionLevel: Int): Long {
        val base = UtilityConstants.REGIONAL_MINIMUM_WAGES[regionLevel] ?: UtilityConstants.REGIONAL_MINIMUM_WAGES[4]!!
        return base * 5
    }

    fun getMaxBenefitDuration(contributionMonths: Int): Int = when {
        contributionMonths < 12 -> 0
        contributionMonths < 36 -> 3
        contributionMonths < 72 -> 6
        contributionMonths < 144 -> 9
        else -> 12
    }
}

/**
 * Calculator for unemployment insurance benefits
 */
object UnemploymentInsuranceCalculator {
    
    fun calculate(input: UnemploymentInput): UnemploymentResult {
        val averageSalary = input.averageSalary.toLongOrNull() ?: 0L
        val contributionMonths = input.contributionMonths.toIntOrNull() ?: 0
        val currentAge = input.currentAge.toIntOrNull() ?: 0
        val regionLevel = (input.regionLevel).coerceIn(1, 4)

        // Check eligibility
        val eligibility = checkEligibility(contributionMonths, currentAge)

        // Calculate base benefit (60% of average salary) with min/max limits
        val baseBenefit = (averageSalary * UnemploymentConstants.BENEFIT_RATE).toLong()
        val minBenefit = UnemploymentConstants.getMinimumBenefit(regionLevel)
        val maxBenefit = UnemploymentConstants.getMaximumBenefit(regionLevel)
        val monthlyBenefit = when {
            baseBenefit < minBenefit -> minBenefit
            baseBenefit > maxBenefit -> maxBenefit
            else -> baseBenefit
        }

        val maxDuration = if (eligibility == UnemploymentEligibility.ELIGIBLE)
            UnemploymentConstants.getMaxBenefitDuration(contributionMonths) else 0
        val totalBenefit = monthlyBenefit * maxDuration

        val requirements = mutableListOf<String>()
        requirements.add("Đóng ít nhất ${UnemploymentConstants.MIN_CONTRIBUTION_MONTHS} tháng trong 24 tháng gần nhất")
        requirements.add("Tuổi dưới ${UnemploymentConstants.MAX_AGE}")
        if (input.hasVocationalTraining) requirements.add("Đã tham gia học nghề/đào tạo kỹ năng")

        return UnemploymentResult(
            monthlyBenefit = monthlyBenefit,
            maxDuration = maxDuration,
            totalBenefit = totalBenefit,
            eligibilityStatus = when (eligibility) {
                UnemploymentEligibility.ELIGIBLE -> UnemploymentEligibility.ELIGIBLE
                UnemploymentEligibility.NOT_ENOUGH_CONTRIBUTIONS -> UnemploymentEligibility.NOT_ENOUGH_CONTRIBUTIONS
                UnemploymentEligibility.TOO_OLD -> UnemploymentEligibility.TOO_OLD
                else -> UnemploymentEligibility.OTHER_ISSUES
            },
            requirements = requirements
        )
    }
    
    private fun checkEligibility(contributionMonths: Int, currentAge: Int): UnemploymentEligibility {
        return when {
            contributionMonths < UnemploymentConstants.MIN_CONTRIBUTION_MONTHS -> 
                UnemploymentEligibility.NOT_ENOUGH_CONTRIBUTIONS
            currentAge >= UnemploymentConstants.MAX_AGE -> 
                UnemploymentEligibility.TOO_OLD
            else -> UnemploymentEligibility.ELIGIBLE
        }
    }
    
    /**
     * Get contribution requirements info
     */
    fun getContributionRequirements(): Map<String, String> {
        return mapOf(
            "Tối thiểu" to "12 tháng trong vòng 24 tháng gần nhất",
            "Tuổi tối đa" to "Dưới 60 tuổi",
            "Điều kiện khác" to "Mất việc không do lỗi của người lao động"
        )
    }
    
    /**
     * Get benefit duration table
     */
    fun getBenefitDurationTable(): Map<String, Int> {
        return mapOf(
            "12-35 tháng" to 3,
            "36-71 tháng" to 6,
            "72-143 tháng" to 9,
            "144+ tháng" to 12
        )
    }
}