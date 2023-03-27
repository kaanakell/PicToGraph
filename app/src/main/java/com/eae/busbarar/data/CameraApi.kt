package com.eae.busbarar.data

import com.eae.busbarar.BuildConfig
import com.eae.busbarar.Constants
import com.eae.busbarar.data.model.*
import okhttp3.Call
import okhttp3.MultipartBody
import retrofit2.Response
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
        @Header("X-EAE-Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Part files: MultipartBody.Part,
    ) : ApiResponse

    @POST("registerfbt")
    suspend fun uploadToken(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("X-EAE-Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Body request: TokenRequest,
    ) : TokenResponse


    @POST("getsensorid")
    suspend fun uploadFileName(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("X-EAE-Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Body fileNameRequest: FileNameRequest,
    ) : FileNameResponse

    /*@POST("getsensortempv2")
    suspend fun uploadSensorId(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("X-EAE-Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Body request: TextRecognitionRequest,
    ) : TemperatureResponse*/

    @POST("getaggtemp")
    suspend fun uploadSensorId(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("X-EAE-Auth") header3: String = BuildConfig.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Body request: TextRecognitionRequest,
    ) : List<TemperatureAgg>

    /*@POST("getsensortempv3")
    suspend fun uploadSensorId(
        @Header("Accept") header1: String = "application/json",
        @Header("User-Agent") header2: String = "Thunder Client(https://www.thunderclient.com/)",
        @Header("X-EAE-Auth") header3: String = Constants.API_KEY,
        @Header("Connection") header5: String= "keep-alive",
        @Body request: TextRecognitionRequest,
    ) : TemperatureResponse*/

}