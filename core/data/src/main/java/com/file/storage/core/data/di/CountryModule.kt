package com.file.storage.core.data.di

import com.file.storage.core.data.manager.DynamicCountryBehaviorDelegator
import com.file.storage.core.domain.behavior.CountryBehavior
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CountryModule {

    @Binds
    @Singleton
    abstract fun bindCountryBehavior(
        impl: DynamicCountryBehaviorDelegator
    ): CountryBehavior
}
