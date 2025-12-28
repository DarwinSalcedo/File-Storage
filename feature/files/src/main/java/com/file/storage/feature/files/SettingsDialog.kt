package com.file.storage.feature.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val countries = listOf("DEFAULT", "US", "ES")

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Select Country Behavior")
        },
        text = {
            Column {
                countries.forEach { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onCountrySelected(country) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (country == selectedCountry),
                            onClick = null
                        )
                        Text(
                            text = country,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("OK")
            }
        }
    )
}
