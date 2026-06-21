package com.mangoapps.phonebook.di

import android.content.Context
import com.mangoapps.phonebook.feature.sms.data.SmsLocalDataSource
import com.mangoapps.phonebook.feature.sms.data.SmsRepositoryImpl
import com.mangoapps.phonebook.feature.sms.domain.repository.SmsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SmsModule {

    @Binds
    @Singleton
    abstract fun bindSmsRepository(impl: SmsRepositoryImpl): SmsRepository

    companion object {
        @Provides
        @Singleton
        fun provideSmsLocalDataSource(@ApplicationContext context: Context): SmsLocalDataSource =
            SmsLocalDataSource(context.contentResolver)
    }
}
