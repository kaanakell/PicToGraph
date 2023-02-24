package com.eae.busbarar.di

import com.eae.busbarar.data.CameraApi
import com.eae.busbarar.data.repository.CameraRepository
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