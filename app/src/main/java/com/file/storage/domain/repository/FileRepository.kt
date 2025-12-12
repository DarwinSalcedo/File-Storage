package com.file.storage.domain.repository

import com.file.storage.domain.model.FileModel
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    fun getAllFiles(): Flow<List<FileModel>>
    suspend fun saveFile(file: FileModel)
    suspend fun retryUpload(id: String)
}
