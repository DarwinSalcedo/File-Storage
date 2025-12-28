package com.file.storage.core.data.repository

import android.content.Context
import androidx.core.content.edit
import com.file.storage.core.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    private val _selectedCountryCode = MutableStateFlow(prefs.getString("country_code", "DEFAULT") ?: "DEFAULT")

    override val selectedCountryCode: Flow<String> = _selectedCountryCode.asStateFlow()

    override suspend fun setCountryCode(code: String) {
        prefs.edit { putString("country_code", code) }
        _selectedCountryCode.value = code
    }
}
