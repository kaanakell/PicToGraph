package com.example.cameraeae.data.repository

import com.example.cameraeae.data.CameraApi
import com.example.cameraeae.data.model.ApiResponse
import com.example.cameraeae.data.model.FileNameRequest
import com.example.cameraeae.data.model.FileNameResponse
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
}