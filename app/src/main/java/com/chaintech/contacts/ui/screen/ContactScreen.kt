@file:OptIn(ExperimentalFoundationApi::class)

package com.chaintech.contacts.ui.screen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.chaintech.contacts.ContactHelper.RequestPermissionExample
import com.chaintech.contacts.room.table.Contact
import com.chaintech.contacts.ui.ContactViewModel
import kotlinx.coroutines.launch

@Composable
fun ContactScreen() {
    RequestPermissionExample {
        ContactListScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen() {
    val viewModel = viewModel<ContactViewModel>()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = viewModel.isRefresh.value,
        onRefresh = viewModel::setRefresh
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Contacts List") }
                )
            }
        ) { innerPadding ->
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = viewModel.listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 30.dp)
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

                val interaction = remember { MutableInteractionSource() }
                val isDragging by interaction.collectIsDraggedAsState()
                val alphabet = viewModel.alphabeticalContacts.value.map { it.letter }

                /*LaunchedEffect(viewModel.listState) {
                    snapshotFlow { viewModel.listState.firstVisibleItemIndex }
                        .collect {
                            viewModel.sliderPosition.floatValue = it.toFloat()
                        }
                }*/

                LaunchedEffect(viewModel.sliderPosition.floatValue) {
                    viewModel.listState.scrollToItem(viewModel.sliderPosition.floatValue.toInt())
                }
                Slider(
                    interactionSource = interaction,
                    value = viewModel.sliderPosition.floatValue,
                    onValueChange = {
                        viewModel.sliderPosition.floatValue = it
                    },
                    valueRange = 0f..(if (alphabet.size > 0) alphabet.size.toFloat() - 1 else 0f),
                    track = {
                        Box(
                            modifier = Modifier
                                .offset(y = -3.dp)
                                .height(10.dp)
                                .background(Color.LightGray)
                                .fillMaxWidth()
                        )
                    },
                    thumb = {
                        if (isDragging) {
                            Box(
                                modifier = Modifier
                                    .offset(y = 40.dp)
                                    .size(50.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(
                                            topStart = 15.dp,
                                            topEnd = 0.dp,
                                            bottomStart = 15.dp,
                                            bottomEnd = 15.dp
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = alphabet[viewModel.sliderPosition.floatValue.toInt()].toString(),
                                    color = Color.White,
                                    modifier = Modifier.rotate(270f)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .height(10.dp)
                                .width(50.dp)
                        )
                    },
                    modifier = Modifier
//                        .background(Color.Blue)
                        .graphicsLayer {
                            rotationZ = 90f
                            translationX = (this.size.width / 2) * 0.97f
                        }
                        .align(Alignment.CenterEnd)
//                        .background(Color.LightGray)
                )
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

