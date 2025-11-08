package com.example.ptitjob.data.api.request

import com.google.gson.annotations.SerializedName

data class EvaluateCvRequest(
    @SerializedName("file")
    val fileBase64: String,
    @SerializedName("jd_text") 
    val jobDescription: String = "Software Engineer",
    @SerializedName("jd_skills")
    val jobSkills: String? = null
)

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
