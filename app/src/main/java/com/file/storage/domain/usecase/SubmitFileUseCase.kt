package com.file.storage.domain.usecase

import com.file.storage.domain.model.FileModel
import com.file.storage.domain.repository.FileRepository
import javax.inject.Inject

class SubmitFileUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(file: FileModel) {
        repository.saveFile(file)
    }
}
