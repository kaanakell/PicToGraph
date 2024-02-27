package com.kaan.pictograph.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaan.pictograph.data.model.ApiResponse
import com.kaan.pictograph.data.model.FileNameResponse
import com.kaan.pictograph.data.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraPreviewViewModel @Inject constructor(
    private val repository: CameraRepository
) : ViewModel() {

    private val _response = MutableLiveData<ApiResponse>()
    val response: LiveData<ApiResponse> = _response


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
}