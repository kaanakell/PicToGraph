package com.kaan.pictograph.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaan.pictograph.data.model.TokenRequest
import com.kaan.pictograph.data.model.TokenResponse
import com.kaan.pictograph.data.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenCameraViewModel @Inject constructor(
    private val repository: CameraRepository
) : ViewModel() {

    private val _response = MutableLiveData<TokenResponse>()
    val response: LiveData<TokenResponse> = _response

    fun uploadToken(request: TokenRequest) {
        viewModelScope.launch {
            _response.postValue(repository.uploadToken(request))
        }
    }

}