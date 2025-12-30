package com.file.storage.core.data.remote

import com.file.storage.core.model.FileModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import javax.inject.Inject

class FirebaseUploaderImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) : FileUploader {

    override suspend fun upload(file: FileModel, inputStream: InputStream): String {
        //  val storageRef = storage.reference.child("uploads/${file.id}")

        // Upload file stream
        // storageRef.putStream(inputStream).await()
        val downloadUrl = "tesry"//storageRef.downloadUrl.await().toString()

        // Create metadata map
        val fileData = hashMapOf(
            "id" to file.id,
            "createdAt" to file.createdAt,
            "note" to file.note
        )

        // Add additional fields based on type
        when (file) {
            is FileModel.Claim -> {
                fileData["type"] = "claim"
                fileData["amount"] = file.amount
            }

            is FileModel.Document -> {
                fileData["type"] = "document"
                fileData["docType"] = file.docType
            }
        }

        firestore.collection("files").document(file.id).set(fileData).await()

        return downloadUrl
    }
}

