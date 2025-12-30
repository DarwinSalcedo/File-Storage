package com.file.storage.core.data.manager

import android.content.Context
import android.net.Uri
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedFileManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val masterKey: MasterKey
) {

    fun saveImage(uri: Uri): String {
        val filename = "${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, filename)

        if (file.exists()) file.delete()

       /* val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()*/

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }

    fun getInputStream(path: String): InputStream {
        val file = File(path)
        /*val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()*/
        return file.inputStream()
    }
}
