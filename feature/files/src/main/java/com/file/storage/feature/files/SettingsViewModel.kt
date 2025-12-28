package com.file.storage.feature.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.file.storage.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val selectedCountry: StateFlow<String> = settingsRepository.selectedCountryCode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "DEFAULT"
        )

    fun onCountrySelected(code: String) {
        viewModelScope.launch {
            settingsRepository.setCountryCode(code)
        }
    }
}
