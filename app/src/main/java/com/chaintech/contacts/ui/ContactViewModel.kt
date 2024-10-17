package com.chaintech.contacts.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaintech.contacts.ContactHelper.fetchContacts
import com.chaintech.contacts.repository.ContactRepository
import com.chaintech.contacts.room.table.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val app: Application,
    private val contactRepository: ContactRepository
) : ViewModel() {

    var contacts = mutableStateOf<List<Contact>>(emptyList())

    init {
        reFetch()
    }

    var isRefresh = mutableStateOf(false)
        private set

    fun setRefresh() {
        isRefresh.value = true
        contacts.value = emptyList()
        reFetch().invokeOnCompletion {
            isRefresh.value = false
        }
    }

    private fun reFetch() = viewModelScope.launch(Dispatchers.IO) {
        contactRepository.clear()
        contactRepository.insertContacts(fetchContacts(app))
        readContact()
    }

    private fun readContact() {
        contactRepository.readContact().onEach {
            contacts.value = it
        }.launchIn(viewModelScope)
    }
}