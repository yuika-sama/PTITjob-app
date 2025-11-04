package com.example.ptitjob.ui.screen.candidate.utilities.models

import androidx.compose.runtime.Immutable

// ===== BHXH MODELS =====
@Immutable
data class BHXHInput(
    val monthlyGrossSalary: String = "",
    val contributionMonths: String = "",
    val insuranceType: BHXHType = BHXHType.MANDATORY,
    val regionLevel: Int = 1, // Vùng 1, 2, 3, 4
    val dependents: String = "0"
) {
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (monthlyGrossSalary.isBlank()) {
            errors.add("Vui lòng nhập lương tháng")
        } else {
            val salary = monthlyGrossSalary.toLongOrNull()
            if (salary == null || salary <= 0) {
                errors.add("Lương tháng phải là số dương")
            } else if (salary < 1000000) {
                errors.add("Lương tháng tối thiểu 1,000,000 VNĐ")
            } else if (salary > 1000000000) {
                errors.add("Lương tháng không được vượt quá 1 tỷ VNĐ")
            }
        }
        
        if (contributionMonths.isBlank()) {
            errors.add("Vui lòng nhập số tháng đóng")
        } else {
            val months = contributionMonths.toIntOrNull()
            if (months == null || months <= 0) {
                errors.add("Số tháng đóng phải là số dương")
            } else if (months > 600) { // 50 years max
                errors.add("Số tháng đóng không được vượt quá 600 tháng")
            }
        }
        
        val deps = dependents.toIntOrNull()
        if (deps == null || deps < 0) {
            errors.add("Số người phụ thuộc phải là số không âm")
        } else if (deps > 20) {
            errors.add("Số người phụ thuộc không được vượt quá 20")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
}

@Immutable
data class BHXHResult(
    val totalAmount: Double,
    val averageSalary: Double,
    val totalMonths: Int,
    val periods: List<BHXHPeriod>,
    val calculationType: BHXHType = BHXHType.MANDATORY
)

@Immutable
data class BHXHPeriod(
    val id: String,
    val startYear: Int,
    val endYear: Int?,
    val months: Int,
    val averageSalary: Long
)

enum class BHXHType(val displayName: String) {
    MANDATORY("BHXH bắt buộc"),
    VOLUNTARY("BHXH tự nguyện"),
    BOTH("Cả hai loại")
}

// ===== TAX MODELS =====
@Immutable
data class TaxInput(
    val monthlyGrossSalary: String = "",
    val dependents: String = "0",
    val personalDeduction: String = "11000000", // Default personal deduction
    val insuranceDeduction: String = "",
    val otherDeductions: String = "0"
) {
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (monthlyGrossSalary.isBlank()) {
            errors.add("Vui lòng nhập lương gộp tháng")
        } else {
            val salary = monthlyGrossSalary.toLongOrNull()
            if (salary == null || salary <= 0) {
                errors.add("Lương gộp phải là số dương")
            } else if (salary > 1000000000) {
                errors.add("Lương gộp không được vượt quá 1 tỷ VNĐ")
            }
        }
        
        val deps = dependents.toIntOrNull()
        if (deps == null || deps < 0) {
            errors.add("Số người phụ thuộc phải là số không âm")
        } else if (deps > 20) {
            errors.add("Số người phụ thuộc không được vượt quá 20")
        }
        
        val personalDed = personalDeduction.toLongOrNull()
        if (personalDed == null || personalDed < 0) {
            errors.add("Giảm trừ bản thân phải là số không âm")
        }
        
        val insuranceDed = insuranceDeduction.toLongOrNull()
        if (insuranceDed != null && insuranceDed < 0) {
            errors.add("Khấu trừ bảo hiểm phải là số không âm")
        }
        
        val otherDed = otherDeductions.toLongOrNull()
        if (otherDed == null || otherDed < 0) {
            errors.add("Khấu trừ khác phải là số không âm")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
}

@Immutable
data class TaxCalculationResult(
    val grossSalary: Long,
    val personalDeduction: Long,
    val dependentDeduction: Long,
    val insuranceDeduction: Long,
    val otherDeductions: Long,
    val taxableIncome: Long,
    val personalIncomeTax: Long,
    val netSalary: Long,
    val breakdown: List<TaxBracketBreakdown> = emptyList()
)

@Immutable
data class TaxBracketBreakdown(
    val level: Int,
    val rate: Double,
    val taxableAmount: Long,
    val tax: Long,
    val description: String
)

// ===== SALARY MODELS =====
@Immutable
data class SalaryInput(
    val inputSalary: String = "",
    val dependents: String = "0",
    val region: Int = 1, // For minimum wage calculation
    val hasUnemploymentInsurance: Boolean = true,
    val calculationType: SalaryCalculationType = SalaryCalculationType.GROSS_TO_NET
) {
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (inputSalary.isBlank()) {
            errors.add("Vui lòng nhập mức lương")
        } else {
            val salary = inputSalary.toLongOrNull()
            if (salary == null || salary <= 0) {
                errors.add("Mức lương phải là số dương")
            } else if (salary > 1000000000) {
                errors.add("Mức lương không được vượt quá 1 tỷ VNĐ")
            }
        }
        
        val deps = dependents.toIntOrNull()
        if (deps == null || deps < 0) {
            errors.add("Số người phụ thuộc phải là số không âm")
        } else if (deps > 20) {
            errors.add("Số người phụ thuộc không được vượt quá 20")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
}

@Immutable
data class SalaryCalculationResult(
    val grossSalary: Long,
    val netSalary: Long,
    val socialInsurance: Long = 0,
    val healthInsurance: Long = 0,
    val unemploymentInsurance: Long = 0,
    val personalIncomeTax: Long = 0,
    val totalDeductions: Long = 0,
    val breakdown: SalaryBreakdown
)

@Immutable
data class SalaryBreakdown(
    val socialInsuranceRate: Double = 0.08,
    val healthInsuranceRate: Double = 0.015,
    val unemploymentInsuranceRate: Double = 0.01,
    val personalDeduction: Long = 11000000,
    val dependentDeduction: Long = 0
)

enum class SalaryCalculationType(val displayName: String) {
    GROSS_TO_NET("Từ lương gộp sang thực nhận"),
    NET_TO_GROSS("Từ lương thực nhận sang gộp")
}

// ===== COMPOUND INTEREST MODELS =====
@Immutable
data class CompoundInterestInput(
    val principal: String = "",
    val annualRate: String = "",
    val years: String = "",
    val compoundFrequency: CompoundFrequency = CompoundFrequency.YEARLY,
    val monthlyContribution: String = "0"
) {
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (principal.isBlank()) {
            errors.add("Vui lòng nhập số tiền ban đầu")
        } else {
            val amount = principal.toLongOrNull()
            if (amount == null || amount <= 0) {
                errors.add("Số tiền ban đầu phải là số dương")
            } else if (amount > 10000000000000) { // 10 trillion
                errors.add("Số tiền ban đầu quá lớn")
            }
        }
        
        if (annualRate.isBlank()) {
            errors.add("Vui lòng nhập lãi suất năm")
        } else {
            val rate = annualRate.toDoubleOrNull()
            if (rate == null || rate <= 0) {
                errors.add("Lãi suất phải là số dương")
            } else if (rate > 100) {
                errors.add("Lãi suất không được vượt quá 100%")
            }
        }
        
        if (years.isBlank()) {
            errors.add("Vui lòng nhập số năm đầu tư")
        } else {
            val y = years.toIntOrNull()
            if (y == null || y <= 0) {
                errors.add("Số năm đầu tư phải là số dương")
            } else if (y > 100) {
                errors.add("Số năm đầu tư không được vượt quá 100 năm")
            }
        }
        
        val monthlyAmount = monthlyContribution.toLongOrNull()
        if (monthlyAmount != null && monthlyAmount < 0) {
            errors.add("Đóng góp hàng tháng không được âm")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
}

@Immutable
data class CompoundInterestResult(
    val principal: Long,
    val totalContributions: Long,
    val totalInvestment: Long,
    val finalAmount: Long,
    val totalInterest: Long,
    val yearlyBreakdown: List<YearlyGrowth>,
    val effectiveRate: Double
)

@Immutable
data class YearlyGrowth(
    val year: Int,
    val startAmount: Long,
    val contribution: Long,
    val interest: Long,
    val endAmount: Long
)

enum class CompoundFrequency(val displayName: String, val value: Int) {
    YEARLY("Hàng năm", 1),
    SEMI_ANNUALLY("6 tháng/lần", 2),
    QUARTERLY("Hàng quý", 4),
    MONTHLY("Hàng tháng", 12),
    DAILY("Hàng ngày", 365)
}

// ===== UNEMPLOYMENT INSURANCE MODELS =====
@Immutable
data class UnemploymentInput(
    val averageSalary: String = "",
    val contributionMonths: String = "",
    val currentAge: String = "",
    val hasVocationalTraining: Boolean = false,
    val regionLevel: Int = 0
) {
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (averageSalary.isBlank()) {
            errors.add("Vui lòng nhập lương trung bình")
        } else {
            val salary = averageSalary.toLongOrNull()
            if (salary == null || salary <= 0) {
                errors.add("Lương trung bình phải là số dương")
            } else if (salary > 100000000) {
                errors.add("Lương trung bình quá cao")
            }
        }
        
        if (contributionMonths.isBlank()) {
            errors.add("Vui lòng nhập số tháng đóng")
        } else {
            val months = contributionMonths.toIntOrNull()
            if (months == null || months <= 0) {
                errors.add("Số tháng đóng phải là số dương")
            } else if (months > 600) {
                errors.add("Số tháng đóng không được vượt quá 600 tháng")
            }
        }
        
        if (currentAge.isBlank()) {
            errors.add("Vui lòng nhập tuổi hiện tại")
        } else {
            val age = currentAge.toIntOrNull()
            if (age == null || age < 15) {
                errors.add("Tuổi phải từ 15 trở lên")
            } else if (age > 100) {
                errors.add("Tuổi không hợp lệ")
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
}

@Immutable
data class UnemploymentResult(
    val monthlyBenefit: Long,
    val maxDuration: Int, // months
    val totalBenefit: Long,
    val eligibilityStatus: UnemploymentEligibility,
    val requirements: List<String>
)

enum class UnemploymentEligibility(val displayName: String) {
    ELIGIBLE("Đủ điều kiện"),
    NOT_ENOUGH_CONTRIBUTIONS("Chưa đủ thời gian đóng"),
    TOO_OLD("Quá tuổi lao động"),
    OTHER_ISSUES("Cần kiểm tra thêm")
}

// ===== VALIDATION RESULT =====
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val errors: List<String>) : ValidationResult()
    
    val isValid: Boolean get() = this is Success
    val errorMessages: List<String> get() = when (this) {
        is Success -> emptyList()
        is Error -> errors
    }
}

// ===== UI STATE =====
@Immutable
data class CalculatorUiState<T>(
    val isLoading: Boolean = false,
    val input: T,
    val result: Any? = null,
    val error: String? = null,
    val validationErrors: List<String> = emptyList()
) {
    val hasValidationErrors: Boolean get() = validationErrors.isNotEmpty()
    val hasError: Boolean get() = error != null
    val hasResult: Boolean get() = result != null && !hasError
}

// ===== UTILITY CONSTANTS =====
object UtilityConstants {
    // Tax brackets 2025
    val TAX_BRACKETS = listOf(
        TaxBracket(0, 5000000, 0.05),
        TaxBracket(5000000, 10000000, 0.10),
        TaxBracket(10000000, 18000000, 0.15),
        TaxBracket(18000000, 32000000, 0.20),
        TaxBracket(32000000, 52000000, 0.25),
        TaxBracket(52000000, 80000000, 0.30),
        TaxBracket(80000000, Long.MAX_VALUE, 0.35)
    )
    
    // Insurance rates
    const val SOCIAL_INSURANCE_RATE = 0.08
    const val HEALTH_INSURANCE_RATE = 0.015
    const val UNEMPLOYMENT_INSURANCE_RATE = 0.01
    
    // Deductions
    const val PERSONAL_DEDUCTION_2025 = 11000000L
    const val DEPENDENT_DEDUCTION_2025 = 4400000L

    const val MINIMUM_REGIONAL_WAGE_ZONE_1_2025 = 10000000L
    const val MINIMUM_REGIONAL_WAGE_ZONE_2_2025 = 11000000L
    const val MINIMUM_REGIONAL_WAGE_ZONE_3_2025 = 12000000L
    const val MINIMUM_REGIONAL_WAGE_ZONE_4_2025 = 13000000L

    // Regional minimum wages 2025
    val REGIONAL_MINIMUM_WAGES = mapOf(
        1 to 4680000L,
        2 to 4160000L,
        3 to 3640000L,
        4 to 3250000L
    )
}

@Immutable
data class TaxBracket(
    val min: Long,
    val max: Long,
    val rate: Double
)