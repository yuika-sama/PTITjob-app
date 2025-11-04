package com.example.ptitjob.data.api.auth

import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.JobApplicationDto
import com.example.ptitjob.data.api.request.BulkUpdateStatusRequest
import com.example.ptitjob.data.api.request.CreateJobApplicationRequest
import com.example.ptitjob.data.api.request.UpdateApplicationStatusRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Job Application API Interface
 */
interface JobApplicationApi {
    
    @GET("applications")
    suspend fun getAllApplications(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("jobTitle") jobTitle: String? = null,
        @Query("company") company: String? = null
    ): Response<ApiResponse<List<JobApplicationDto>>>
    
    @GET("applications/{id}")
    suspend fun getApplicationById(
        @Path("id") id: String
    ): Response<ApiResponse<JobApplicationDto>>
    
    @POST("applications")
    suspend fun createApplication(
        @Body request: CreateJobApplicationRequest
    ): Response<ApiResponse<JobApplicationDto>>
    
    @PUT("applications/{id}/status")
    suspend fun updateApplicationStatus(
        @Path("id") id: String,
        @Body request: UpdateApplicationStatusRequest
    ): Response<ApiResponse<JobApplicationDto>>
    
    @DELETE("applications/{id}")
    suspend fun deleteApplication(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @GET("applications/user/{userId}")
    suspend fun getApplicationsByUser(
        @Path("userId") userId: String
    ): Response<ApiResponse<List<JobApplicationDto>>>
    
    @GET("applications/job/{jobId}")
    suspend fun getApplicationsByJob(
        @Path("jobId") jobId: String
    ): Response<ApiResponse<List<JobApplicationDto>>>
    
    @PUT("applications/bulk-status")
    suspend fun bulkUpdateStatus(
        @Body request: BulkUpdateStatusRequest
    ): Response<ApiResponse<Unit>>
}
