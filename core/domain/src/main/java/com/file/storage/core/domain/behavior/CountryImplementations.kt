package com.file.storage.core.domain.behavior

import javax.inject.Inject

class DefaultCountryBehavior @Inject constructor() : CountryBehavior {
    override fun getCountryCode() = "DEFAULT"
    override fun validateDocumentId(docId: String) = docId.isNotEmpty()
    override fun getMaxTransactionAmount() = 100.0
}

class USCountryBehavior @Inject constructor() : CountryBehavior {
    override fun getCountryCode() = "US"
    override fun validateDocumentId(docId: String) = docId.matches(Regex("\\d{3}-\\d{2}-\\d{4}")) // SSN format
    override fun getMaxTransactionAmount() = 10000.0
}

class ESCountryBehavior @Inject constructor() : CountryBehavior {
    override fun getCountryCode() = "ES"
    override fun validateDocumentId(docId: String) = docId.matches(Regex("[0-9]{8}[A-Z]")) // DNI format
    override fun getMaxTransactionAmount() = 3000.0
}
