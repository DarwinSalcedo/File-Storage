package com.file.storage.feature.files

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.file.storage.core.model.FileModel
import com.file.storage.core.model.FileType

@Composable
fun FileListScreen(
    type: FileType,
    onItemClick: (String) -> Unit,
    viewModel: FileViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    viewModel.getFilesByType(type)

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        when (val content = uiState) {
            is FileUiState.Error -> item {
                Text(
                    text = content.message ?: "An error occurred",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            FileUiState.Loading -> {
                item {
                    Text(
                        text = "No items found.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            FileUiState.NavigateToAdd -> {
                Unit
            }

            is FileUiState.Success -> {
                if (content.files.isEmpty()) {
                    item {
                        Text(
                            text = "No items found.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                items(content.files) { file ->
                    FileItemCard(file = file, onClick = { onItemClick(file.id) })
                }

            }
        }


    }
}

@Composable
fun FileItemCard(file: FileModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val title = when (file) {
                    is FileModel.Claim -> "Claim: $${file.amount}"
                    is FileModel.Document -> "Doc: ${file.docType}"
                }
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = file.status.name, style = MaterialTheme.typography.labelSmall)
            }
            if (file.note != null) {
                Text(text = file.note ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
