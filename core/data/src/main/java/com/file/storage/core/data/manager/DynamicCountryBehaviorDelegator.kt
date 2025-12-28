package com.file.storage.core.data.manager

import com.file.storage.core.domain.behavior.CountryBehavior
import com.file.storage.core.domain.behavior.DefaultCountryBehavior
import com.file.storage.core.domain.behavior.ESCountryBehavior
import com.file.storage.core.domain.behavior.USCountryBehavior
import com.file.storage.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicCountryBehaviorDelegator @Inject constructor(
    private val usBehavior: USCountryBehavior,
    private val esBehavior: ESCountryBehavior,
    private val defaultBehavior: DefaultCountryBehavior,
    private val settingsRepository: SettingsRepository
) : CountryBehavior {

    private fun getCurrentBehavior(): CountryBehavior {
        val code = runBlocking { settingsRepository.selectedCountryCode.first() }
        return when (code) {
            "US" -> usBehavior
            "ES" -> esBehavior
            else -> defaultBehavior
        }
    }

    override fun getCountryCode(): String {
        return getCurrentBehavior().getCountryCode()
    }

    override fun validateDocumentId(docId: String): Boolean {
        return getCurrentBehavior().validateDocumentId(docId)
    }

    override fun getMaxTransactionAmount(): Double {
        return getCurrentBehavior().getMaxTransactionAmount()
    }
}
