package com.example.ptitjob.data.api.request

import com.google.gson.annotations.SerializedName

/**
 * Create Company Request
 */
data class CreateCompanyRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("website")
    val website: String? = null,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("phone")
    val phone: String? = null,
    
    @SerializedName("address")
    val address: String? = null,
    
    @SerializedName("logo_url")
    val logoUrl: String? = null
)

/**
 * Update Company Request
 */
data class UpdateCompanyRequest(
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("website")
    val website: String? = null,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("phone")
    val phone: String? = null,
    
    @SerializedName("address")
    val address: String? = null,
    
    @SerializedName("logo_url")
    val logoUrl: String? = null
)
