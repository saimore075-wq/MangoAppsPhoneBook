package com.mangoapps.phonebook.di

import android.content.Context
import com.mangoapps.phonebook.feature.calllogs.data.CallLogsLocalDataSource
import com.mangoapps.phonebook.feature.calllogs.data.CallLogsRepositoryImpl
import com.mangoapps.phonebook.feature.calllogs.domain.repository.CallLogsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CallLogsModule {

    @Binds
    @Singleton
    abstract fun bindCallLogsRepository(impl: CallLogsRepositoryImpl): CallLogsRepository

    companion object {
        @Provides
        @Singleton
        fun provideCallLogsLocalDataSource(@ApplicationContext context: Context): CallLogsLocalDataSource =
            CallLogsLocalDataSource(context.contentResolver, context)
    }
}
