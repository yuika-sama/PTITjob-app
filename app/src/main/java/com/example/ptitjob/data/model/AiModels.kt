package com.example.ptitjob.data.model

import java.time.OffsetDateTime

/**
 * Domain models returned by AI service endpoints. They intentionally capture
 * only the fields the mobile app needs to render the AI experiences while
 * remaining flexible with optional properties for backend evolution.
 */
data class CvEvaluationResult(
    val score: Int,
    val summary: String?,
    val strengths: List<String>,
    val improvements: List<String>,
    val recommendations: List<String>
)

data class InterviewSession(
    val sessionId: String,
    val cvScore: Int?,
    val candidateName: String?,
    val matchedSkills: List<String>,
    val initialMessage: String?
)

data class InterviewMessage(
    val sessionId: String,
    val sender: ChatSender,
    val message: String,
    val timestamp: OffsetDateTime? = null
)

data class InterviewResult(
    val finalScore: Int?,
    val cvScore: Int?,
    val interviewScore: Int?,
    val improvements: List<Pair<String, String>>,
    val summary: String?
)

enum class ChatSender {
    AI,
    USER,
    SYSTEM
}
