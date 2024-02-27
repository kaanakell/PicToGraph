package com.kaan.pictograph.data

import com.kaan.pictograph.BuildConfig
import com.kaan.pictograph.data.model.*
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CameraApi {

    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Header("Accept") header1: String = "multipart/form-data",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Part files: MultipartBody.Part,
    ) : ApiResponse

    @POST("registerfbt")
    suspend fun uploadToken(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Body request: TokenRequest,
    ) : TokenResponse


    @POST("getid")
    suspend fun uploadFileName(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Body fileNameRequest: FileNameRequest,
    ) : FileNameResponse

    @POST("getaggtempmulti2")
    suspend fun uploadId(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Body request: TextRecognitionRequest,
    ) : Array<Array<Double>>

}