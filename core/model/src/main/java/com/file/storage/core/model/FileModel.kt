package com.file.storage.core.model

sealed class FileModel {
    abstract val id: String
    abstract val path: String
    abstract val status: FileStatus
    abstract val createdAt: Long
    abstract val note: String?

    data class Claim(
        override val id: String,
        override val path: String,
        override val status: FileStatus,
        override val createdAt: Long,
        override val note: String?,
        val amount: Double
    ) : FileModel()

    data class Document(
        override val id: String,
        override val path: String,
        override val status: FileStatus,
        override val createdAt: Long,
        override val note: String?,
        val docType: String
    ) : FileModel()
}
