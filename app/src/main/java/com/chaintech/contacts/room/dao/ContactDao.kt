package com.chaintech.contacts.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chaintech.contacts.room.table.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contactList: List<Contact>)

    @Query("DELETE FROM Contact")
    suspend fun clear()

    @Query("SELECT * FROM Contact ORDER BY name ASC LIMIT :limit OFFSET :offSet")
    fun readContact(limit: Int, offSet: Int): List<Contact>

}