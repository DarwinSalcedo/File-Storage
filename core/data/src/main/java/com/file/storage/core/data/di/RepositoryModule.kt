package com.file.storage.core.data.di

import com.file.storage.core.data.repository.FileRepositoryImpl
import com.file.storage.core.domain.repository.FileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFileRepository(
        impl: FileRepositoryImpl
    ): FileRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: com.file.storage.core.data.repository.SettingsRepositoryImpl
    ): com.file.storage.core.domain.repository.SettingsRepository
}
