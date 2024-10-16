package com.chaintech.contacts.ui

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract

object ContactHelper {
    data class Contact(
        val id: String,
        val name: String,
        val phoneNumber: String?,
        val photoUri: Uri?
    )

    // Fetch contacts from the device
    fun fetchContacts(context: Context): List<Contact> {
        val contactsList = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, null
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
                var phoneNumber: String? = null

                if (hasPhoneNumber) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    if (phoneCursor != null && phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER))
                        phoneCursor.close()
                    }
                }

                val photoUri: Uri? = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))?.let { Uri.parse(it) }

                contactsList.add(Contact(id, name, phoneNumber, photoUri))
            }
            cursor.close()
        }

        contactsList.sortBy { it.name }
        return contactsList
    }
}