package com.eae.busbarar.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.eae.busbarar.data.CameraApi
import com.eae.busbarar.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
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

    suspend fun uploadSensorId(request: TextRecognitionRequest): List<TemperatureResponseAggregation> {
        Log.e(TAG, "uploadSensorId: start request")
        return try {
            val response = api.uploadSensorId(request = request)
            Log.e(TAG, "uploadSensorId: received response=$response")
            val resultList = mutableListOf<TemperatureResponseAggregation>()
            for (i in response.indices) {
                val item = TemperatureResponseAggregation(
                    datetime = response[i][0].toInt(),
                    pred = response[i][1].toInt(),
                    sensor = response[i][2].toInt(),
                    average = response[i][3].toFloat(),
                    min = response[i][4].toFloat(),
                    max = response[i][5].toFloat(),
                    open = response[i][6].toFloat(),
                    close = response[i][7].toFloat()
                )
                resultList.add(item)
            }
            Log.e(TAG, "uploadSensorId: end request, parsed response=$resultList")
            resultList
        } catch (e: Exception) {
            Log.e(TAG, "uploadSensorId: exception occurred: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun  uploadToken(request: TokenRequest): TokenResponse?{
        return try {
            val response = api.uploadToken(request = request)
            response
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }
}