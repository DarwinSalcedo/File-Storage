package com.file.storage.domain.usecase

import com.file.storage.domain.model.FileModel
import com.file.storage.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(
    private val repository: FileRepository
) {
    operator fun invoke(): Flow<List<FileModel>> {
        return repository.getAllFiles()
    }
}
