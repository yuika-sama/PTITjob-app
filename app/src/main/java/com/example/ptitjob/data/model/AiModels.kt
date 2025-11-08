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
    val cvAnalysisResult: Map<String, Any>? = null,
    val state: Map<String, Any>? = null,
    val cvScore: Int? = null,
    val candidateName: String? = null,
    val matchedSkills: List<String> = emptyList(),
    val initialMessage: String? = null
)

data class InterviewMessage(
    val sender: ChatSender,
    val message: String,
    val timestamp: OffsetDateTime? = null,
    val state: Map<String, Any>? = null
)

data class InterviewResult(
    val finalScore: Double? = null,
    val overallAssessment: String? = null,
    val recommendation: String? = null,
    val breakdown: InterviewBreakdown? = null,
    val improvements: List<InterviewImprovement> = emptyList()
)

data class InterviewBreakdown(
    val cvScore: Double? = null,
    val cvWeight: Double? = null,
    val interviewScore: Double? = null,
    val interviewWeight: Double? = null,
    val interviewDetails: InterviewDetails? = null
)

data class InterviewDetails(
    val totalQuestions: Int = 0,
    val averageScore: Double = 0.0,
    val minScore: Double = 0.0,
    val maxScore: Double = 0.0,
    val scoresDistribution: ScoresDistribution? = null
)

data class ScoresDistribution(
    val excellent: Int = 0,
    val good: Int = 0,
    val average: Int = 0,
    val poor: Int = 0
)

data class InterviewImprovement(
    val area: String,
    val tip: String
)

enum class ChatSender {
    AI,
    USER,
    SYSTEM
}
