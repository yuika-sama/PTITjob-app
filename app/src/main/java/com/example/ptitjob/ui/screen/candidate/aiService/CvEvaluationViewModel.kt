package com.example.ptitjob.ui.screen.candidate.aiService

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.model.CvEvaluationResult
import com.example.ptitjob.data.repository.AiServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CvEvaluationViewModel @Inject constructor(
    private val aiServiceRepository: AiServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CvEvaluationUiState())
    val uiState: StateFlow<CvEvaluationUiState> = _uiState.asStateFlow()

    fun evaluateCv(file: File, displayName: String?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    step = CvEvaluationStep.Analyzing,
                    isLoading = true,
                    selectedFileName = displayName ?: file.name,
                    errorMessage = null
                )
            }

            // Validate file before sending
            try {
                if (!file.exists()) {
                    throw IllegalStateException("File không tồn tại")
                }
                if (file.length() == 0L) {
                    throw IllegalStateException("File rỗng")
                }
                if (file.length() > 10 * 1024 * 1024) { // 10MB
                    throw IllegalStateException("File quá lớn (tối đa 10MB)")
                }
                if (!file.name.lowercase().endsWith(".pdf")) {
                    throw IllegalStateException("Chỉ hỗ trợ file PDF")
                }
                
                println("✅ File validation passed - ${file.name} (${file.length()} bytes)")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        step = CvEvaluationStep.Upload,
                        isLoading = false,
                        evaluation = null,
                        errorMessage = e.message ?: "File không hợp lệ"
                    )
                }
                file.delete()
                return@launch
            }

            // Use default job description or provide a more comprehensive one
            val jobDescription = """
                Software Developer position requiring:
                - Programming skills (Java, Kotlin, Python, JavaScript, etc.)
                - Software development experience
                - Problem-solving abilities
                - Communication and teamwork skills
                - Bachelor's degree in Computer Science or related field
            """.trimIndent()

            val result = aiServiceRepository.evaluateCv(
                file = file,
                jobDescription = jobDescription,
                jobSkills = "Programming, Software Development, Problem Solving, Communication"
            )
            file.delete()

            result.fold(
                onSuccess = { evaluation ->
                    _uiState.update {
                        it.copy(
                            step = CvEvaluationStep.Result,
                            isLoading = false,
                            evaluation = evaluation
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            step = CvEvaluationStep.Upload,
                            isLoading = false,
                            evaluation = null,
                            errorMessage = error.resolveMessage()
                        )
                    }
                }
            )
        }
    }

    fun reset() {
        _uiState.value = CvEvaluationUiState()
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun reportError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    private fun Throwable.resolveMessage(): String {
        return message?.takeIf { it.isNotBlank() } ?: "Không thể đánh giá CV. Vui lòng thử lại."
    }
}

data class CvEvaluationUiState(
    val step: CvEvaluationStep = CvEvaluationStep.Upload,
    val isLoading: Boolean = false,
    val selectedFileName: String? = null,
    val evaluation: CvEvaluationResult? = null,
    val errorMessage: String? = null
)

enum class CvEvaluationStep { Upload, Analyzing, Result }
