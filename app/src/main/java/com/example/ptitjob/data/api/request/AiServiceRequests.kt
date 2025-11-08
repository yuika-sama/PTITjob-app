package com.example.ptitjob.data.api.request

import com.google.gson.annotations.SerializedName

// EvaluateCvRequest removed - now using multipart form data

data class InterviewChatRequest(
    @SerializedName("history")
    val history: List<ChatMessage> = emptyList(),
    @SerializedName("cv_analysis_result")
    val cvAnalysisResult: Map<String, Any>? = null,
    @SerializedName("state")
    val state: Map<String, Any>? = null
)

data class ChatMessage(
    @SerializedName("sender")
    val sender: String, // "user" or "ai"
    @SerializedName("text")
    val text: String,
    @SerializedName("state")
    val state: Map<String, Any>? = null
)
