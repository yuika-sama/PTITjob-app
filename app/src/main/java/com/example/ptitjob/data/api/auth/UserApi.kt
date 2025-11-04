package com.example.ptitjob.data.api.auth

import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.UserDto
import com.example.ptitjob.data.api.dto.UserStatsDto
import com.example.ptitjob.data.api.request.CreateUserRequest
import com.example.ptitjob.data.api.request.UpdateUserRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * User API Interface
 */
interface UserApi {
    
    @GET("users")
    suspend fun getAllUsers(): Response<ApiResponse<List<UserDto>>>
    
    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: String
    ): Response<ApiResponse<UserDto>>
    
    @POST("users")
    suspend fun createUser(
        @Body request: CreateUserRequest
    ): Response<ApiResponse<UserDto>>
    
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UpdateUserRequest
    ): Response<ApiResponse<UserDto>>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @GET("users")
    suspend fun getUsersPaginated(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<UserDto>>>
    
    @GET("users/search")
    suspend fun searchUsers(
        @Query("email") email: String? = null,
        @Query("full_name") fullName: String? = null,
        @Query("role") role: String? = null,
        @Query("company_id") companyId: Int? = null
    ): Response<ApiResponse<List<UserDto>>>
    
    @GET("users/profile")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>
    
    @PUT("users/profile")
    suspend fun updateCurrentUser(
        @Body request: UpdateUserRequest
    ): Response<ApiResponse<UserDto>>
    
    @GET("users/stats")
    suspend fun getUserStats(): Response<ApiResponse<UserStatsDto>>
    
    @PATCH("users/{id}/toggle-status")
    suspend fun toggleUserStatus(
        @Path("id") id: String
    ): Response<ApiResponse<UserDto>>
}
