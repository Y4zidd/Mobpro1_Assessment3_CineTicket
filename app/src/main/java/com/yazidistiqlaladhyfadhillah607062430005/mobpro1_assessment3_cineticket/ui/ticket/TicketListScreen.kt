package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    viewModel: TicketViewModel,
    onAddTicketClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val tickets by viewModel.tickets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        user?.email?.let { viewModel.fetchTickets(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My CineTickets") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Text("Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTicketClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Ticket")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator() // Task 3.3: Global Loading Indicator
            } else if (error != null) {
                // Task 3.4: Error handling
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { user?.email?.let { viewModel.fetchTickets(it) } }) {
                        Text("Retry")
                    }
                }
            } else if (tickets.isEmpty()) {
                Text("No tickets found. Add your first movie diary!")
            } else {
                // Task 3.2: LazyColumn with Coil
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tickets) { ticket ->
                        TicketItem(ticket)
                    }
                }
            }
        }
    }
}

@Composable
fun TicketItem(ticket: Ticket) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(100.dp)
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
                modifier = Modifier.fillMaxHeight(),
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
        }
    }
}
