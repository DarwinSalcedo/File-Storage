package com.file.storage.core.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideMasterKey(@ApplicationContext context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    @Provides
    @Singleton
    @Named("encrypted_prefs")
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context,
        masterKey: MasterKey
    ): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    @Named("db_passphrase")
    fun provideDbPassphrase(@Named("encrypted_prefs") prefs: SharedPreferences): ByteArray {
        val existingKey = prefs.getString("db_key", null)
        return if (existingKey != null) {
            Base64.getDecoder().decode(existingKey)
        } else {
            val bytes = ByteArray(32)
            SecureRandom().nextBytes(bytes)
            val encodedConfig = Base64.getEncoder().encodeToString(bytes)
            prefs.edit().putString("db_key", encodedConfig).apply()
            bytes
        }
    }
}
