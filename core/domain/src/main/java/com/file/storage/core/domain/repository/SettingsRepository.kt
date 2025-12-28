package com.file.storage.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val selectedCountryCode: Flow<String>
    suspend fun setCountryCode(code: String)
}
