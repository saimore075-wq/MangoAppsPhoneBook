package com.mangoapps.phonebook.di

import android.content.ContentResolver
import android.content.Context
import com.mangoapps.phonebook.feature.contacts.data.ContactsRepositoryImpl
import com.mangoapps.phonebook.feature.contacts.data.local.ContactsLocalDataSource
import com.mangoapps.phonebook.feature.contacts.data.remote.ContactsApi
import com.mangoapps.phonebook.feature.contacts.domain.repository.ContactsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ContactsModule {

    @Binds
    @Singleton
    abstract fun bindContactsRepository(impl: ContactsRepositoryImpl): ContactsRepository

    companion object {
        @Provides
        @Singleton
        fun provideContactsApi(retrofit: Retrofit): ContactsApi =
            retrofit.create(ContactsApi::class.java)

        @Provides
        @Singleton
        fun provideContactsLocalDataSource(@ApplicationContext context: Context): ContactsLocalDataSource =
            ContactsLocalDataSource(context.contentResolver)
    }
}
