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
    val imageUrl: String
)

fun TicketEntity.toModel() = Ticket(
    id = id,
    userEmail = userEmail,
    movieTitle = movieTitle,
    review = review,
    imageUrl = imageUrl
)

fun Ticket.toEntity() = TicketEntity(
    id = id ?: "",
    userEmail = userEmail,
    movieTitle = movieTitle,
    review = review,
    imageUrl = imageUrl
)
