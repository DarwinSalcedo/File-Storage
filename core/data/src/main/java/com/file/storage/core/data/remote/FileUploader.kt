package com.file.storage.core.data.remote

import com.file.storage.core.model.FileModel
import java.io.InputStream

interface FileUploader {
    suspend fun upload(file: FileModel, inputStream: InputStream): String
}