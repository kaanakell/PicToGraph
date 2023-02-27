package com.eae.busbarar.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eae.busbarar.data.model.TemperatureResponse
import com.eae.busbarar.data.model.TextRecognitionRequest
import com.eae.busbarar.data.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartActivityViewModel @Inject constructor(
    private val repository: CameraRepository
) : ViewModel() {

    private val _sensorResponse = MutableLiveData<TemperatureResponse>()
    val sensorResponse: LiveData<TemperatureResponse> = _sensorResponse

    fun uploadSensorId(request: TextRecognitionRequest) {
        viewModelScope.launch {
            _sensorResponse.postValue(repository.uploadSensorId(request))
        }
    }

}