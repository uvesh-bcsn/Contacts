package com.chaintech.contacts.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.chaintech.contacts.room.convertor.Converter
import com.chaintech.contacts.room.dao.ContactDao
import com.chaintech.contacts.room.table.Contact

@Database(
    entities = [Contact::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class ContactDataBase: RoomDatabase() {
    abstract fun contactDao(): ContactDao
}