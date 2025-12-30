package com.file.storage.core.data.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.file.storage.core.data.local.AppDatabase
import com.file.storage.core.data.local.FileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        @Named("db_passphrase") passphrase: ByteArray
    ): AppDatabase {
        val supportFactory = SupportFactory(passphrase)
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "file_storage_db_003"
        ).openHelperFactory(supportFactory)
        .build()
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
