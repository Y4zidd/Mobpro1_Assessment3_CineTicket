package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.R
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    viewModel: TicketViewModel,
    onAddTicketClick: () -> Unit,
    onProfileClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val tickets by viewModel.tickets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser
    
    val ticketToDeleteState = remember { mutableStateOf<Ticket?>(null) }
    val ticketToDelete = ticketToDeleteState.value

    LaunchedEffect(Unit) {
        user?.email?.let { viewModel.fetchTickets(it) }
    }

    if (ticketToDelete != null) {
        AlertDialog(
            onDismissRequest = { ticketToDeleteState.value = null },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { 
                Text(
                    stringResource(
                        R.string.delete_confirm_msg, 
                        ticketToDelete.movieTitle
                    )
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        ticketToDelete.id?.let { id ->
                            viewModel.deleteTicket(id)
                        }
                        ticketToDeleteState.value = null
                    }
                ) {
                    Text(stringResource(R.string.delete_action), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { ticketToDeleteState.value = null }) {
                    Text(stringResource(R.string.cancel_action))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_home)) },
                actions = {
                    TextButton(onClick = onProfileClick) {
                        Text(stringResource(R.string.nav_profile), color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTicketClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.nav_add))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            val currentError = error
            if (isLoading && tickets.isEmpty()) {
                CircularProgressIndicator()
            } else if (currentError != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = currentError, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { user?.email?.let { viewModel.fetchTickets(it) } }) {
                        Text(stringResource(R.string.error_retry))
                    }
                }
            } else if (tickets.isEmpty()) {
                Text(stringResource(R.string.ticket_empty))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tickets) { ticket ->
                        TicketItem(
                            ticket = ticket,
                            onDeleteClick = { ticketToDeleteState.value = ticket },
                            onItemClick = { ticket.id?.let { onEditClick(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketItem(
    ticket: Ticket,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ticket.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = ticket.movieTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ticket.review,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_action),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
