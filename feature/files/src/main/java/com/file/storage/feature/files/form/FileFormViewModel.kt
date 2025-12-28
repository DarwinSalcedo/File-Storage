package com.file.storage.feature.files.form

import android.util.Log
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.file.storage.core.domain.repository.FileRepository
import com.file.storage.core.domain.usecase.GetFileByIdUseCase
import com.file.storage.core.domain.usecase.GetFilesUseCase
import com.file.storage.core.domain.usecase.SubmitFileUseCase
import com.file.storage.core.model.FileModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileFormViewModel @Inject constructor(
    private val submitFileUseCase: SubmitFileUseCase,
    private val getFileByIdUseCase: GetFileByIdUseCase,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<FileFormUiState> = MutableStateFlow(FileFormUiState.Init)
    val uiState: StateFlow<FileFormUiState> = _uiState

    // ... otros StateFlows
    private val _imageByteArray = MutableStateFlow<ByteArray?>(null)
    val imageByteArray: StateFlow<ByteArray?> = _imageByteArray

// ...

    fun loadEncryptedImage(path: String) {
        viewModelScope.launch(Dispatchers.IO) { // Usa un hilo de fondo para I/O
            try {
                // 1. Obtener el InputStream descifrado
                val inputStream = fileRepository.getFileByPath(path) // Asumiendo una función en tu repositorio

                // 2. Leer los bytes del InputStream
                val bytes = inputStream.use { it.readBytes() }

                // 3. Actualizar el StateFlow
                _imageByteArray.value = bytes

            } catch (e: Exception) {
                Log.e("ViewModel", "Error al cargar la imagen cifrada", e)
                _imageByteArray.value = null // Limpiar en caso de error
            }
        }
    }

    fun getFileById(fileId: String) {
        _uiState.value = FileFormUiState.Loading
        viewModelScope.launch {
            val result = getFileByIdUseCase(fileId)
            if (result != null) {
                loadEncryptedImage(result.path)
                _uiState.value = FileFormUiState.Success(result)

            } else {
                _uiState.value = FileFormUiState.Error("File not found")
            }
        }
    }


    fun saveFile(fileModel: FileModel) {
        viewModelScope.launch {
            submitFileUseCase(fileModel)
        }
    }

}