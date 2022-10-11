package com.eae.busbarar.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eae.busbarar.data.model.ApiResponse
import com.eae.busbarar.data.model.FileNameResponse
import com.eae.busbarar.data.model.TemperatureResponse
import com.eae.busbarar.data.model.TextRecognitionRequest
import com.eae.busbarar.data.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val repository: CameraRepository
) : ViewModel() {

    private val _response = MutableLiveData<ApiResponse>()
    val response: LiveData<ApiResponse> = _response

    private val _sensorResponse = MutableLiveData<TemperatureResponse>()
    val sensorResponse: LiveData<TemperatureResponse> = _sensorResponse

    private val _fileNameResponse = MutableLiveData<FileNameResponse>()
    val fileNameResponse: LiveData<FileNameResponse> = _fileNameResponse


    fun uploadImage(file: File) {
        viewModelScope.launch {
            _response.postValue(repository.uploadImage(file))
        }
    }

    fun uploadFileName(fileName: String?) {
        viewModelScope.launch {
            _fileNameResponse.postValue(repository.uploadFileName(fileName))
        }
    }

    fun uploadSensorId(request: TextRecognitionRequest){
        viewModelScope.launch {
            _sensorResponse.postValue(repository.uploadSensorId(request))
        }
    }

}