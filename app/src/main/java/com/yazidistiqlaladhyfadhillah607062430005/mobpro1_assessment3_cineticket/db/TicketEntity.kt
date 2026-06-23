package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey
    val id: String,
    val userEmail: String,
    val movieTitle: String,
    val review: String,
    val posterUrl: String,
    val personalPhotoUrls: String,
    val rating: Float,
    val dateWatched: String,
    val isSynced: Boolean
)

fun TicketEntity.toModel() = Ticket(
    id = id,
    userEmail = userEmail,
    movieTitle = movieTitle,
    review = review,
    posterUrl = posterUrl,
    personalPhotoUrls = personalPhotoUrls,
    rating = rating,
    dateWatched = dateWatched,
    isSynced = isSynced
)

fun Ticket.toEntity() = TicketEntity(
    id = id ?: "",
    userEmail = userEmail ?: "",
    movieTitle = movieTitle ?: "",
    review = review ?: "",
    posterUrl = posterUrl ?: "",
    personalPhotoUrls = personalPhotoUrls ?: personalPhotoUrl ?: "",
    rating = rating ?: 0f,
    dateWatched = dateWatched ?: "",
    isSynced = isSynced
)
