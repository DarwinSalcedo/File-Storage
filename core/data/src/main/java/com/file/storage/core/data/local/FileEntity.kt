package com.file.storage.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "uploads")
data class FileEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val status: String,
    val path: String,
    val createdAt: Long = System.currentTimeMillis(),
    val amount: Double? = null,
    val docType: String? = null,
    val note: String? = null
)
