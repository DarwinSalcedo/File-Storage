package com.file.storage.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.file.storage.data.local.AppDatabase
import com.file.storage.data.local.FileDao
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "file_storage_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFileDao(db: AppDatabase): FileDao {
        return db.fileDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}

