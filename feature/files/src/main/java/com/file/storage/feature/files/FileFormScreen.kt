package com.file.storage.feature.files

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.file.storage.core.model.FileModel
import com.file.storage.core.model.FileStatus
import com.file.storage.core.model.FileType
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileFormScreen(
    initialType: FileType,
    fileId: String?,
    onNavigateBack: () -> Unit,
    viewModel: FileViewModel = hiltViewModel()
) {
    var type by remember { mutableStateOf(initialType) }
    var amount by remember { mutableStateOf("") }
    var docType by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingId by remember { mutableStateOf<String?>(null) }
    var createdAt by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(fileId) {
        if (fileId != null) {
            val file = viewModel.getFileById(fileId)
            if (file != null) {
                existingId = file.id
                createdAt = file.createdAt
                note = file.note ?: ""
                imageUri = Uri.parse(file.path)
                when (file) {
                    is FileModel.Claim -> {
                        type = FileType.CLAIM
                        amount = file.amount.toString()
                    }
                    is FileModel.Document -> {
                        type = FileType.DOCUMENT
                        docType = file.docType
                    }
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                OutlinedButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Image")
                }
            }
            if (imageUri != null) {
                 OutlinedButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Image")
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            if (type == FileType.CLAIM) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = docType,
                    onValueChange = { docType = it },
                    label = { Text("Document Type (e.g. ID, Form)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Text("path+"+imageUri?.toString() ?: "")
            Button(
                onClick = {
                    val id = existingId ?: UUID.randomUUID().toString()
                    val path = imageUri?.toString() ?: ""
                    
                    val file = if (type == FileType.CLAIM) {
                        FileModel.Claim(
                            id = id,
                            path = path,
                            status = FileStatus.PENDING,
                            createdAt = createdAt,
                            note = note,
                            amount = amount.toDoubleOrNull() ?: 0.0
                        )
                    } else {
                        FileModel.Document(
                            id = id,
                            path = path,
                            status = FileStatus.PENDING,
                            createdAt = createdAt,
                            note = note,
                            docType = docType
                        )
                    }
                    viewModel.saveFile(file)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = imageUri != null && (if(type == FileType.CLAIM) amount.isNotEmpty() else docType.isNotEmpty())
            ) {
                Text("Save")
            }
        }
    }
}
