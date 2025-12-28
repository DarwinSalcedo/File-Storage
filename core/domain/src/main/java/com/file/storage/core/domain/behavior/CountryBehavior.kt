package com.file.storage.core.domain.behavior

interface CountryBehavior {
    fun getCountryCode(): String
    fun validateDocumentId(docId: String): Boolean
    fun getMaxTransactionAmount(): Double
}
