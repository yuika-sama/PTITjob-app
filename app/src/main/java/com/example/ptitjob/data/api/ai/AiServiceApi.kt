package com.example.ptitjob.data.api.ai

import com.example.ptitjob.data.api.request.InterviewChatRequest
import com.example.ptitjob.data.api.request.EvaluateCvRequest
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiServiceApi {

    @POST("evaluate-cv")
    suspend fun evaluateCv(
        @Body body: EvaluateCvRequest
    ): Response<JsonObject>

    @POST("interview")
    suspend fun interviewChat(
        @Body body: InterviewChatRequest
    ): Response<JsonObject>
}
