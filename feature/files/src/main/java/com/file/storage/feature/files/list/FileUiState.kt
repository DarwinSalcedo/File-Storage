package com.file.storage.feature.files.list

import com.file.storage.core.model.FileModel

sealed class FileUiState {
    object Loading : FileUiState()
    data class Success(val files: List<FileModel>) : FileUiState()
    data class Error(val message: String?) : FileUiState()
    object NavigateToAdd : FileUiState()
}