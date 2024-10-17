package com.chaintech.contacts.room.convertor

import android.net.Uri
import androidx.room.TypeConverter

class Converter {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}