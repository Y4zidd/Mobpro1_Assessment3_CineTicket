package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model

import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("userEmail")
    val userEmail: String? = "",
    @SerializedName("movieTitle")
    val movieTitle: String? = "",
    @SerializedName("review")
    val review: String? = "",
    @SerializedName("posterUrl")
    val posterUrl: String? = "",
    @SerializedName("personalPhotoUrls")
    val personalPhotoUrls: String? = "",
    @SerializedName("personalPhotoUrl")
    val personalPhotoUrl: String? = "",
    @SerializedName("rating")
    val rating: Float? = 0f,
    @Transient // Do not send to MockAPI
    val isSynced: Boolean = true
)
