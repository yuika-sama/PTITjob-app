package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.ai.AiServiceApi
import com.example.ptitjob.data.api.request.ChatMessage
import com.example.ptitjob.data.api.request.InterviewChatRequest
import com.example.ptitjob.data.model.ChatSender
import com.example.ptitjob.data.model.CvEvaluationResult
import com.example.ptitjob.data.model.InterviewMessage
import com.example.ptitjob.data.model.InterviewResult
import com.example.ptitjob.data.model.InterviewSession
import com.example.ptitjob.data.model.InterviewBreakdown
import com.example.ptitjob.data.model.InterviewDetails
import com.example.ptitjob.data.model.InterviewImprovement
import com.example.ptitjob.data.model.ScoresDistribution
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiServiceRepository @Inject constructor(
    private val api: AiServiceApi
) {

    suspend fun evaluateCv(file: File, jobDescription: String = "Software Engineer", jobSkills: String? = null): Result<CvEvaluationResult> {
        return withContext(Dispatchers.IO) {
            runCatching {
                // Debug logging
                println("🔍 AiServiceRepository - Starting CV evaluation")
                println("📁 File: ${file.name}, Size: ${file.length()} bytes")
                println("💼 Job Description: $jobDescription")
                println("🎯 Job Skills: $jobSkills")
                
                // Create multipart request body
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    file.asRequestBody("application/pdf".toMediaType())
                )
                
                val jobDescriptionBody = jobDescription.toRequestBody("text/plain".toMediaType())
                val jobSkillsBody = jobSkills?.toRequestBody("text/plain".toMediaType())
                
                println("🚀 Sending multipart request to AI service...")
                val response = api.evaluateCv(filePart, jobDescriptionBody, jobSkillsBody)
                
                println("📡 Response received - Status: ${response.code()}")
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                    println("❌ Error response: $errorBody")
                    throw IllegalStateException(errorBody ?: "CV evaluation failed with status ${response.code()}")
                }
                
                val body = response.body()
                    ?: throw IllegalStateException("AI service returned empty response")
                
                println("✅ Success response received")
                println("📄 Response body keys: ${body.keySet()}")
                
                val score = body.findFirstInt(SCORE_KEYS) ?: 0
                val summary = body.findFirstString(SUMMARY_KEYS)
                val strengths = body.findFirstStringList(STRENGTH_KEYS)
                val improvements = body.findFirstStringList(IMPROVEMENT_KEYS)
                val recommendations = body.findFirstStringList(RECOMMENDATION_KEYS)
                
                println("📊 Parsed result - Score: $score, Summary length: ${summary?.length ?: 0}")
                println("💪 Strengths: ${strengths.size} items")
                println("🔧 Improvements: ${improvements.size} items")
                println("💡 Recommendations: ${recommendations.size} items")
                
                CvEvaluationResult(
                    score = score.coerceIn(0, 100),
                    summary = summary,
                    strengths = strengths,
                    improvements = improvements,
                    recommendations = recommendations
                )
            }
        }
    }

    suspend fun startInterview(cvAnalysisResult: Map<String, Any>): Result<InterviewSession> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val request = InterviewChatRequest(
                    history = emptyList(),
                    cvAnalysisResult = cvAnalysisResult,
                    state = null
                )
                
                val response = api.interviewChat(request)
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                    throw IllegalStateException(errorBody ?: "Interview start failed with status ${response.code()}")
                }
                
                val body = response.body()
                    ?: throw IllegalStateException("AI service returned empty response")
                
                val initialMessage = body.findFirstString(RESPONSE_KEYS)
                val state = body.get("state")?.asJsonObject?.let { stateObj ->
                    stateObj.entrySet().associate { (key, value) ->
                        key to when {
                            value.isJsonPrimitive -> {
                                val primitive = value.asJsonPrimitive
                                when {
                                    primitive.isString -> primitive.asString
                                    primitive.isNumber -> primitive.asNumber
                                    primitive.isBoolean -> primitive.asBoolean
                                    else -> value.toString()
                                }
                            }
                            else -> value
                        }
                    }
                }
                
                val cvScore = cvAnalysisResult["scoring"]?.let { scoring ->
                    (scoring as? Map<*, *>)?.get("overall_score_percent")?.toString()?.toDoubleOrNull()?.toInt()
                }
                
                val candidateName = cvAnalysisResult["candidate"]?.let { candidate ->
                    (candidate as? Map<*, *>)?.get("name")?.toString()
                }
                
                val matchedSkills = cvAnalysisResult["matching"]?.let { matching ->
                    (matching as? Map<*, *>)?.get("skills_matched")?.let { skills ->
                        (skills as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    }
                } ?: emptyList()
                
                InterviewSession(
                    cvAnalysisResult = cvAnalysisResult,
                    state = state,
                    cvScore = cvScore,
                    candidateName = candidateName,
                    matchedSkills = matchedSkills,
                    initialMessage = initialMessage
                )
            }
        }
    }

    suspend fun sendInterviewMessage(
        history: List<InterviewMessage>,
        cvAnalysisResult: Map<String, Any>,
        state: Map<String, Any>?
    ): Result<InterviewMessage> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val chatHistory = history.map { msg ->
                    ChatMessage(
                        sender = when (msg.sender) {
                            ChatSender.AI -> "ai"
                            ChatSender.USER -> "user"
                            ChatSender.SYSTEM -> "system"
                        },
                        text = msg.message,
                        state = msg.state
                    )
                }
                
                val request = InterviewChatRequest(
                    history = chatHistory,
                    cvAnalysisResult = cvAnalysisResult,
                    state = state
                )
                
                val response = api.interviewChat(request)
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                    throw IllegalStateException(errorBody ?: "Interview message failed with status ${response.code()}")
                }
                
                val body = response.body()
                    ?: throw IllegalStateException("AI service returned empty response")
                
                val aiMessage = body.findFirstString(RESPONSE_KEYS)
                    ?: throw IllegalStateException("AI response is missing")
                
                val isFinished = body.findFirstBoolean(FINISHED_KEYS) ?: false
                val newState = body.get("state")?.asJsonObject?.let { stateObj ->
                    stateObj.entrySet().associate { (key, value) ->
                        key to when {
                            value.isJsonPrimitive -> {
                                val primitive = value.asJsonPrimitive
                                when {
                                    primitive.isString -> primitive.asString
                                    primitive.isNumber -> primitive.asNumber
                                    primitive.isBoolean -> primitive.asBoolean
                                    else -> value.toString()
                                }
                            }
                            else -> value
                        }
                    }
                }
                
                InterviewMessage(
                    sender = ChatSender.AI,
                    message = aiMessage,
                    timestamp = OffsetDateTime.now(),
                    state = newState
                )
            }
        }
    }

    suspend fun finishInterview(
        history: List<InterviewMessage>,
        cvAnalysisResult: Map<String, Any>,
        state: Map<String, Any>?
    ): Result<InterviewResult> {
        // Backend tự động kết thúc khi hết câu hỏi, không cần endpoint riêng
        // Chỉ cần gửi message cuối cùng và nhận kết quả
        return sendInterviewMessage(history, cvAnalysisResult, state).map { lastMessage ->
            // Parse result từ state cuối cùng nếu có
            val finalState = lastMessage.state ?: state ?: emptyMap()
            val finalScore = finalState["final_score"]?.toString()?.toDoubleOrNull()
            val overallAssessment = finalState["overall_assessment"]?.toString()
            val recommendation = finalState["recommendation"]?.toString()
            
            // Extract breakdown if available
            val breakdown = finalState["breakdown"]?.let { breakdownData ->
                when (breakdownData) {
                    is Map<*, *> -> {
                        val cvScore = breakdownData["cv_score"]?.toString()?.toDoubleOrNull()
                        val interviewScore = breakdownData["interview_score"]?.toString()?.toDoubleOrNull()
                        com.example.ptitjob.data.model.InterviewBreakdown(
                            cvScore = cvScore,
                            cvWeight = 0.3,
                            interviewScore = interviewScore,
                            interviewWeight = 0.7
                        )
                    }
                    else -> null
                }
            }
            
            // Extract improvements if available
            val improvements = finalState["improvements"]?.let { improvementsData ->
                when (improvementsData) {
                    is List<*> -> improvementsData.mapNotNull { item ->
                        when (item) {
                            is Map<*, *> -> {
                                val area = item["area"]?.toString() ?: ""
                                val tip = item["tip"]?.toString() ?: ""
                                if (area.isNotBlank() && tip.isNotBlank()) {
                                    com.example.ptitjob.data.model.InterviewImprovement(area, tip)
                                } else null
                            }
                            else -> null
                        }
                    }
                    else -> emptyList()
                }
            } ?: emptyList()
            
            InterviewResult(
                finalScore = finalScore,
                overallAssessment = overallAssessment,
                recommendation = recommendation,
                breakdown = breakdown,
                improvements = improvements
            )
        }
    }

    private fun JsonObject.findFirstObject(keys: Array<String>): JsonObject? {
        return keys.firstNotNullOfOrNull { key ->
            when {
                has(key) && get(key).isJsonObject -> getAsJsonObject(key)
                else -> null
            }
        }
    }

    private fun JsonObject.findFirstStringList(keys: Array<String>): List<String> {
        val array = keys.firstNotNullOfOrNull { key ->
            when {
                has(key) && get(key).isJsonArray -> getAsJsonArray(key)
                else -> null
            }
        }
        if (array != null) {
            val values = array.mapNotNull { element ->
                when {
                    element.isJsonPrimitive && element.asJsonPrimitive.isString -> element.asString.trim()
                    element.isJsonObject -> element.asJsonObject.findFirstString(CONTENT_KEYS)
                        ?: element.asJsonObject.findFirstString(TITLE_KEYS)
                    else -> null
                }
            }
            if (values.isNotEmpty()) return values
        }
        val single = keys.firstNotNullOfOrNull { key ->
            when {
                has(key) && get(key).isJsonPrimitive -> get(key).asString
                else -> null
            }
        }
        return single?.let { listOf(it) } ?: emptyList()
    }

    private fun JsonObject.findFirstString(keys: Array<String>): String? {
        return keys.firstNotNullOfOrNull { key ->
            when {
                has(key) && get(key).isJsonPrimitive -> get(key).asString.trim()
                else -> null
            }
        }
    }

    private fun JsonObject.findFirstInt(keys: Array<String>): Int? {
        return keys.firstNotNullOfOrNull { key ->
            when {
                has(key) && get(key).isJsonPrimitive -> {
                    val primitive = get(key).asJsonPrimitive
                    when {
                        primitive.isNumber -> runCatching { primitive.asNumber.toDouble().toInt() }.getOrNull()
                        primitive.isString -> primitive.asString.toDoubleOrNull()?.toInt()
                        else -> null
                    }
                }
                else -> null
            }
        }
    }

    private fun JsonObject.findFirstBoolean(keys: Array<String>): Boolean? {
        return keys.firstNotNullOfOrNull { key ->
            when {
                has(key) && get(key).isJsonPrimitive -> {
                    val primitive = get(key).asJsonPrimitive
                    when {
                        primitive.isBoolean -> primitive.asBoolean
                        primitive.isString -> primitive.asString.toBoolean()
                        else -> null
                    }
                }
                else -> null
            }
        }
    }

    companion object {
        private val SCORE_KEYS = arrayOf("match_score", "matchScore", "score", "overall_score", "overallScore", "overall_score_percent")
        private val SUMMARY_KEYS = arrayOf("summary", "comment", "comments", "overall_feedback", "overallFeedback")
        private val STRENGTH_KEYS = arrayOf("strengths", "strong_points", "highlights", "positives")
        private val IMPROVEMENT_KEYS = arrayOf("improvements", "weaknesses", "areas_to_improve", "improvementAreas")
        private val RECOMMENDATION_KEYS = arrayOf("recommendations", "suggestions", "tips", "next_steps")
        private val CONTENT_KEYS = arrayOf("content", "details", "description", "text")
        private val TITLE_KEYS = arrayOf("title", "name", "label", "heading")
        private val RESPONSE_KEYS = arrayOf("response", "message", "ai_message", "answer")
        private val FINISHED_KEYS = arrayOf("finished", "completed", "done", "end")
    }
}
