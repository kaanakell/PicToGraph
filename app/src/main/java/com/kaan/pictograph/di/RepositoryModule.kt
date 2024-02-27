package com.kaan.pictograph.di

import com.kaan.pictograph.data.CameraApi
import com.kaan.pictograph.data.repository.CameraRepository
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
    fun provideCameraRepository(api: CameraApi): CameraRepository {
        return  CameraRepository(api)
    }
}