package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Edit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils.NetworkConnectivityObserver
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils.ConnectivityObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTicketScreen(
    ticketId: String,
    viewModel: TicketViewModel,
    tmdbViewModel: TmdbSearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val tickets by viewModel.tickets.collectAsState()
    val ticket = tickets.find { it.id == ticketId }
    val user = FirebaseAuth.getInstance().currentUser
    
    val networkStatus by NetworkConnectivityObserver(context).observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )
    val isOnline = networkStatus == ConnectivityObserver.Status.Available

    LaunchedEffect(Unit) {
        user?.email?.let { viewModel.fetchTickets(it) }
    }

    var movieTitle by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(0f) }
    var posterUrl by remember { mutableStateOf("") }
    var dateWatched by remember { mutableStateOf("") }
    val personalPhotos = remember { mutableStateListOf<Uri>() }
    var showTmdbDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val previewImageUri = remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val showUnsavedDialog = remember { mutableStateOf(false) }

    val hasUnsavedChanges = movieTitle != (ticket?.movieTitle ?: "") ||
            review != (ticket?.review ?: "") ||
            rating != (ticket?.rating ?: 0f) ||
            dateWatched != (ticket?.dateWatched ?: "") ||
            posterUrl != (ticket?.posterUrl ?: "") ||
            personalPhotos.joinToString(",") != (ticket?.personalPhotoUrls ?: "")

    BackHandler(enabled = hasUnsavedChanges) {
        showUnsavedDialog.value = true
    }
    
    // Inisialisasi data saat ticket ditemukan
    LaunchedEffect(ticket) {
        if (ticket != null) {
            movieTitle = ticket.movieTitle ?: ""
            review = ticket.review ?: ""
            rating = ticket.rating ?: 0f
            posterUrl = ticket.posterUrl ?: ""
            dateWatched = ticket.dateWatched ?: ""
            
            personalPhotos.clear()
            ticket.personalPhotoUrls?.split(",")?.filter { url -> url.isNotEmpty() }?.forEach { url ->
                personalPhotos.add(url.toUri())
            }
        }
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, "Gagal: $it", Toast.LENGTH_SHORT).show()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && personalPhotos.size < 5) {
            personalPhotos.add(uri)
        }
    }

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            Toast.makeText(context, "Berhasil memperbarui film!", Toast.LENGTH_SHORT).show()
            viewModel.resetAddSuccess()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ticket_update)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { 
                        if (hasUnsavedChanges) {
                            showUnsavedDialog.value = true 
                        } else {
                            onBackClick() 
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showUnsavedDialog.value) {
            AlertDialog(
                onDismissRequest = { showUnsavedDialog.value = false },
                title = { Text("Perubahan Belum Disimpan") },
                text = { Text("Anda telah mengubah data film ini. Yakin ingin membuang perubahan dan kembali?") },
                confirmButton = {
                    TextButton(onClick = { 
                        showUnsavedDialog.value = false
                        Toast.makeText(context, "Perubahan dibatalkan. Film batal diperbarui.", Toast.LENGTH_SHORT).show()
                        onBackClick() 
                    }) {
                        Text("Buang Perubahan", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnsavedDialog.value = false }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }
            )
        }

        if (ticket == null && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.ticket_not_found))
            }
        } else {
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                dateWatched = formatter.format(Date(millis))
                            }
                            showDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
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
                                        .width(100.dp)
                                        .height(150.dp),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = R.drawable.loading_img),
                                    error = painterResource(id = R.drawable.broken_img)
                                )
                            }
                            
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = movieTitle,
                                    onValueChange = { movieTitle = it },
                                    label = { Text(stringResource(R.string.ticket_title)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                                )

                                Button(onClick = { showTmdbDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Cari Film di TMDB")
                                }
                            }
                        }

                        OutlinedTextField(
                            value = dateWatched,
                            onValueChange = { },
                            label = { Text("Tanggal Nonton") },
                            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                            enabled = false,
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                        )

                        OutlinedTextField(
                            value = review,
                            onValueChange = { review = it },
                            label = { Text(stringResource(R.string.ticket_review)) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("Rating", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            StarRatingBar(
                                rating = rating,
                                onRatingChanged = { rating = it }
                            )
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
            
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!isOnline) {
                Text(
                    text = "Mode Offline: Foto dan perubahan akan disinkronkan otomatis saat online.",
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
                        val isDuplicate = tickets.any { it.id != ticketId && it.movieTitle.equals(movieTitle.trim(), ignoreCase = true) }
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
                        if (user?.email != null && ticket != null) {
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
                                    val updatedTicket = ticket.copy(
                                        movieTitle = movieTitle,
                                        review = review,
                                        posterUrl = posterUrl,
                                        personalPhotoUrls = processedPhotos.joinToString(",") { it.toString() },
                                        rating = rating,
                                        dateWatched = dateWatched
                                    )
                                    viewModel.updateTicket(ticketId, updatedTicket)
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
                        Text(stringResource(R.string.ticket_update))
                    }
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
