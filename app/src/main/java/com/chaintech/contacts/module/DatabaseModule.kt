package com.chaintech.contacts.module

import android.app.Application
import androidx.room.Room
import com.chaintech.contacts.room.database.ContactDataBase
import com.chaintech.contacts.ui.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideContactMainDatabase(app: Application) =
        Room.databaseBuilder(app, ContactDataBase::class.java, "contact-main-database").build()

    @Provides
    @Singleton
    fun provideContactDao(db: ContactDataBase) = db.contactDao()
}