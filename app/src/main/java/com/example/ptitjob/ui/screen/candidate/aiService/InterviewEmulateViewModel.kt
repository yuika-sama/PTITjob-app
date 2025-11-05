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
import java.time.ZoneOffset
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

            val result = aiServiceRepository.startInterview(file)
            file.delete()

            result.fold(
                onSuccess = { session ->
                    val initialMessages = buildList {
                        session.initialMessage?.takeIf { msg -> msg.isNotBlank() }?.let { msg ->
                            add(
                                InterviewMessage(
                                    sessionId = session.sessionId,
                                    sender = ChatSender.AI,
                                    message = msg,
                                    timestamp = OffsetDateTime.now(ZoneOffset.UTC)
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
                            errorMessage = error.resolveMessage()
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
            sessionId = session.sessionId,
            sender = ChatSender.USER,
            message = content.trim(),
            timestamp = OffsetDateTime.now(ZoneOffset.UTC)
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
                sessionId = session.sessionId,
                message = userMessage.message,
                history = history
            )

            result.fold(
                onSuccess = { aiReply ->
                    _uiState.update {
                        it.copy(
                            messages = it.messages + aiReply,
                            isSending = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            messages = currentState.messages,
                            isSending = false,
                            errorMessage = error.resolveMessage()
                        )
                    }
                }
            )
        }
    }

    fun finishInterview() {
        val session = _uiState.value.session ?: return
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = aiServiceRepository.finishInterview(session.sessionId)
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
                            errorMessage = error.resolveMessage()
                        )
                    }
                }
            )
        }
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
