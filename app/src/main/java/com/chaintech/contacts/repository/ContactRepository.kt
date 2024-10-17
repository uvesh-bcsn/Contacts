package com.chaintech.contacts.repository

import com.chaintech.contacts.room.dao.ContactDao
import com.chaintech.contacts.room.table.Contact
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {
    suspend fun insertContacts(contactList: List<Contact>) = contactDao.insertContacts(contactList)

    suspend fun clear() = contactDao.clear()

    fun readContact() = contactDao.readContact()
}