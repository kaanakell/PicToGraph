package com.example.cameraeae.data

import com.example.cameraeae.Constants
import com.example.cameraeae.data.model.ApiResponse
import com.example.cameraeae.data.model.FileNameRequest
import com.example.cameraeae.data.model.FileNameResponse
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
        @Header("X-EAE-Auth") header3: String = Constants.API_KEY,
        @Header("Accept-Encoding") header4: String = "gzip, deflate, br",
        @Header("Connection") header5: String= "keep-alive",
        @Part files: MultipartBody.Part,
    ) : ApiResponse


    @POST("getsensorid")
    suspend fun uploadFileName(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("X-EAE-Auth") header3: String = Constants.API_KEY,
        @Header("Accept-Encoding") header4: String = "gzip, deflate, br",
        @Header("Connection") header5: String= "keep-alive",
        @Body fileNameRequest: FileNameRequest,
    ) : FileNameResponse

}