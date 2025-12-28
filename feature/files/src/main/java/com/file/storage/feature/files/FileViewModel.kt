package com.file.storage.feature.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.file.storage.core.model.FileModel
import com.file.storage.core.model.FileStatus
import com.file.storage.core.model.FileType
import com.file.storage.core.domain.usecase.GetFileByIdUseCase
import com.file.storage.core.domain.usecase.GetFilesUseCase
import com.file.storage.core.domain.usecase.SubmitFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class FileViewModel @Inject constructor(
    private val getFilesUseCase: GetFilesUseCase,
    private val submitFileUseCase: SubmitFileUseCase,
    private val getFileByIdUseCase: GetFileByIdUseCase
) : ViewModel() {

    val _uiState: MutableStateFlow<FileUiState> = MutableStateFlow(FileUiState.Loading)
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

    fun getFileById(fileId: String): FileModel? {
        viewModelScope.launch {
            getFileByIdUseCase(fileId)
        }
        return null // TODO: Fix this, suspend function cannot return immediately like this if used synchronously in UI
    }


    fun saveFile(fileModel: FileModel) {
        viewModelScope.launch {
            submitFileUseCase(fileModel)
        }
    }

    fun test() {
        viewModelScope.launch {
            repeat(10) {
                val file = FileModel.Claim(
                    id = UUID.randomUUID().toString(),
                    path = "path/test/ramdom",
                    status = FileStatus.PENDING,
                    createdAt = System.currentTimeMillis(),
                    note = "",
                    amount = Random.nextDouble(100.0, 1000.0)
                )

                val d = FileModel.Document(
                    id = UUID.randomUUID().toString(),
                    path = "path/test/ramdom",
                    status = FileStatus.PENDING,
                    createdAt = System.currentTimeMillis(),
                    docType = "TEST",
                    note = ""
                )
            }

        }
    }
}
