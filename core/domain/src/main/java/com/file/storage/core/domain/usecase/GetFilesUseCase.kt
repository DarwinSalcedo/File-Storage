package com.file.storage.core.domain.usecase

import com.file.storage.core.model.FileModel
import com.file.storage.core.model.FileType
import com.file.storage.core.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(
    private val repository: FileRepository
) {
    operator fun invoke(type: FileType? = null): Flow<List<FileModel>> {

        return repository.getAllFiles()
            .map { files ->
                files.filter { file ->
                    when (type) {
                        FileType.CLAIM -> file is FileModel.Claim
                        FileType.DOCUMENT -> file is FileModel.Document
                        else -> true
                    }
                }
            }.catch {
                throw it
            }

    }

}
