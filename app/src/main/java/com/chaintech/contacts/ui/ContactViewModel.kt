package com.chaintech.contacts.ui

import android.app.Application
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaintech.contacts.ContactHelper
import com.chaintech.contacts.ContactHelper.fetchContacts
import com.chaintech.contacts.pref.PreferencesManager
import com.chaintech.contacts.repository.ContactRepository
import com.chaintech.contacts.room.table.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val app: Application,
    private val contactRepository: ContactRepository
) : ViewModel() {
    private val pref = PreferencesManager(app)

    var alphabeticalContacts = mutableStateOf<List<ContactHelper.AlphabeticalList>>(emptyList())
    var sliderPosition = mutableFloatStateOf(0f)
    val listState = LazyListState()

    init {
        val newDate = LocalDateTime.now()
        val defaultTime = newDate.minusHours(7)
        val date = stringToLocalDateTime(pref.getData("fetchTime", localDateTimeToString(defaultTime)))
        val duration = Duration.between(date, newDate).toHours()

        if (duration > 6) {
            pref.saveData("fetchTime", localDateTimeToString(LocalDateTime.now()))
            reFetch()
        } else {
            readContact()
        }
    }

    var isRefresh = mutableStateOf(false)
        private set

    fun setRefresh() {
        isRefresh.value = true
        alphabeticalContacts.value = emptyList()
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
            alphabeticalContacts.value = createAlphabeticalListState(it)
        }.launchIn(viewModelScope)
    }

    private fun localDateTimeToString(dateTime: LocalDateTime, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return dateTime.format(formatter)
    }

    private fun stringToLocalDateTime(dateTimeString: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    fun createAlphabeticalListState(contacts: List<Contact>): List<ContactHelper.AlphabeticalList> {
        return contacts
            .filter { it.name.isNotBlank() }
            .groupBy { it.name.first().uppercaseChar() }
            .map { (letter, contacts) -> ContactHelper.AlphabeticalList(letter, contacts) }
            .sortedBy { it.letter }
    }

}