package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.ui.screen.candidate.utilities.calculations.*
import com.example.ptitjob.ui.screen.candidate.utilities.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Comprehensive ViewModel for all Utilities/Calculators
 * Handles navigation, calculations, and state management
 */
@HiltViewModel
class UtilitiesViewModel @Inject constructor() : ViewModel() {

    // =========================== BHXH Calculator ===========================
    private val _bhxhState = MutableStateFlow(
        CalculatorUiState(
            input = BHXHInput(),
            isLoading = false
        )
    )
    val bhxhState: StateFlow<CalculatorUiState<BHXHInput>> = _bhxhState.asStateFlow()

    fun updateBHXHInput(input: BHXHInput) {
        _bhxhState.value = _bhxhState.value.copy(
            input = input,
            validationErrors = emptyList(),
            error = null
        )
    }

    fun calculateBHXH() {
        val currentInput = _bhxhState.value.input
        val validation = currentInput.validate()
        
        if (!validation.isValid) {
            _bhxhState.value = _bhxhState.value.copy(
                validationErrors = validation.errorMessages
            )
            return
        }

        viewModelScope.launch {
            _bhxhState.value = _bhxhState.value.copy(isLoading = true, error = null)
            
            try {
                val result = BHXHCalculator.calculate(currentInput)
                _bhxhState.value = _bhxhState.value.copy(
                    isLoading = false,
                    result = result,
                    validationErrors = emptyList()
                )
            } catch (e: Exception) {
                _bhxhState.value = _bhxhState.value.copy(
                    isLoading = false,
                    error = "Lỗi tính toán: ${e.message}",
                    result = null
                )
            }
        }
    }

    fun clearBHXHResult() {
        _bhxhState.value = _bhxhState.value.copy(
            result = null,
            error = null,
            validationErrors = emptyList()
        )
    }

    // =========================== Tax Calculator ===========================
    private val _taxState = MutableStateFlow(
        CalculatorUiState(
            input = TaxInput(),
            isLoading = false
        )
    )
    val taxState: StateFlow<CalculatorUiState<TaxInput>> = _taxState.asStateFlow()

    fun updateTaxInput(input: TaxInput) {
        _taxState.value = _taxState.value.copy(
            input = input,
            validationErrors = emptyList(),
            error = null
        )
    }

    fun calculateTax() {
        val currentInput = _taxState.value.input
        val validation = currentInput.validate()
        
        if (!validation.isValid) {
            _taxState.value = _taxState.value.copy(
                validationErrors = validation.errorMessages
            )
            return
        }

        viewModelScope.launch {
            _taxState.value = _taxState.value.copy(isLoading = true, error = null)
            
            try {
                val result = TaxCalculator.calculatePersonalIncomeTax(currentInput)
                _taxState.value = _taxState.value.copy(
                    isLoading = false,
                    result = result,
                    validationErrors = emptyList()
                )
            } catch (e: Exception) {
                _taxState.value = _taxState.value.copy(
                    isLoading = false,
                    error = "Lỗi tính toán thuế: ${e.message}",
                    result = null
                )
            }
        }
    }

    fun clearTaxResult() {
        _taxState.value = _taxState.value.copy(
            result = null,
            error = null,
            validationErrors = emptyList()
        )
    }

    // =========================== Salary Calculator ===========================
    private val _salaryState = MutableStateFlow(
        CalculatorUiState(
            input = SalaryInput(),
            isLoading = false
        )
    )
    val salaryState: StateFlow<CalculatorUiState<SalaryInput>> = _salaryState.asStateFlow()

    fun updateSalaryInput(input: SalaryInput) {
        _salaryState.value = _salaryState.value.copy(
            input = input,
            validationErrors = emptyList(),
            error = null
        )
    }

    fun calculateSalary() {
        val currentInput = _salaryState.value.input
        val validation = currentInput.validate()
        
        if (!validation.isValid) {
            _salaryState.value = _salaryState.value.copy(
                validationErrors = validation.errorMessages
            )
            return
        }

        viewModelScope.launch {
            _salaryState.value = _salaryState.value.copy(isLoading = true, error = null)
            
            try {
                val result = when (currentInput.calculationType) {
                    SalaryCalculationType.GROSS_TO_NET -> SalaryCalculator.calculateGrossToNet(currentInput)
                    SalaryCalculationType.NET_TO_GROSS -> SalaryCalculator.calculateNetToGross(currentInput)
                }
                
                _salaryState.value = _salaryState.value.copy(
                    isLoading = false,
                    result = result,
                    validationErrors = emptyList()
                )
            } catch (e: Exception) {
                _salaryState.value = _salaryState.value.copy(
                    isLoading = false,
                    error = "Lỗi tính toán lương: ${e.message}",
                    result = null
                )
            }
        }
    }

    fun clearSalaryResult() {
        _salaryState.value = _salaryState.value.copy(
            result = null,
            error = null,
            validationErrors = emptyList()
        )
    }

    // =========================== Compound Interest Calculator ===========================
    private val _compoundInterestState = MutableStateFlow(
        CalculatorUiState(
            input = CompoundInterestInput(),
            isLoading = false
        )
    )
    val compoundInterestState: StateFlow<CalculatorUiState<CompoundInterestInput>> = _compoundInterestState.asStateFlow()

    fun updateCompoundInterestInput(input: CompoundInterestInput) {
        _compoundInterestState.value = _compoundInterestState.value.copy(
            input = input,
            validationErrors = emptyList(),
            error = null
        )
    }

    fun calculateCompoundInterest() {
        val currentInput = _compoundInterestState.value.input
        val validation = currentInput.validate()
        
        if (!validation.isValid) {
            _compoundInterestState.value = _compoundInterestState.value.copy(
                validationErrors = validation.errorMessages
            )
            return
        }

        viewModelScope.launch {
            _compoundInterestState.value = _compoundInterestState.value.copy(isLoading = true, error = null)
            
            try {
                val result = CompoundInterestCalculator.calculate(currentInput)
                _compoundInterestState.value = _compoundInterestState.value.copy(
                    isLoading = false,
                    result = result,
                    validationErrors = emptyList()
                )
            } catch (e: Exception) {
                _compoundInterestState.value = _compoundInterestState.value.copy(
                    isLoading = false,
                    error = "Lỗi tính toán lãi suất: ${e.message}",
                    result = null
                )
            }
        }
    }

    fun clearCompoundInterestResult() {
        _compoundInterestState.value = _compoundInterestState.value.copy(
            result = null,
            error = null,
            validationErrors = emptyList()
        )
    }

    // =========================== Unemployment Insurance Calculator ===========================
    private val _unemploymentState = MutableStateFlow(
        CalculatorUiState(
            input = UnemploymentInput(),
            isLoading = false
        )
    )
    val unemploymentState: StateFlow<CalculatorUiState<UnemploymentInput>> = _unemploymentState.asStateFlow()

    fun updateUnemploymentInput(input: UnemploymentInput) {
        _unemploymentState.value = _unemploymentState.value.copy(
            input = input,
            validationErrors = emptyList(),
            error = null
        )
    }

    fun calculateUnemployment() {
        val currentInput = _unemploymentState.value.input
        val validation = currentInput.validate()
        
        if (!validation.isValid) {
            _unemploymentState.value = _unemploymentState.value.copy(
                validationErrors = validation.errorMessages
            )
            return
        }

        viewModelScope.launch {
            _unemploymentState.value = _unemploymentState.value.copy(isLoading = true, error = null)
            
            try {
                val result = UnemploymentInsuranceCalculator.calculate(currentInput)
                _unemploymentState.value = _unemploymentState.value.copy(
                    isLoading = false,
                    result = result,
                    validationErrors = emptyList()
                )
            } catch (e: Exception) {
                _unemploymentState.value = _unemploymentState.value.copy(
                    isLoading = false,
                    error = "Lỗi tính toán bảo hiểm thất nghiệp: ${e.message}",
                    result = null
                )
            }
        }
    }

    fun clearUnemploymentResult() {
        _unemploymentState.value = _unemploymentState.value.copy(
            result = null,
            error = null,
            validationErrors = emptyList()
        )
    }

    // =========================== Navigation Logic ===========================
    
    /**
     * Get popular calculator recommendations based on user demographics
     */
    fun getRecommendedCalculators(): List<String> {
        return listOf(
            "SalaryCalculator", // Most used
            "PersonalIncomeTax", // Essential for all workers
            "BHXHCalculator", // Important for long-term planning
            "CompoundInterest" // Financial planning
        )
    }

    /**
     * Get calculator usage analytics (mock data - would come from analytics API)
     */
    fun getCalculatorUsageStats(): Map<String, Int> {
        return mapOf(
            "SalaryCalculator" to 1250,
            "PersonalIncomeTax" to 890,
            "BHXHCalculator" to 670,
            "UnemploymentInsurance" to 420,
            "CompoundInterest" to 380
        )
    }

    /**
     * Save calculation result for sharing/export (would integrate with API)
     */
    fun saveCalculationResult(calculatorType: String, result: Any): String {
        // In real app, this would save to API and return share URL
        return "https://ptitjob.app/shared-calculation/${System.currentTimeMillis()}"
    }

    /**
     * Get calculator tutorial/help content
     */
    fun getCalculatorHelp(calculatorType: String): List<String> {
        return when (calculatorType) {
            "BHXHCalculator" -> listOf(
                "Nhập lương tháng gộp của bạn",
                "Chọn số tháng đã đóng bảo hiểm",
                "Chọn loại bảo hiểm (bắt buộc/tự nguyện)",
                "Kết quả sẽ hiển thị số tiền dự kiến nhận được"
            )
            "PersonalIncomeTax" -> listOf(
                "Nhập lương gộp tháng",
                "Nhập số người phụ thuộc",
                "Điều chỉnh các khoản giảm trừ nếu cần",
                "Xem chi tiết thuế theo từng bậc"
            )
            "SalaryCalculator" -> listOf(
                "Chọn tính từ lương gộp hoặc lương NET",
                "Nhập số tiền tương ứng",
                "Nhập số người phụ thuộc",
                "Xem breakdown chi tiết các khoản khấu trừ"
            )
            "CompoundInterest" -> listOf(
                "Nhập số tiền đầu tư ban đầu",
                "Nhập lãi suất năm (%)",
                "Chọn thời gian đầu tư",
                "Có thể thêm khoản đóng góp hàng tháng"
            )
            "UnemploymentInsurance" -> listOf(
                "Nhập lương trung bình 6 tháng gần nhất",
                "Nhập số tháng đã đóng bảo hiểm",
                "Nhập tuổi hiện tại",
                "Xem mức trợ cấp và thời gian nhận"
            )
            else -> listOf("Hướng dẫn sử dụng calculator")
        }
    }

    // =========================== Utility Functions ===========================
    
    fun formatCurrency(amount: Long): String = formatCurrency(amount)
    fun formatPercentage(value: Double): String = formatPercentage(value)
    fun formatNumber(number: Long): String = formatNumber(number)
    
    /**
     * Reset all calculator states
     */
    fun resetAllCalculators() {
        _bhxhState.value = CalculatorUiState(input = BHXHInput())
        _taxState.value = CalculatorUiState(input = TaxInput())
        _salaryState.value = CalculatorUiState(input = SalaryInput())
        _compoundInterestState.value = CalculatorUiState(input = CompoundInterestInput())
        _unemploymentState.value = CalculatorUiState(input = UnemploymentInput())
    }
}