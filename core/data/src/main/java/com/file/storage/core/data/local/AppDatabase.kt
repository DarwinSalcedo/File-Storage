package com.file.storage.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
}
