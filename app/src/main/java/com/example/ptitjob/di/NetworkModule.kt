package com.example.ptitjob.di

import android.content.Context
import android.content.SharedPreferences
import com.example.ptitjob.BuildConfig
import com.example.ptitjob.data.api.ai.AiServiceApi
import com.example.ptitjob.data.api.auth.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIMEOUT = 30L
    
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("ptitjob_prefs", Context.MODE_PRIVATE)
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(sharedPreferences: SharedPreferences): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            
            // Add access token to header if available
            val token = sharedPreferences.getString("accessToken", null)
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            
            requestBuilder.addHeader("Content-Type", "application/json")
            
            val request = requestBuilder.build()
            val response = chain.proceed(request)
            
            // Handle 401 - Token expired
            if (response.code == 401) {
                // Clear tokens and redirect to login
                sharedPreferences.edit().apply {
                    remove("accessToken")
                    remove("refreshToken")
                    remove("ptitjob_user")
                    apply()
                }
            }
            
            response
        }
    }
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @MainApiRetrofit
    fun provideMainRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @AiApiRetrofit
    fun provideAiRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.AI_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    // API Providers
    
    @Provides
    @Singleton
    fun provideAuthApi(@MainApiRetrofit retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideUserApi(@MainApiRetrofit retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideJobApi(@MainApiRetrofit retrofit: Retrofit): JobApi {
        return retrofit.create(JobApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCompanyApi(@MainApiRetrofit retrofit: Retrofit): CompanyApi {
        return retrofit.create(CompanyApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideJobApplicationApi(@MainApiRetrofit retrofit: Retrofit): JobApplicationApi {
        return retrofit.create(JobApplicationApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideResumeApi(@MainApiRetrofit retrofit: Retrofit): ResumeApi {
        return retrofit.create(ResumeApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideJobCategoryApi(@MainApiRetrofit retrofit: Retrofit): JobCategoryApi {
        return retrofit.create(JobCategoryApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideLocationApi(@MainApiRetrofit retrofit: Retrofit): LocationApi {
        return retrofit.create(LocationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAiServiceApi(@AiApiRetrofit retrofit: Retrofit): AiServiceApi {
        return retrofit.create(AiServiceApi::class.java)
    }
}
