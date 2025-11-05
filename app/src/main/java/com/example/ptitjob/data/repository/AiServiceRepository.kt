package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.ai.AiServiceApi
import com.example.ptitjob.data.api.request.InterviewHistoryMessage
import com.example.ptitjob.data.api.request.InterviewMessageRequest
import com.example.ptitjob.data.api.request.InterviewFinishRequest
import com.example.ptitjob.data.model.ChatSender
import com.example.ptitjob.data.model.CvEvaluationResult
import com.example.ptitjob.data.model.InterviewMessage
import com.example.ptitjob.data.model.InterviewResult
import com.example.ptitjob.data.model.InterviewSession
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiServiceRepository @Inject constructor(
    private val api: AiServiceApi
) {

    suspend fun evaluateCv(file: File): Result<CvEvaluationResult> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = api.evaluateCv(createFileParts(file))
                handleResponse(response) { payload ->
                    val score = payload.findFirstInt(SCORE_KEYS) ?: 0
                    val summary = payload.findFirstString(SUMMARY_KEYS)
                    val strengths = payload.findFirstStringList(STRENGTH_KEYS)
                    val improvements = payload.findFirstStringList(IMPROVEMENT_KEYS)
                    val recommendations = payload.findFirstStringList(RECOMMENDATION_KEYS)
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
    }

    suspend fun startInterview(file: File): Result<InterviewSession> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = api.startInterview(createFileParts(file))
                handleResponse(response) { payload ->
                    val sessionId = payload.findFirstString(SESSION_ID_KEYS)
                        ?: error("Session id is missing in AI response")
                    val cvScore = payload.findFirstInt(CV_SCORE_KEYS)
                    val candidateName = payload.findFirstString(CANDIDATE_NAME_KEYS)
                    val matchedSkills = payload.findFirstStringList(MATCHED_SKILLS_KEYS)
                    val message = payload.findFirstString(INITIAL_MESSAGE_KEYS)
                    InterviewSession(
                        sessionId = sessionId,
                        cvScore = cvScore,
                        candidateName = candidateName,
                        matchedSkills = matchedSkills,
                        initialMessage = message
                    )
                }
            }
        }
    }

    suspend fun sendInterviewMessage(
        sessionId: String,
        message: String,
        history: List<InterviewMessage>
    ): Result<InterviewMessage> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val request = InterviewMessageRequest(
                    sessionId = sessionId,
                    message = message,
                    history = history.map {
                        InterviewHistoryMessage(
                            role = when (it.sender) {
                                ChatSender.AI -> "assistant"
                                ChatSender.USER -> "user"
                                ChatSender.SYSTEM -> "system"
                            },
                            content = it.message
                        )
                    }
                )
                val response = api.sendInterviewMessage(request)
                handleResponse(response) { payload ->
                    val reply = payload.findFirstString(AI_MESSAGE_KEYS)
                        ?: error("AI reply is missing")
                    val timestamp = payload.findFirstString(TIMESTAMP_KEYS)?.let(::parseTimestamp)
                    InterviewMessage(
                        sessionId = sessionId,
                        sender = ChatSender.AI,
                        message = reply,
                        timestamp = timestamp
                    )
                }
            }
        }
    }

    suspend fun finishInterview(sessionId: String): Result<InterviewResult> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = api.finishInterview(InterviewFinishRequest(sessionId))
                handleResponse(response) { payload ->
                    val finalScore = payload.findFirstInt(FINAL_SCORE_KEYS)
                    val cvScore = payload.findFirstInt(CV_SCORE_KEYS)
                    val interviewScore = payload.findFirstInt(INTERVIEW_SCORE_KEYS)
                    val improvements = payload.findImprovements()
                    val summary = payload.findFirstString(SUMMARY_KEYS)
                    InterviewResult(
                        finalScore = finalScore,
                        cvScore = cvScore,
                        interviewScore = interviewScore,
                        improvements = improvements,
                        summary = summary
                    )
                }
            }
        }
    }

    private fun createFileParts(file: File): List<MultipartBody.Part> {
        val mediaType = "application/pdf".toMediaType()
        val requestBody: RequestBody = file.asRequestBody(mediaType)
        return FILE_PART_NAMES.distinct().map { name ->
            MultipartBody.Part.createFormData(name, file.name, requestBody)
        }
    }

    private inline fun <T> handleResponse(
        response: Response<JsonObject>,
        crossinline mapper: (JsonObject) -> T
    ): T {
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
            throw IllegalStateException(errorBody ?: response.message())
        }
        val body = response.body()
            ?: throw IllegalStateException("AI service returned empty body")
        val payload = body.unwrapData()
        return mapper(payload)
    }

    private fun JsonObject.unwrapData(): JsonObject {
        val dataNode = findFirstObject(DATA_KEYS)
        return dataNode ?: this
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

    private fun JsonObject.findImprovements(): List<Pair<String, String>> {
        val candidates = listOfNotNull(
            findFirstArray(IMPROVEMENT_KEYS),
            findFirstArray(RECOMMENDATION_KEYS)
        )
        val merged = mutableListOf<Pair<String, String>>()
        for (array in candidates) {
            array.forEach { element ->
                when {
                    element.isJsonObject -> {
                        val obj = element.asJsonObject
                        val title = obj.findFirstString(TITLE_KEYS) ?: obj.findFirstString(AREA_KEYS) ?: ""
                        val detail = obj.findFirstString(CONTENT_KEYS) ?: obj.findFirstString(SUMMARY_KEYS) ?: ""
                        if (title.isNotBlank() || detail.isNotBlank()) {
                            merged += title.trim() to detail.trim()
                        }
                    }
                    element.isJsonPrimitive && element.asJsonPrimitive.isString -> {
                        val value = element.asString.trim()
                        if (value.isNotBlank()) merged += value to ""
                    }
                }
            }
        }
        if (merged.isNotEmpty()) return merged
        return findFirstStringList(IMPROVEMENT_KEYS).map { it to "" }
    }

    private fun JsonObject.findFirstArray(keys: Array<String>): JsonArray? {
        return keys.firstNotNullOfOrNull { key ->
            when {
                has(key) && get(key).isJsonArray -> getAsJsonArray(key)
                else -> null
            }
        }
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

    private fun parseTimestamp(raw: String): OffsetDateTime? {
        return runCatching { OffsetDateTime.parse(raw) }.getOrNull()
    }

    companion object {
        private val FILE_PART_NAMES = listOf("file", "cv", "resume", "cv_file")
        private val DATA_KEYS = arrayOf("data", "result", "payload")
        private val SCORE_KEYS = arrayOf("match_score", "matchScore", "score", "overall_score", "overallScore")
        private val SUMMARY_KEYS = arrayOf("summary", "comment", "comments", "overall_feedback", "overallFeedback")
        private val STRENGTH_KEYS = arrayOf("strengths", "strong_points", "highlights", "positives")
        private val IMPROVEMENT_KEYS = arrayOf("improvements", "weaknesses", "areas_to_improve", "improvementAreas")
        private val RECOMMENDATION_KEYS = arrayOf("recommendations", "suggestions", "tips", "next_steps")
        private val CONTENT_KEYS = arrayOf("content", "details", "description", "text")
        private val TITLE_KEYS = arrayOf("title", "name", "label", "heading")
        private val AREA_KEYS = arrayOf("area", "topic", "category")
        private val SESSION_ID_KEYS = arrayOf("session_id", "sessionId", "id")
        private val CV_SCORE_KEYS = arrayOf("cv_score", "cvScore")
        private val MATCHED_SKILLS_KEYS = arrayOf("matched_skills", "skills", "top_skills")
        private val INITIAL_MESSAGE_KEYS = arrayOf("initial_message", "message", "ai_message", "greeting")
        private val CANDIDATE_NAME_KEYS = arrayOf("candidate_name", "candidateName", "user_name", "userName")
        private val AI_MESSAGE_KEYS = arrayOf("ai_message", "message", "response", "answer")
        private val TIMESTAMP_KEYS = arrayOf("timestamp", "created_at", "createdAt")
        private val FINAL_SCORE_KEYS = arrayOf("final_score", "finalScore", "overall_score", "score")
        private val INTERVIEW_SCORE_KEYS = arrayOf("interview_score", "interviewScore")
    }
}
