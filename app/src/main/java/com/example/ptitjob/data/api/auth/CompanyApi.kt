package com.example.ptitjob.data.api.auth

import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.CompanyDto
import com.example.ptitjob.data.api.dto.CompanyStatsDto
import com.example.ptitjob.data.api.request.CreateCompanyRequest
import com.example.ptitjob.data.api.request.UpdateCompanyRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Company API Interface
 */
interface CompanyApi {
    
    @GET("companies")
    suspend fun getAllCompanies(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("search") search: String? = null,
        @Query("size") size: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<CompanyDto>>>
    
    @GET("companies/{id}")
    suspend fun getCompanyById(
        @Path("id") id: String
    ): Response<ApiResponse<CompanyDto>>
    
    @POST("companies")
    suspend fun createCompany(
        @Body request: CreateCompanyRequest
    ): Response<ApiResponse<CompanyDto>>
    
    @PUT("companies/{id}")
    suspend fun updateCompany(
        @Path("id") id: String,
        @Body request: UpdateCompanyRequest
    ): Response<ApiResponse<CompanyDto>>
    
    @DELETE("companies/{id}")
    suspend fun deleteCompany(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @GET("companies/search")
    suspend fun searchCompanies(
        @Query("name") name: String? = null,
        @Query("company_size") companySize: String? = null,
        @Query("website") website: String? = null
    ): Response<ApiResponse<List<CompanyDto>>>
    
    @GET("/stats")
    suspend fun getCompanyStats(): Response<ApiResponse<CompanyStatsDto>>
}
