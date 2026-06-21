package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTicketScreen(
    ticketId: String,
    viewModel: TicketViewModel,
    onBackClick: () -> Unit
) {
    val tickets by viewModel.tickets.collectAsState()
    val ticket = tickets.find { it.id == ticketId }

    var movieTitle by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Inisialisasi data saat ticket ditemukan
    LaunchedEffect(ticket) {
        ticket?.let {
            movieTitle = it.movieTitle
            review = it.review
            imageUri = it.imageUrl.toUri()
        }
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) imageUri = uri
    }

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            viewModel.resetAddSuccess()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ticket_update)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cancel_action))
                    }
                }
            )
        }
    ) { innerPadding ->
        if (ticket == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.ticket_not_found))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = movieTitle,
                    onValueChange = { movieTitle = it },
                    label = { Text(stringResource(R.string.ticket_title)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text(stringResource(R.string.ticket_review)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Button(onClick = { launcher.launch("image/*") }) {
                    Text(stringResource(R.string.ticket_image_change))
                }

                imageUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val updatedTicket = ticket.copy(
                            movieTitle = movieTitle,
                            review = review,
                            imageUrl = imageUri?.toString() ?: ticket.imageUrl
                        )
                        viewModel.updateTicket(ticketId, updatedTicket)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && movieTitle.isNotBlank() && review.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(stringResource(R.string.ticket_update))
                    }
                }
            }
        }
    }
}
