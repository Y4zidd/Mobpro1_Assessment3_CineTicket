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
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.R
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTicketScreen(
    viewModel: TicketViewModel,
    onBackClick: () -> Unit
) {
    var movieTitle by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
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
                title = { Text(stringResource(R.string.nav_add)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cancel_action))
                    }
                }
            )
        }
    ) { innerPadding ->
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
                Text(stringResource(R.string.ticket_image_select))
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
                    if (user?.email != null && movieTitle.isNotBlank() && review.isNotBlank()) {
                        val newTicket = Ticket(
                            userEmail = user.email!!,
                            movieTitle = movieTitle,
                            review = review,
                            imageUrl = imageUri?.toString() ?: ""
                        )
                        viewModel.addTicket(newTicket)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && movieTitle.isNotBlank() && review.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.ticket_save))
                }
            }
        }
    }
}
