@file:Suppress("SpellCheckingInspection")
package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.network

import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.BuildConfig
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.TmdbSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): TmdbSearchResponse
}

object TmdbRetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/"

    val instance: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }
}
