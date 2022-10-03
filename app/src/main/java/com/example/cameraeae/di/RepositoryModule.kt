package com.example.cameraeae.di

import com.example.cameraeae.data.CameraApi
import com.example.cameraeae.data.repository.CameraRepository
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
    fun provideCameraRepository(api: CameraApi): CameraRepository{
        return  CameraRepository(api)
    }
}