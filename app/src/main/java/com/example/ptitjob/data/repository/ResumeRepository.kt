package com.example.ptitjob.data.repository

import com.example.ptitjob.data.api.auth.ResumeApi
import com.example.ptitjob.data.api.dto.ApiResponse
import com.example.ptitjob.data.api.dto.ResumeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resume Repository
 * Handles all resume-related operations
 */
@Singleton
class ResumeRepository @Inject constructor(
    private val resumeApi: ResumeApi
) {
    
    suspend fun getAllResumes(): Result<ApiResponse<List<ResumeDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = resumeApi.getAllResumes()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy danh sách CV thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getResumeById(id: Int): Result<ApiResponse<ResumeDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = resumeApi.getResumeById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy thông tin CV thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun uploadResume(file: File, userId: Int): Result<ApiResponse<ResumeDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("resume", file.name, requestBody)
                
                val response = resumeApi.uploadResume(filePart, userId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tải lên CV thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteResume(id: Int): Result<ApiResponse<Unit>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = resumeApi.deleteResume(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Xóa CV thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getResumesByUser(userId: Int): Result<ApiResponse<List<ResumeDto>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = resumeApi.getResumesByUser(userId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Lấy CV của người dùng thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun downloadResume(id: Int): Result<ResponseBody> {
        return withContext(Dispatchers.IO) {
            try {
                val response = resumeApi.downloadResume(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.message() ?: "Tải xuống CV thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
