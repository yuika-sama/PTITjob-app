package com.example.ptitjob.ui.screen.candidate.aiService

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptitjob.data.model.ChatSender
import com.example.ptitjob.data.model.InterviewMessage
import com.example.ptitjob.data.model.InterviewResult
import com.example.ptitjob.data.model.InterviewSession
import com.example.ptitjob.data.repository.AiServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class InterviewEmulateViewModel @Inject constructor(
    private val aiServiceRepository: AiServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterviewUiState())
    val uiState: StateFlow<InterviewUiState> = _uiState.asStateFlow()

    fun startInterview(file: File, displayName: String?) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    step = InterviewStep.Preparing,
                    isLoading = true,
                    selectedFileName = displayName ?: file.name,
                    errorMessage = null,
                    session = null,
                    messages = emptyList(),
                    result = null
                )
            }

            // Bước 1: Evaluate CV trước
            val cvResult = aiServiceRepository.evaluateCv(file)
            file.delete()

            cvResult.fold(
                onSuccess = { cvEvaluation ->
                    // Convert CvEvaluationResult to Map for backend
                    val cvAnalysisResult = mapOf(
                        "candidate" to mapOf("name" to ""),
                        "scoring" to mapOf("overall_score_percent" to cvEvaluation.score),
                        "matching" to mapOf(
                            "skills_matched" to cvEvaluation.strengths,
                            "skills_missing" to cvEvaluation.improvements
                        ),
                        "analysis" to mapOf(
                            "summary" to (cvEvaluation.summary ?: ""),
                            "strengths" to cvEvaluation.strengths,
                            "improvements" to cvEvaluation.improvements,
                            "recommendations" to cvEvaluation.recommendations
                        )
                    )

                    // Bước 2: Start interview
                    val interviewResult = aiServiceRepository.startInterview(cvAnalysisResult)
                    
                    interviewResult.fold(
                        onSuccess = { session ->
                            val initialMessages = buildList {
                                session.initialMessage?.takeIf { msg -> msg.isNotBlank() }?.let { msg ->
                                    add(
                                        InterviewMessage(
                                            sender = ChatSender.AI,
                                            message = msg,
                                            timestamp = OffsetDateTime.now(),
                                            state = session.state
                                        )
                                    )
                                }
                            }
                            _uiState.update {
                                it.copy(
                                    step = InterviewStep.Chatting,
                                    isLoading = false,
                                    session = session,
                                    messages = initialMessages
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    step = InterviewStep.Upload,
                                    isLoading = false,
                                    session = null,
                                    errorMessage = "Lỗi khởi tạo phỏng vấn: ${error.resolveMessage()}"
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            step = InterviewStep.Upload,
                            isLoading = false,
                            session = null,
                            errorMessage = "Lỗi đánh giá CV: ${error.resolveMessage()}"
                        )
                    }
                }
            )
        }
    }

    fun sendMessage(content: String) {
        val currentState = _uiState.value
        val session = currentState.session ?: return
        if (content.isBlank() || currentState.isSending) return

        val userMessage = InterviewMessage(
            sender = ChatSender.USER,
            message = content.trim(),
            timestamp = OffsetDateTime.now()
        )

        val history = currentState.messages + userMessage

        _uiState.update {
            it.copy(
                messages = history,
                isSending = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = aiServiceRepository.sendInterviewMessage(
                history = history,
                cvAnalysisResult = session.cvAnalysisResult ?: emptyMap(),
                state = getLatestState()
            )

            result.fold(
                onSuccess = { aiReply ->
                    val updatedHistory = history + aiReply
                    
                    // Check if interview is finished based on AI response or state
                    val isFinished = aiReply.state?.get("interview_finished")?.toString()?.toBoolean() == true ||
                                   aiReply.state?.get("completed")?.toString()?.toBoolean() == true ||
                                   aiReply.message.contains("🎉") || 
                                   aiReply.message.contains("phỏng vấn hoàn tất", ignoreCase = true) ||
                                   aiReply.message.contains("kết thúc phỏng vấn", ignoreCase = true)
                    
                    if (isFinished) {
                        // Parse final result from AI message and state
                        val finalResult = parseInterviewResult(aiReply.message, aiReply.state)
                        _uiState.update {
                            it.copy(
                                messages = updatedHistory,
                                isSending = false,
                                step = InterviewStep.Result,
                                result = finalResult
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                messages = updatedHistory,
                                isSending = false
                            )
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            messages = currentState.messages,
                            isSending = false,
                            errorMessage = "Lỗi gửi tin nhắn: ${error.resolveMessage()}"
                        )
                    }
                }
            )
        }
    }

    fun finishInterview() {
        val currentState = _uiState.value
        val session = currentState.session ?: return
        if (currentState.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = aiServiceRepository.finishInterview(
                history = currentState.messages,
                cvAnalysisResult = session.cvAnalysisResult ?: emptyMap(),
                state = getLatestState()
            )
            
            result.fold(
                onSuccess = { summary ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = InterviewStep.Result,
                            result = summary
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Lỗi kết thúc phỏng vấn: ${error.resolveMessage()}"
                        )
                    }
                }
            )
        }
    }

    private fun getLatestState(): Map<String, Any>? {
        return _uiState.value.messages.lastOrNull { it.sender == ChatSender.AI }?.state
    }

    private fun parseInterviewResult(message: String, state: Map<String, Any>?): InterviewResult {
        // Try to extract info from state first (more reliable)
        val finalScore = state?.let { s ->
            s["final_score"]?.toString()?.toDoubleOrNull() ?:
            s["overall_score"]?.toString()?.toDoubleOrNull() ?:
            s["total_score"]?.toString()?.toDoubleOrNull()
        }
        
        val assessment = state?.let { s ->
            s["overall_assessment"]?.toString() ?:
            s["assessment"]?.toString() ?:
            s["evaluation"]?.toString()
        }
        
        val recommendation = state?.let { s ->
            s["recommendation"]?.toString() ?:
            s["summary"]?.toString()
        }
        
        // Fallback to parsing from message if state doesn't have the info
        val messageScore = if (finalScore == null) {
            val scoreRegex = """(?:điểm|score).*?(\d+\.?\d*)""".toRegex(RegexOption.IGNORE_CASE)
            scoreRegex.find(message)?.groupValues?.get(1)?.toDoubleOrNull()
        } else finalScore
        
        val messageAssessment = if (assessment == null) {
            val assessmentRegex = """đánh giá:\s*(.+?)(?:\n|${'$'})""".toRegex(RegexOption.IGNORE_CASE)
            assessmentRegex.find(message)?.groupValues?.get(1)?.trim()
        } else assessment
        
        // Extract breakdown from state if available
        val breakdown = state?.let { s ->
            val cvScore = s["cv_score"]?.toString()?.toDoubleOrNull()
            val interviewScores = s["interview_scores"] as? List<*>
            val avgInterviewScore = interviewScores?.mapNotNull { (it as? Number)?.toDouble() }
                ?.takeIf { it.isNotEmpty() }?.average()?.times(100) // Convert 0-1 to 0-100
                
            if (cvScore != null || avgInterviewScore != null) {
                com.example.ptitjob.data.model.InterviewBreakdown(
                    cvScore = cvScore,
                    cvWeight = 0.3,
                    interviewScore = avgInterviewScore,
                    interviewWeight = 0.7
                )
            } else null
        }
        
        // Extract improvements from state if available
        val improvements = state?.let { s ->
            (s["improvements"] as? List<*>)?.mapNotNull { item ->
                when (item) {
                    is Map<*, *> -> {
                        val area = item["area"]?.toString() ?: item["title"]?.toString() ?: ""
                        val tip = item["tip"]?.toString() ?: item["suggestion"]?.toString() ?: ""
                        if (area.isNotBlank() && tip.isNotBlank()) {
                            com.example.ptitjob.data.model.InterviewImprovement(area, tip)
                        } else null
                    }
                    is String -> {
                        // Simple string format: "Area: Tip"
                        val parts = item.split(":", limit = 2)
                        if (parts.size == 2) {
                            com.example.ptitjob.data.model.InterviewImprovement(
                                parts[0].trim(), 
                                parts[1].trim()
                            )
                        } else null
                    }
                    else -> null
                }
            }
        } ?: emptyList()
        
        return InterviewResult(
            finalScore = messageScore,
            overallAssessment = messageAssessment,
            recommendation = recommendation,
            breakdown = breakdown,
            improvements = improvements
        )
    }

    fun reset() {
        _uiState.value = InterviewUiState()
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun Throwable.resolveMessage(): String {
        return message?.takeIf { it.isNotBlank() } ?: "Không thể kết nối tới AI service. Vui lòng thử lại."
    }
}

data class InterviewUiState(
    val step: InterviewStep = InterviewStep.Upload,
    val isLoading: Boolean = false,
    val selectedFileName: String? = null,
    val session: InterviewSession? = null,
    val messages: List<InterviewMessage> = emptyList(),
    val isSending: Boolean = false,
    val result: InterviewResult? = null,
    val errorMessage: String? = null
)

enum class InterviewStep { Upload, Preparing, Chatting, Result }
