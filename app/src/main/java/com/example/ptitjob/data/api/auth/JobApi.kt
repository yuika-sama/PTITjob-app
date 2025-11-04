package com.example.ptitjob.data.api.auth

import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.JobDto
import com.example.ptitjob.data.api.request.CreateJobRequest
import com.example.ptitjob.data.api.request.UpdateJobRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Job API Interface
 */
interface JobApi {
    
    @GET("jobs")
    suspend fun getAllJobs(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("search") search: String? = null,
        @Query("jobType") jobType: String? = null,
        @Query("location") location: String? = null,
        @Query("status") status: String? = null,
        @Query("experienceLevel") experienceLevel: String? = null
    ): Response<ApiResponse<List<JobDto>>>
    
    @GET("jobs/{id}")
    suspend fun getJobById(
        @Path("id") id: String
    ): Response<ApiResponse<JobDto>>
    
    @POST("jobs")
    suspend fun createJob(
        @Body request: CreateJobRequest
    ): Response<ApiResponse<JobDto>>
    
    @PUT("jobs/{id}")
    suspend fun updateJob(
        @Path("id") id: String,
        @Body request: UpdateJobRequest
    ): Response<ApiResponse<JobDto>>
    
    @DELETE("jobs/{id}")
    suspend fun deleteJob(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @GET("jobs/search")
    suspend fun searchJobs(
        @Query("title") title: String? = null,
        @Query("company_id") companyId: Int? = null,
        @Query("location_id") locationId: Int? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("job_type") jobType: String? = null,
        @Query("experience_level") experienceLevel: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ApiResponse<List<JobDto>>>
    
    @GET("jobs")
    suspend fun getFeaturedJobs(): Response<ApiResponse<List<JobDto>>>
}
