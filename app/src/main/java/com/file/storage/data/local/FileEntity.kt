package com.file.storage.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "uploads")
data class FileEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: String, // "CLAIM" or "DOCUMENT"
    val status: String, // "PENDING", "SYNCING", "SYNCED", "FAILED"
    val path: String, // Local absolute path
    val createdAt: Long = System.currentTimeMillis(),
    
    // Claim specific
    val amount: Double? = null,
    
    // Document specific
    val docType: String? = null,
    
    // Shared
    val note: String? = null
)
