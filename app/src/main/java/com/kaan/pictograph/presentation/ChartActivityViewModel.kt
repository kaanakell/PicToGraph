package com.kaan.pictograph.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaan.pictograph.data.model.TemperatureResponseAggregation
import com.kaan.pictograph.data.model.TextRecognitionRequest
import com.kaan.pictograph.data.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartActivityViewModel @Inject constructor(
    private val repository: CameraRepository
) : ViewModel() {

    private val _sensorResponse = MutableLiveData<List<TemperatureResponseAggregation>>()
    val sensorResponse: LiveData<List<TemperatureResponseAggregation>> = _sensorResponse

    fun uploadId(request: TextRecognitionRequest) {
        viewModelScope.launch {
            _sensorResponse.postValue(repository.uploadId(request))
        }
    }
}