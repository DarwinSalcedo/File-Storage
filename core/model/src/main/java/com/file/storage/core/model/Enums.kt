package com.file.storage.core.model

enum class FileStatus {
    PENDING,
    SYNCING,
    SYNCED,
    FAILED
}

enum class FileType {
    CLAIM,
    DOCUMENT
}
