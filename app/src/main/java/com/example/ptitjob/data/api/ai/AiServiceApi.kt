package com.example.ptitjob.data.api.ai

import com.example.ptitjob.data.api.request.InterviewChatRequest
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AiServiceApi {

    @GET("health")
    suspend fun healthCheck(): Response<JsonObject>

    @Multipart
    @POST("evaluate-cv")
    suspend fun evaluateCv(
        @Part file: MultipartBody.Part,
        @Part("jd_text") jobDescription: RequestBody,
        @Part("jd_skills") jobSkills: RequestBody?
    ): Response<JsonObject>

    @POST("interview")
    suspend fun interviewChat(
        @Body body: InterviewChatRequest
    ): Response<JsonObject>
}
