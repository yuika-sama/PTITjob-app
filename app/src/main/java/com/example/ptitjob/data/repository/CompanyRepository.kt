package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.auth.CompanyApi
import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.CompanyDto
import com.example.ptitjob.data.api.dto.CompanyStatsDto
import com.example.ptitjob.data.api.request.CreateCompanyRequest
import com.example.ptitjob.data.api.request.UpdateCompanyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Company Repository
 * Handles all company-related operations
 */
@Singleton
class CompanyRepository @Inject constructor(
    private val companyApi: CompanyApi
) {
    
    suspend fun getAllCompanies(
        page: Int? = null,
        limit: Int? = null,
        search: String? = null,
        size: String? = null,
        status: String? = null
    ): Result<ApiResponse<List<CompanyDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = companyApi.getAllCompanies(page, limit, search, size, status)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy danh sách công ty thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getCompanyById(id: String): Result<ApiResponse<CompanyDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = companyApi.getCompanyById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thông tin công ty thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createCompany(request: CreateCompanyRequest): Result<ApiResponse<CompanyDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = companyApi.createCompany(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tạo công ty thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateCompany(id: String, request: UpdateCompanyRequest): Result<ApiResponse<CompanyDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = companyApi.updateCompany(id, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Cập nhật công ty thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteCompany(id: String): Result<ApiResponse<Unit>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = companyApi.deleteCompany(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Xóa công ty thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun searchCompanies(
        name: String? = null,
        companySize: String? = null,
        website: String? = null
    ): Result<ApiResponse<List<CompanyDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = companyApi.searchCompanies(name, companySize, website)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tìm kiếm công ty thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getCompanyStats(): Result<ApiResponse<CompanyStatsDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = companyApi.getCompanyStats()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thống kê công ty thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
