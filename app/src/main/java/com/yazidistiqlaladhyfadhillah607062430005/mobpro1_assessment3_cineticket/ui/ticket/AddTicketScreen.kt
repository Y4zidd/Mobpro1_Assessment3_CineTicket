package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.R
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.components.StarRatingBar
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils.ImageUtils
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.TmdbMovie
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils.NetworkConnectivityObserver
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils.ConnectivityObserver
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTicketScreen(
    viewModel: TicketViewModel,
    tmdbViewModel: TmdbSearchViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    var movieTitle by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(0f) }
    var posterUrl by remember { mutableStateOf("") }
    val personalPhotos = remember { mutableStateListOf<Uri>() }
    var showTmdbDialog by remember { mutableStateOf(false) }
    val previewImageUri = remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    val isLoading by viewModel.isLoading.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()
    val tickets by viewModel.tickets.collectAsState()
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser

    val networkStatus by NetworkConnectivityObserver(context).observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )
    val isOnline = networkStatus == ConnectivityObserver.Status.Available

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && personalPhotos.size < 5) {
            personalPhotos.add(uri)
        }
    }

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            Toast.makeText(context, "Berhasil menambahkan film!", Toast.LENGTH_SHORT).show()
            viewModel.resetAddSuccess()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_add)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (posterUrl.isNotEmpty()) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.broken_img)
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = movieTitle,
                        onValueChange = { movieTitle = it },
                        label = { Text(stringResource(R.string.ticket_title)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(onClick = { showTmdbDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cari Film di TMDB")
                    }
                }
            }

            if (showTmdbDialog) {
                TmdbSearchDialog(
                    viewModel = tmdbViewModel,
                    onDismiss = { showTmdbDialog = false },
                    onMovieSelected = { movie ->
                        movieTitle = movie.title
                        movie.posterPath?.let { path ->
                            posterUrl = "https://image.tmdb.org/t/p/w500$path"
                        }
                        showTmdbDialog = false
                    }
                )
            }

            Text("Rating:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            StarRatingBar(
                rating = rating,
                onRatingChanged = { rating = it }
            )

            OutlinedTextField(
                value = review,
                onValueChange = { review = it },
                label = { Text(stringResource(R.string.ticket_review)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (personalPhotos.size < 5) {
                Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.ticket_image_select))
                }
            }

            if (personalPhotos.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(personalPhotos) { photoUri ->
                        Box(modifier = Modifier.size(100.dp)) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Personal Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { previewImageUri.value = photoUri },
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.loading_img),
                                error = painterResource(id = R.drawable.broken_img)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(20.dp)
                                    .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                                    .clickable { personalPhotos.remove(photoUri) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove Photo",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!isOnline) {
                Text(
                    text = "Mode Offline: Foto dan data akan disinkronkan otomatis saat online.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    val isDuplicate = tickets.any { it.movieTitle.equals(movieTitle.trim(), ignoreCase = true) }
                    if (isDuplicate) {
                        Toast.makeText(context, "Film '$movieTitle' sudah ada di daftar Anda!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (movieTitle.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.error_title_empty), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (rating <= 0f) {
                        Toast.makeText(context, context.getString(R.string.error_rating_empty), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (review.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.error_review_empty), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (personalPhotos.size > 5) {
                        Toast.makeText(context, context.getString(R.string.error_photo_max), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (user?.email != null) {
                        coroutineScope.launch(Dispatchers.IO) {
                            val processedPhotos = personalPhotos.map { uri ->
                                if (uri.toString().startsWith("content://")) {
                                    val path = ImageUtils.copyUriToInternalStorage(context, uri)
                                    if (path.isNotEmpty()) "file://$path".toUri() else uri
                                } else {
                                    uri
                                }
                            }
                            withContext(Dispatchers.Main) {
                                val newTicket = Ticket(
                                    userEmail = user.email!!,
                                    movieTitle = movieTitle,
                                    review = review,
                                    posterUrl = posterUrl,
                                    personalPhotoUrls = processedPhotos.joinToString(",") { it.toString() },
                                    rating = rating
                                )
                                viewModel.addTicket(newTicket)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.ticket_save))
                }
            }
        }
    }

    previewImageUri.value?.let { uri ->
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { previewImageUri.value = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.8f))
                    .clickable { previewImageUri.value = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Zoomed Photo",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.broken_img)
                )
            }
        }
    }
}

@Composable
fun TmdbSearchDialog(
    viewModel: TmdbSearchViewModel,
    onDismiss: () -> Unit,
    onMovieSelected: (TmdbMovie) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchError by viewModel.error.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cari Film di TMDB") },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { 
                        query = it
                        if (it.length > 2) viewModel.searchMovies(it)
                    },
                    label = { Text("Judul Film") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (searchError != null) {
                    Text(
                        text = searchError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(searchResults) { movie ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onMovieSelected(movie) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                movie.posterPath?.let { path ->
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w92$path",
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp),
                                        placeholder = painterResource(id = R.drawable.loading_img),
                                        error = painterResource(id = R.drawable.broken_img)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(movie.title, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}
