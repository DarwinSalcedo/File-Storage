package com.file.storage.core.domain.usecase

import com.file.storage.core.model.FileModel
import com.file.storage.core.domain.repository.FileRepository
import com.file.storage.core.domain.behavior.CountryBehavior
import javax.inject.Inject

class SubmitFileUseCase @Inject constructor(
    private val repository: FileRepository,
    private val countryBehavior: CountryBehavior
) {
    suspend operator fun invoke(file: FileModel) {
        // Validation
        if (file is FileModel.Claim) {
            if (file.amount > countryBehavior.getMaxTransactionAmount()) {
                throw IllegalArgumentException("Amount exceeds limit for ${countryBehavior.getCountryCode()}")
            }
        } else if (file is FileModel.Document) {
             // Basic DOC validation logic or specialized
        }
        
        repository.saveFile(file)
    }
}
