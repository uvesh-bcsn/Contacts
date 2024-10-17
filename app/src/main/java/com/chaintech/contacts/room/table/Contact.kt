package com.chaintech.contacts.room.table

import android.net.Uri
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey
    val id: String,
    val name: String,
    val phoneNumber: String?,
    val photoUri: Uri?
)
