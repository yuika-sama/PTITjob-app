package com.example.ptitjob.data.api.request

import com.google.gson.annotations.SerializedName

data class InterviewMessageRequest(
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("history")
    val history: List<InterviewHistoryMessage>? = null
)

data class InterviewHistoryMessage(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String
)

data class InterviewFinishRequest(
    @SerializedName("session_id")
    val sessionId: String
)
