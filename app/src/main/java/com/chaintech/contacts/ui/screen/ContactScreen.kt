
package com.chaintech.contacts.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.chaintech.contacts.ContactHelper.RequestPermissionExample
import com.chaintech.contacts.room.table.Contact
import com.chaintech.contacts.ui.ContactViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ContactScreen() {
    RequestPermissionExample {
        ContactListScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen() {
    val scope = rememberCoroutineScope()
    val viewModel = viewModel<ContactViewModel>()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = viewModel.isRefresh.value,
        onRefresh = viewModel::setRefresh
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Contacts") },
                    actions = {
                        Text("Count: " + viewModel.alphabeticalContacts.value.flatMap { it.contacts }.size.toString())
                    }
                )
            }
        ) { innerPadding ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = viewModel.listState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(end = 20.dp)
                ) {
                    items(viewModel.alphabeticalContacts.value.size) { index ->
                        Box {
                            Text(
                                text = viewModel.alphabeticalContacts.value[index].letter.toString(),
                                modifier = Modifier.padding(5.dp),
                                fontWeight = FontWeight.Bold
                            )

                            Column {
                                repeat(viewModel.alphabeticalContacts.value[index].contacts.size) { position ->
                                    ContactListItem(contact = viewModel.alphabeticalContacts.value[index].contacts[position])
                                }
                            }
                        }
                    }
                }

                val alphabet = viewModel.alphabeticalContacts.value.map { it.letter }

                LaunchedEffect(viewModel.sliderPosition.floatValue) {
                    viewModel.listState.scrollToItem(viewModel.sliderPosition.floatValue.toInt())
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    alphabet.forEach { char ->
                        Text(
                            text = char.toString(),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .size(21.dp)
                                .clickable {
                                    val findPosition =
                                        viewModel.alphabeticalContacts.value.indexOfFirst { it.letter == char }
                                    scope.launch {
                                        viewModel.listState.scrollToItem(findPosition)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactListItem(contact: Contact) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp, start = 50.dp, end = 8.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            contact.photoUri?.let { uri ->
                val painter = rememberAsyncImagePainter(model = uri)
                Image(
                    painter = painter,
                    contentDescription = "Contact photo",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "No photo",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                contact.phoneNumber?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

