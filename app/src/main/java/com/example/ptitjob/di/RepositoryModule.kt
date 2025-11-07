package com.example.ptitjob.di

import android.content.SharedPreferences
import com.example.ptitjob.data.api.auth.*
import com.example.ptitjob.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        sharedPreferences: SharedPreferences
    ): AuthRepository {
        return AuthRepository(authApi, sharedPreferences)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi): UserRepository {
        return UserRepository(userApi)
    }
    
    @Provides
    @Singleton
    fun provideJobRepository(jobApi: JobApi): JobRepository {
        return JobRepository(jobApi)
    }
    
    @Provides
    @Singleton
    fun provideCompanyRepository(companyApi: CompanyApi): CompanyRepository {
        return CompanyRepository(companyApi)
    }
    
    @Provides
    @Singleton
    fun provideJobApplicationRepository(jobApplicationApi: JobApplicationApi): JobApplicationRepository {
        return JobApplicationRepository(jobApplicationApi)
    }
    
    @Provides
    @Singleton
    fun provideResumeRepository(resumeApi: ResumeApi): ResumeRepository {
        return ResumeRepository(resumeApi)
    }
    
    @Provides
    @Singleton
    fun provideJobCategoryRepository(jobCategoryApi: JobCategoryApi): JobCategoryRepository {
        return JobCategoryRepository(jobCategoryApi)
    }
    
    @Provides
    @Singleton
    fun provideLocationRepository(locationApi: LocationApi): LocationRepository {
        return LocationRepository(locationApi)
    }
}
