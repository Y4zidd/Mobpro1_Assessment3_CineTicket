package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.network

import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.ImgbbResponse
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ImgbbApiService {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): ImgbbResponse
}

object ImgbbClient {
    private const val BASE_URL = "https://api.imgbb.com/"

    val instance: ImgbbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgbbApiService::class.java)
    }
}
