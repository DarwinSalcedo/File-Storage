package com.file.storage.data.repository

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.file.storage.data.local.FileDao
import com.file.storage.data.local.FileEntity
import com.file.storage.domain.model.FileModel
import com.file.storage.domain.model.FileStatus
import com.file.storage.domain.repository.FileRepository
import com.file.storage.worker.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val dao: FileDao,
    private val workManager: WorkManager
) : FileRepository {

    override fun getAllFiles(): Flow<List<FileModel>> {
        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveFile(file: FileModel) {
        val entity = file.toEntity()
        // Save to DB
        dao.insert(entity)
        
        // Enqueue Work
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
