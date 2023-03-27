package com.eae.busbarar.di

import com.eae.busbarar.Constants
import com.eae.busbarar.data.CameraApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val contentType = "application/json".toMediaType()
    @Provides
    @Singleton
    fun provideCameraApi(okHttpClient: OkHttpClient): CameraApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(CameraApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        val trustManager = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManager, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager[0] as X509TrustManager)
            .hostnameVerifier {_ ,_  -> true }
            .readTimeout(120000, TimeUnit.SECONDS)
            .connectTimeout(120000, TimeUnit.SECONDS)
            .writeTimeout(120000, TimeUnit.SECONDS)
            .callTimeout(120000, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()
    }
}