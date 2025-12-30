package com.file.storage.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.file.storage.core.domain.repository.FileRepository
import com.file.storage.core.model.FileStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: FileRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val fileId = inputData.getString("FILE_ID") ?: return Result.failure()

        return try {
            val file = repository.getFileById(fileId) ?: return Result.failure()
            // If already synced or not ready, maybe skip? For now, just upload.
            
            val inputStream = repository.getFileByPath(file.path)
            
            val result = repository.uploadFile(file, inputStream)
            
            if (result.isSuccess) {
                repository.updateStatus(fileId, FileStatus.SYNCED)
                Result.success()
            } else {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
             if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
