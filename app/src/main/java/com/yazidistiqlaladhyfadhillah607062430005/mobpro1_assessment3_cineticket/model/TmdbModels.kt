package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model

import com.google.gson.annotations.SerializedName

data class TmdbSearchResponse(
    @SerializedName("results")
    val results: List<TmdbMovie>
)

data class TmdbMovie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?
)
