package com.file.storage.core.domain.repository

import com.file.storage.core.model.FileModel
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    fun getAllFiles(): Flow<List<FileModel>>
    suspend fun getFileById(id: String): FileModel?
    suspend fun saveFile(file: FileModel)
    suspend fun retryUpload(id: String)
}
