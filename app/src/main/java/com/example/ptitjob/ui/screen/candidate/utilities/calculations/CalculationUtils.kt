package com.example.ptitjob.ui.screen.candidate.utilities.calculations

import com.example.ptitjob.ui.screen.candidate.utilities.models.*
import kotlin.math.pow
import kotlin.math.round

/**
 * Utility class for all tax-related calculations
 */
object TaxCalculator {
    
    fun calculatePersonalIncomeTax(input: TaxInput): TaxCalculationResult {
        val grossSalary = input.monthlyGrossSalary.toLong()
        val dependents = input.dependents.toInt()
        val personalDeduction = input.personalDeduction.toLong()
        val insuranceDeduction = input.insuranceDeduction.toLongOrNull() ?: calculateInsuranceDeduction(grossSalary)
        val otherDeductions = input.otherDeductions.toLong()
        
        val dependentDeduction = dependents * UtilityConstants.DEPENDENT_DEDUCTION_2025
        val totalDeductions = personalDeduction + dependentDeduction + insuranceDeduction + otherDeductions
        val taxableIncome = maxOf(0, grossSalary - totalDeductions)
        
        val (tax, breakdown) = calculateTaxWithBreakdown(taxableIncome)
        val netSalary = grossSalary - insuranceDeduction - tax
        
        return TaxCalculationResult(
            grossSalary = grossSalary,
            personalDeduction = personalDeduction,
            dependentDeduction = dependentDeduction,
            insuranceDeduction = insuranceDeduction,
            otherDeductions = otherDeductions,
            taxableIncome = taxableIncome,
            personalIncomeTax = tax,
            netSalary = netSalary,
            breakdown = breakdown
        )
    }
    
    private fun calculateInsuranceDeduction(grossSalary: Long): Long {
        val socialInsurance = (grossSalary * UtilityConstants.SOCIAL_INSURANCE_RATE).toLong()
        val healthInsurance = (grossSalary * UtilityConstants.HEALTH_INSURANCE_RATE).toLong()
        val unemploymentInsurance = (grossSalary * UtilityConstants.UNEMPLOYMENT_INSURANCE_RATE).toLong()
        return socialInsurance + healthInsurance + unemploymentInsurance
    }
    
    private fun calculateTaxWithBreakdown(taxableIncome: Long): Pair<Long, List<TaxBracketBreakdown>> {
        var remainingIncome = taxableIncome
        var totalTax = 0L
        val breakdown = mutableListOf<TaxBracketBreakdown>()
        
        for ((index, bracket) in UtilityConstants.TAX_BRACKETS.withIndex()) {
            if (remainingIncome <= 0) break
            
            val bracketMin = if (index == 0) 0 else UtilityConstants.TAX_BRACKETS[index - 1].max
            val bracketMax = bracket.max
            val bracketRange = bracketMax - bracketMin
            
            val taxableInThisBracket = minOf(remainingIncome, bracketRange)
            val taxInThisBracket = (taxableInThisBracket * bracket.rate).toLong()
            
            if (taxableInThisBracket > 0) {
                breakdown.add(
                    TaxBracketBreakdown(
                        level = index + 1,
                        rate = bracket.rate,
                        taxableAmount = taxableInThisBracket,
                        tax = taxInThisBracket,
                        description = formatBracketDescription(bracketMin, bracketMax, bracket.rate)
                    )
                )
                totalTax += taxInThisBracket
                remainingIncome -= taxableInThisBracket
            }
        }
        
        return totalTax to breakdown
    }
    
    private fun formatBracketDescription(min: Long, max: Long, rate: Double): String {
        val minFormatted = formatCurrency(min)
        val maxFormatted = if (max == Long.MAX_VALUE) "trở lên" else formatCurrency(max)
        val ratePercent = (rate * 100).toInt()
        return "Từ $minFormatted đến $maxFormatted: $ratePercent%"
    }
}

/**
 * Utility class for salary calculations (Gross to Net and Net to Gross)
 */
object SalaryCalculator {
    
    fun calculateGrossToNet(input: SalaryInput): SalaryCalculationResult {
        val grossSalary = input.inputSalary.toLong()
        val dependents = input.dependents.toInt()
        
        val socialInsurance = (grossSalary * UtilityConstants.SOCIAL_INSURANCE_RATE).toLong()
        val healthInsurance = (grossSalary * UtilityConstants.HEALTH_INSURANCE_RATE).toLong()
        val unemploymentInsurance = if (input.hasUnemploymentInsurance) {
            (grossSalary * UtilityConstants.UNEMPLOYMENT_INSURANCE_RATE).toLong()
        } else 0L
        
        val totalInsurance = socialInsurance + healthInsurance + unemploymentInsurance
        val taxableIncome = maxOf(0, grossSalary - totalInsurance - UtilityConstants.PERSONAL_DEDUCTION_2025 - (dependents * UtilityConstants.DEPENDENT_DEDUCTION_2025))
        val personalIncomeTax = calculateSimpleTax(taxableIncome)
        
        val totalDeductions = totalInsurance + personalIncomeTax
        val netSalary = grossSalary - totalDeductions
        
        return SalaryCalculationResult(
            grossSalary = grossSalary,
            netSalary = netSalary,
            socialInsurance = socialInsurance,
            healthInsurance = healthInsurance,
            unemploymentInsurance = unemploymentInsurance,
            personalIncomeTax = personalIncomeTax,
            totalDeductions = totalDeductions,
            breakdown = SalaryBreakdown(
                dependentDeduction = dependents * UtilityConstants.DEPENDENT_DEDUCTION_2025
            )
        )
    }
    
    fun calculateNetToGross(input: SalaryInput): SalaryCalculationResult {
        val targetNetSalary = input.inputSalary.toLong()
        
        // Use iterative approach to find gross salary
        var grossSalary = targetNetSalary * 12 / 10 // Initial estimate
        var iterations = 0
        val maxIterations = 50
        
        while (iterations < maxIterations) {
            val calculated = calculateGrossToNet(
                input.copy(
                    inputSalary = grossSalary.toString(),
                    calculationType = SalaryCalculationType.GROSS_TO_NET
                )
            )
            
            val diff = calculated.netSalary - targetNetSalary
            if (kotlin.math.abs(diff) < 1000) { // Within 1000 VND tolerance
                return calculated.copy(
                    grossSalary = grossSalary,
                    netSalary = targetNetSalary
                )
            }
            
            // Adjust gross salary
            grossSalary += (diff * -1.3).toLong()
            iterations++
        }
        
        // Fallback if iteration doesn't converge
        return calculateGrossToNet(
            input.copy(
                inputSalary = grossSalary.toString(),
                calculationType = SalaryCalculationType.GROSS_TO_NET
            )
        )
    }
    
    private fun calculateSimpleTax(taxableIncome: Long): Long {
        var remainingIncome = taxableIncome
        var totalTax = 0L
        
        for ((index, bracket) in UtilityConstants.TAX_BRACKETS.withIndex()) {
            if (remainingIncome <= 0) break
            
            val bracketMin = if (index == 0) 0 else UtilityConstants.TAX_BRACKETS[index - 1].max
            val bracketMax = bracket.max
            val bracketRange = bracketMax - bracketMin
            
            val taxableInThisBracket = minOf(remainingIncome, bracketRange)
            val taxInThisBracket = (taxableInThisBracket * bracket.rate).toLong()
            
            totalTax += taxInThisBracket
            remainingIncome -= taxableInThisBracket
        }
        
        return totalTax
    }
}

/**
 * Utility class for compound interest calculations
 */
object CompoundInterestCalculator {
    
    fun calculate(input: CompoundInterestInput): CompoundInterestResult {
        val principal = input.principal.toLong()
        val annualRate = input.annualRate.toDouble() / 100
        val years = input.years.toInt()
        val compoundFrequency = input.compoundFrequency.value
        val monthlyContribution = input.monthlyContribution.toLongOrNull() ?: 0L
        
        val yearlyBreakdown = mutableListOf<YearlyGrowth>()
        var currentAmount = principal.toDouble()
        var totalContributions = 0L
        
        for (year in 1..years) {
            val startAmount = currentAmount.toLong()
            val yearlyContribution = monthlyContribution * 12
            totalContributions += yearlyContribution
            
            // Add monthly contributions throughout the year
            for (month in 1..12) {
                currentAmount += monthlyContribution
                // Compound based on frequency
                currentAmount *= 1 + (annualRate / compoundFrequency)
            }
            
            val endAmount = currentAmount.toLong()
            val interest = endAmount - startAmount - yearlyContribution
            
            yearlyBreakdown.add(
                YearlyGrowth(
                    year = year,
                    startAmount = startAmount,
                    contribution = yearlyContribution,
                    interest = interest,
                    endAmount = endAmount
                )
            )
        }
        
        val finalAmount = currentAmount.toLong()
        val totalInvestment = principal + totalContributions
        val totalInterest = finalAmount - totalInvestment
        val effectiveRate = if (principal > 0) {
            ((finalAmount.toDouble() / principal - 1) / years) * 100
        } else 0.0
        
        return CompoundInterestResult(
            principal = principal,
            totalContributions = totalContributions,
            totalInvestment = totalInvestment,
            finalAmount = finalAmount,
            totalInterest = totalInterest,
            yearlyBreakdown = yearlyBreakdown,
            effectiveRate = effectiveRate
        )
    }
}

/**
 * Utility class for BHXH calculations
 */
object BHXHCalculator {
    
    fun calculate(input: BHXHInput): BHXHResult {
        val averageSalary = input.monthlyGrossSalary.toLong()
        val totalMonths = input.contributionMonths.toInt()
        
        // BHXH calculation formula (simplified)
        val baseAmount = when (input.insuranceType) {
            BHXHType.MANDATORY -> {
                // Simplified calculation for mandatory BHXH
                val monthlyBase = averageSalary * 0.06 // 6% employee contribution
                monthlyBase * totalMonths
            }
            BHXHType.VOLUNTARY -> {
                // Simplified calculation for voluntary BHXH
                val monthlyBase = averageSalary * 0.22 // 22% total contribution
                monthlyBase * totalMonths
            }
            BHXHType.BOTH -> {
                // Combined calculation
                val mandatoryBase = averageSalary * 0.06 * (totalMonths * 0.7)
                val voluntaryBase = averageSalary * 0.22 * (totalMonths * 0.3)
                mandatoryBase + voluntaryBase
            }
        }
        
        // Regional adjustments
        val regionalMultiplier = when (input.regionLevel) {
            1 -> 1.2
            2 -> 1.1
            3 -> 1.0
            4 -> 0.9
            else -> 1.0
        }
        
        val adjustedAmount = baseAmount * regionalMultiplier
        
        // Create periods (simplified - single period)
        val periods = listOf(
            BHXHPeriod(
                id = "1",
                startYear = 2020,
                endYear = 2024,
                months = totalMonths,
                averageSalary = averageSalary
            )
        )
        
        return BHXHResult(
            totalAmount = adjustedAmount,
            averageSalary = averageSalary.toDouble(),
            totalMonths = totalMonths,
            periods = periods,
            calculationType = input.insuranceType
        )
    }
}

/**
 * Utility class for unemployment insurance calculations
 */
object UnemploymentCalculator {
    
    fun calculate(input: UnemploymentInput): UnemploymentResult {
        val averageSalary = input.averageSalary.toLong()
        val contributionMonths = input.contributionMonths.toInt()
        val currentAge = input.currentAge.toInt()
        val regionLevel = input.regionLevel

        // Check eligibility
        val eligibility = when {
            contributionMonths < 12 -> UnemploymentEligibility.NOT_ENOUGH_CONTRIBUTIONS
            currentAge >= 60 -> UnemploymentEligibility.TOO_OLD
            else -> UnemploymentEligibility.ELIGIBLE
        }
        
        if (eligibility != UnemploymentEligibility.ELIGIBLE) {
            return UnemploymentResult(
                monthlyBenefit = 0,
                maxDuration = 0,
                totalBenefit = 0,
                eligibilityStatus = eligibility,
                requirements = getRequirements(eligibility)
            )
        }
        
        // Determine the minimum regional wage based on the provided region level
        val minimumRegionalWage = when (regionLevel) {
            1 -> UtilityConstants.MINIMUM_REGIONAL_WAGE_ZONE_1_2025
            2 -> UtilityConstants.MINIMUM_REGIONAL_WAGE_ZONE_2_2025
            3 -> UtilityConstants.MINIMUM_REGIONAL_WAGE_ZONE_3_2025
            4 -> UtilityConstants.MINIMUM_REGIONAL_WAGE_ZONE_4_2025
            else -> UtilityConstants.MINIMUM_REGIONAL_WAGE_ZONE_1_2025 // Default to zone 1
        }

        // Calculate benefits
        val monthlyBenefit = minOf(
            averageSalary * 0.6, // 60% of average salary
            (5 * minimumRegionalWage).toDouble() // Maximum benefit cap
        ).toLong()
        
        val maxDuration = when {
            contributionMonths >= 144 -> 12 // 12 years+ -> 12 months
            contributionMonths >= 84 -> 9   // 7-11 years -> 9 months
            contributionMonths >= 60 -> 6   // 5-6 years -> 6 months
            contributionMonths >= 36 -> 4   // 3-4 years -> 4 months
            contributionMonths >= 24 -> 3   // 2-2.9 years -> 3 months
            else -> 2                       // 1-1.9 years -> 2 months
        }
        
        val totalBenefit = monthlyBenefit * maxDuration
        
        return UnemploymentResult(
            monthlyBenefit = monthlyBenefit,
            maxDuration = maxDuration,
            totalBenefit = totalBenefit,
            eligibilityStatus = eligibility,
            requirements = getRequirements(eligibility)
        )
    }
    
    private fun getRequirements(eligibility: UnemploymentEligibility): List<String> {
        return when (eligibility) {
            UnemploymentEligibility.ELIGIBLE -> listOf(
                "Đã đóng bảo hiểm thất nghiệp đủ thời gian quy định",
                "Trong độ tuổi lao động",
                "Thất nghiệp không do lỗi của bản thân",
                "Đã nộp hồ sơ đề nghị hưởng bảo hiểm thất nghiệp"
            )
            UnemploymentEligibility.NOT_ENOUGH_CONTRIBUTIONS -> listOf(
                "Cần đóng bảo hiểm thất nghiệp ít nhất 12 tháng",
                "Thời gian đóng không được gián đoạn quá 60 ngày"
            )
            UnemploymentEligibility.TOO_OLD -> listOf(
                "Đã vượt quá độ tuổi lao động (60 tuổi)",
                "Không đủ điều kiện hưởng bảo hiểm thất nghiệp"
            )
            UnemploymentEligibility.OTHER_ISSUES -> listOf(
                "Cần kiểm tra lại thông tin cá nhân",
                "Liên hệ cơ quan bảo hiểm xã hội để được tư vấn"
            )
        }
    }
}

/**
 * Utility functions for formatting and display
 */
fun formatCurrency(amount: Long): String {
    return "${String.format("%,d", amount)} VNĐ"
}

fun formatCurrencyShort(amount: Long): String {
    return when {
        amount >= 1_000_000_000 -> "${String.format("%.1f", amount / 1_000_000_000.0)}B VNĐ"
        amount >= 1_000_000 -> "${String.format("%.1f", amount / 1_000_000.0)}M VNĐ"
        amount >= 1_000 -> "${String.format("%.1f", amount / 1_000.0)}K VNĐ"
        else -> "$amount VNĐ"
    }
}

fun formatPercentage(value: Double, decimals: Int = 1): String {
    return "${String.format("%.${decimals}f", value)}%"
}

fun formatNumber(number: Long): String {
    return String.format("%,d", number)
}