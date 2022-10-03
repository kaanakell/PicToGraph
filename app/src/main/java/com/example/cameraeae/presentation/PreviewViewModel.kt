package com.example.cameraeae.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cameraeae.data.model.ApiResponse
import com.example.cameraeae.data.model.FileNameResponse
import com.example.cameraeae.data.repository.CameraRepository
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

    private val _success = MutableLiveData<FileNameResponse>()
    val success: LiveData<FileNameResponse> = _success

    fun uploadImage(file: File) {
        viewModelScope.launch {
            _response.postValue(repository.uploadImage(file))
        }
    }

    fun uploadFileName(fileName: String?) {
        viewModelScope.launch {
            _success.postValue(repository.uploadFileName(fileName))
        }
    }

}