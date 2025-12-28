package com.file.storage.feature.files.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.file.storage.core.domain.usecase.GetFilesUseCase
import com.file.storage.core.model.FileModel
import com.file.storage.core.model.FileType
import com.file.storage.feature.files.list.FileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getFilesUseCase: GetFilesUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<FileUiState> = MutableStateFlow(FileUiState.Loading)
    val uiState: StateFlow<FileUiState> = _uiState

    fun getFilesByType(type: FileType) {
        viewModelScope.launch {
            getFilesUseCase(type)
                .map { files ->
                    files.filter { file ->
                        when (type) {
                            FileType.CLAIM -> file is FileModel.Claim
                            FileType.DOCUMENT -> file is FileModel.Document
                        }
                    }
                }.collect { _uiState.value = FileUiState.Success(it) }
        }
    }

}