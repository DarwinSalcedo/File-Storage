package com.file.storage.feature.files.form

import com.file.storage.core.model.FileModel

sealed class FileFormUiState {
    object Init : FileFormUiState()
    object Loading : FileFormUiState()
    data class Success(val file: FileModel) : FileFormUiState()
    data class Error(val message: String?) : FileFormUiState()
}
