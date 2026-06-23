package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model

import com.google.gson.annotations.SerializedName

data class ImgbbResponse(
    val data: ImgbbData,
    val success: Boolean,
    val status: Int
)

data class ImgbbData(
    val id: String,
    val title: String?,
    val url: String,
    @SerializedName("display_url") val displayUrl: String,
    val size: Long,
    val time: Long,
    val expiration: Long,
    @SerializedName("delete_url") val deleteUrl: String
)
