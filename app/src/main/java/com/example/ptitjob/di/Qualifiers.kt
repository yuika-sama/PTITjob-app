package com.example.ptitjob.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AiApiRetrofit
