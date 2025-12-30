package com.file.storage.core.data.repository

import android.net.Uri
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.file.storage.core.data.remote.FileUploader
import com.file.storage.core.data.local.FileDao
import com.file.storage.core.data.local.FileEntity
import com.file.storage.core.data.worker.SyncWorker
import com.file.storage.core.model.FileModel
import com.file.storage.core.model.FileStatus
import com.file.storage.core.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.file.storage.core.data.manager.EncryptedFileManager
import java.io.InputStream
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val localFileDao: FileDao,
    private val workManager: WorkManager,
    private val encryptedFileManager: EncryptedFileManager,
    private val remoteFileUploader: FileUploader
) : FileRepository {

    override fun getAllFiles(): Flow<List<FileModel>> {
        return localFileDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getFileById(id: String): FileModel? {
        return localFileDao.getById(id)?.toDomain()
    }

    override suspend fun getFileByPath(path: String): InputStream {
        return encryptedFileManager.getInputStream(path)
    }

    override suspend fun uploadFile(file: FileModel, inputStream: InputStream): Result<String> {
        return try {
            val url = remoteFileUploader.upload(file, inputStream)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStatus(id: String, status: FileStatus) {
        localFileDao.getById(id)?.let { entity ->
            localFileDao.insert(entity.copy(status = status.name))
        }
    }

    override suspend fun saveFile(file: FileModel) {
        val savedPath = if (file.path.startsWith("content://")) {
            try {
                encryptedFileManager.saveImage(Uri.parse(file.path))
            } catch (e: Exception) {
                file.path // Fallback or handle error
            }
        } else {
            file.path
        }

        
        val newFile = when(file) {
            is FileModel.Claim -> file.copy(path = savedPath)
            is FileModel.Document -> file.copy(path = savedPath)
        }

        val entity = newFile.toEntity()

        localFileDao.insert(entity)
        
        enqueueSync(entity.id)
    }

    override suspend fun retryUpload(id: String) {
        enqueueSync(id)
    }

    private fun enqueueSync(id: String) {
        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(workDataOf("FILE_ID" to id))
            .build()
        
        workManager.enqueueUniqueWork(
            "sync_$id",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            workRequest
        )
    }

    private fun FileEntity.toDomain(): FileModel {
        val statusEnum = try {
             FileStatus.valueOf(this.status)
        } catch (e: Exception) { FileStatus.FAILED }

        return when (this.type) {
            "CLAIM" -> FileModel.Claim(
                id = this.id,
                path = this.path,
                status = statusEnum,
                createdAt = this.createdAt,
                note = this.note,
                amount = this.amount ?: 0.0
            )
            "DOCUMENT" -> FileModel.Document(
                id = this.id,
                path = this.path,
                status = statusEnum,
                createdAt = this.createdAt,
                note = this.note,
                docType = this.docType ?: "UNKNOWN"
            )
            else -> throw IllegalStateException("Unknown file type: ${this.type}")
        }
    }

    private fun FileModel.toEntity(): FileEntity {
        val typeStr = when (this) {
            is FileModel.Claim -> "CLAIM"
            is FileModel.Document -> "DOCUMENT"
        }
        val amount = if (this is FileModel.Claim) this.amount else null
        val docType = if (this is FileModel.Document) this.docType else null

        return FileEntity(
            id = this.id,
            type = typeStr,
            status = this.status.name,
            path = this.path,
            createdAt = this.createdAt,
            note = this.note,
            amount = amount,
            docType = docType
        )
    }
}
