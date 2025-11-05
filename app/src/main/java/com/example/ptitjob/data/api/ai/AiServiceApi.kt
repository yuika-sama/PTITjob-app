package com.example.ptitjob.data.api.ai

import com.example.ptitjob.data.api.request.InterviewFinishRequest
import com.example.ptitjob.data.api.request.InterviewMessageRequest
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AiServiceApi {

    @Multipart
    @POST("cv/evaluate")
    suspend fun evaluateCv(
        @Part parts: List<MultipartBody.Part>
    ): Response<JsonObject>

    @Multipart
    @POST("interview/start")
    suspend fun startInterview(
        @Part parts: List<MultipartBody.Part>
    ): Response<JsonObject>

    @POST("interview/message")
    suspend fun sendInterviewMessage(
        @Body body: InterviewMessageRequest
    ): Response<JsonObject>

    @POST("interview/finish")
    suspend fun finishInterview(
        @Body body: InterviewFinishRequest
    ): Response<JsonObject>
}
