package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.R
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.components.StarRatingBar
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils.NetworkConnectivityObserver
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils.ConnectivityObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    viewModel: TicketViewModel,
    onAddTicketClick: () -> Unit,
    onLogout: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val tickets by viewModel.tickets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser
    val ticketToDeleteState = remember { mutableStateOf<Ticket?>(null) }
    val ticketToDelete = ticketToDeleteState.value
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val networkStatus by NetworkConnectivityObserver(context).observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )

    var isManualRefresh by remember { mutableStateOf(false) }

    LaunchedEffect(networkStatus) {
        if (networkStatus == ConnectivityObserver.Status.Available) {
            val hasPending = tickets.any { !it.isSynced }
            if (hasPending) {
                Toast.makeText(context, "Koneksi pulih. Menyinkronkan data ke server...", Toast.LENGTH_SHORT).show()
                isManualRefresh = true
                user?.email?.let { viewModel.fetchTickets(it) }
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, "Offline / Error: $it", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        user?.email?.let { viewModel.fetchTickets(it) }
    }

    LaunchedEffect(isLoading) {
        if (isManualRefresh && !isLoading && error == null) {
            Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
            isManualRefresh = false
        }
    }

    if (ticketToDelete != null) {
        AlertDialog(
            onDismissRequest = { ticketToDeleteState.value = null },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { 
                Text(
                    stringResource(
                        R.string.delete_confirm_msg, 
                        ticketToDelete.movieTitle ?: ""
                    )
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        ticketToDelete.id?.let { id ->
                            viewModel.deleteTicket(id)
                            Toast.makeText(context, "Film berhasil dihapus!", Toast.LENGTH_SHORT).show()
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
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { 
                        isManualRefresh = true
                        user?.email?.let { viewModel.fetchTickets(it) } 
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.nav_profile),
                            tint = MaterialTheme.colorScheme.primary
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val currentError = error
                if (tickets.isEmpty()) {
                    if (currentError != null && !isLoading) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Tidak ada internet & data kosong", color = MaterialTheme.colorScheme.error)
                            Button(onClick = { user?.email?.let { viewModel.fetchTickets(it) } }) {
                                Text(stringResource(R.string.error_retry))
                            }
                        }
                    } else if (!isLoading) {
                        Text(stringResource(R.string.ticket_empty))
                    }
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
        
        if (showDialog.value) {
            ProfileDialog(
                user = user,
                onDismissRequest = { showDialog.value = false },
                onLogout = onLogout
            )
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
                .height(IntrinsicSize.Min)
                .defaultMinSize(minHeight = 110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TMDB Poster
            if (!ticket.posterUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ticket.posterUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .width(70.dp)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.broken_img)
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Poster", style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ticket.movieTitle ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!ticket.isSynced) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Menunggu Jaringan",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                // Read-only star rating
                StarRatingBar(
                    rating = ticket.rating ?: 0f,
                    onRatingChanged = {},
                    isReadOnly = true,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .height(20.dp)
                )

                if (!ticket.dateWatched.isNullOrBlank()) {
                    Text(
                        text = "Ditonton pada: ${ticket.dateWatched}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Text(
                    text = ticket.review ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                // Indicator if personal photos exist
                if (!ticket.personalPhotoUrls.isNullOrBlank()) {
                    val photoCount = ticket.personalPhotoUrls.split(",").filter { it.isNotBlank() }.size
                    if (photoCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$photoCount Foto Tersimpan",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
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

@Composable
fun ProfileDialog(
    user: com.google.firebase.auth.FirebaseUser?,
    onDismissRequest: () -> Unit,
    onLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.nav_profile)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = user?.photoUrl,
                    contentDescription = stringResource(R.string.profile_picture),
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.broken_img)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user?.displayName ?: "No Name",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user?.email ?: "No Email",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onLogout,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.profile_logout))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}
