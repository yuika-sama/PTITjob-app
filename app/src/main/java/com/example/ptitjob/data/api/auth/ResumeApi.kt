package com.example.ptitjob.data.api.auth

import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.ResumeDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Resume API Interface
 */
interface ResumeApi {
    
    @GET("resumes")
    suspend fun getAllResumes(): Response<ApiResponse<List<ResumeDto>>>
    
    @GET("resumes/{id}")
    suspend fun getResumeById(
        @Path("id") id: Int
    ): Response<ApiResponse<ResumeDto>>
    
    @Multipart
    @POST("resumes")
    suspend fun uploadResume(
        @Part file: MultipartBody.Part,
        @Part("user_id") userId: Int
    ): Response<ApiResponse<ResumeDto>>
    
    @DELETE("resumes/{id}")
    suspend fun deleteResume(
        @Path("id") id: Int
    ): Response<ApiResponse<Unit>>
    
    @GET("resumes/user/{userId}")
    suspend fun getResumesByUser(
        @Path("userId") userId: Int
    ): Response<ApiResponse<List<ResumeDto>>>
    
    @GET("resumes/{id}/download")
    @Streaming
    suspend fun downloadResume(
        @Path("id") id: Int
    ): Response<ResponseBody>
}
