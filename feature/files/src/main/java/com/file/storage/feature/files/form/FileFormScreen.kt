package com.file.storage.feature.files.form

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.file.storage.core.model.FileModel
import com.file.storage.core.model.FileStatus
import com.file.storage.core.model.FileType
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileFormScreen(
    initialType: FileType,
    fileId: String?,
    onNavigateBack: () -> Unit,
    viewModel: FileFormViewModel = hiltViewModel()
) {

    var type by remember { mutableStateOf(initialType) }
    var amount by remember { mutableStateOf("") }
    var docType by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingId by remember { mutableStateOf<String?>(null) }
    var createdAt by remember { mutableStateOf(System.currentTimeMillis()) }

    val fileForEditionById by viewModel.uiState.collectAsState()


    val imageBytes by viewModel.imageByteArray.collectAsState()

    LaunchedEffect(fileId) {
        if (fileId != null) {
            viewModel.getFileById(fileId)
        }
    }

    LaunchedEffect(fileForEditionById) {
        if (fileForEditionById is FileFormUiState.Success) {
            val file = (fileForEditionById as FileFormUiState.Success).file
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
                Log.e("TAG", "FileFormScreen: imageUri " + imageUri)

                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(
                                // This logic correctly handles both content URIs and local file paths
                                if (imageUri?.scheme == null && imageUri?.path != null) {
                                    imageBytes
                                } else {
                                    imageUri
                                }
                            )
                            .crossfade(true) // For a smooth fade-in animation
                            .build(),
                        onState = { state ->
                            // Optional: Handle loading and error states for a better UX
                            when (state) {
                                is AsyncImagePainter.State.Loading -> {
                                    Log.e(
                                        "FileFormScreen",
                                        "Coil image loading : "
                                    )

                                    //CircularProgressIndicator()
                                }

                                is AsyncImagePainter.State.Error -> {
                                    // Log the error for easier debugging
                                    Log.e(
                                        "FileFormScreen",
                                        "Coil image  error: ",
                                        state.result.throwable
                                    )
                                    // Optionally, display an error icon to the user
                                }

                                else -> {
                                    // State is either Success or Empty
                                }
                            }
                        }
                    ), contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop)// Ensures the image looks good)
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
            Text(imageUri?.toString() ?: "")
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
                enabled = imageUri != null && (if (type == FileType.CLAIM) amount.isNotEmpty() else docType.isNotEmpty())
            ) {
                Text("Save")
            }
        }
    }
}
