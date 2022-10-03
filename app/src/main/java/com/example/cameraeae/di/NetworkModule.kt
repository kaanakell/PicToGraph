package com.example.cameraeae.di

import com.example.cameraeae.Constants
import com.example.cameraeae.data.CameraApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideCameraApi(okHttpClient: OkHttpClient): CameraApi{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CameraApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient{
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .readTimeout(120000, TimeUnit.SECONDS)
            .connectTimeout(120000, TimeUnit.SECONDS)
            .writeTimeout(120000, TimeUnit.SECONDS)
            .callTimeout(120000, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()
    }
}