package com.example.ptitjob.data.api.auth

import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.JobCategoryDto
import com.example.ptitjob.data.api.dto.LocationDto
import com.example.ptitjob.data.api.request.CreateJobCategoryRequest
import com.example.ptitjob.data.api.request.CreateLocationRequest
import com.example.ptitjob.data.api.request.UpdateJobCategoryRequest
import com.example.ptitjob.data.api.request.UpdateLocationRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Job Category API Interface
 */
interface JobCategoryApi {
    
    @GET("categories")
    suspend fun getAllCategories(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<JobCategoryDto>>>
    
    @GET("categories/{id}")
    suspend fun getCategoryById(
        @Path("id") id: String
    ): Response<ApiResponse<JobCategoryDto>>
    
    @POST("categories")
    suspend fun createCategory(
        @Body request: CreateJobCategoryRequest
    ): Response<ApiResponse<JobCategoryDto>>
    
    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body request: UpdateJobCategoryRequest
    ): Response<ApiResponse<JobCategoryDto>>
    
    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @PUT("categories/{id}/toggle-status")
    suspend fun toggleCategoryStatus(
        @Path("id") id: String
    ): Response<ApiResponse<JobCategoryDto>>
}

/**
 * Location API Interface
 */
interface LocationApi {
    
    @GET("locations")
    suspend fun getAllLocations(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("search") search: String? = null,
        @Query("country") country: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<LocationDto>>>
    
    @GET("locations/{id}")
    suspend fun getLocationById(
        @Path("id") id: String
    ): Response<ApiResponse<LocationDto>>
    
    @POST("locations")
    suspend fun createLocation(
        @Body request: CreateLocationRequest
    ): Response<ApiResponse<LocationDto>>
    
    @PUT("locations/{id}")
    suspend fun updateLocation(
        @Path("id") id: String,
        @Body request: UpdateLocationRequest
    ): Response<ApiResponse<LocationDto>>
    
    @DELETE("locations/{id}")
    suspend fun deleteLocation(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @PUT("locations/{id}/toggle-status")
    suspend fun toggleLocationStatus(
        @Path("id") id: String
    ): Response<ApiResponse<LocationDto>>
    
    @GET("locations/search")
    suspend fun searchLocations(
        @Query("q") query: String
    ): Response<ApiResponse<List<LocationDto>>>
}
