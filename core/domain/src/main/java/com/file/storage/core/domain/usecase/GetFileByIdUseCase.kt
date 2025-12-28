package com.file.storage.core.domain.usecase

import com.file.storage.core.model.FileModel
import com.file.storage.core.domain.repository.FileRepository
import javax.inject.Inject

class GetFileByIdUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(id: String): FileModel? {
        return repository.getFileById(id)
    }
}
