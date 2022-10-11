package com.eae.busbarar.data.repository

import com.eae.busbarar.data.CameraApi
import com.eae.busbarar.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class CameraRepository @Inject constructor(private val api: CameraApi) {

    suspend fun uploadImage(file: File): ApiResponse? {
        return try {
            val response = api.uploadImage(
                files = MultipartBody.Part.createFormData(
                    "files",
                    file.name,
                    file.asRequestBody(),
                )
            )
            response
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun  uploadFileName(fileName: String?): FileNameResponse?{
        return try {
            val response = api.uploadFileName(fileNameRequest = FileNameRequest(fileName))
            response
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun uploadSensorId(request: TextRecognitionRequest): TemperatureResponse? {
        return try {
            val response = api.uploadSensorId(
                request = request
            )
            response
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}