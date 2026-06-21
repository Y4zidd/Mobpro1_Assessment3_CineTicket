package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.network

import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @GET("tickets")
    suspend fun getTickets(
        @Query("userEmail") email: String
    ): List<Ticket>

    @POST("tickets")
    suspend fun addTicket(
        @Body ticket: Ticket
    ): Ticket

    @PUT("tickets/{id}")
    suspend fun updateTicket(
        @Path("id") id: String,
        @Body ticket: Ticket
    ): Ticket

    @DELETE("tickets/{id}")
    suspend fun deleteTicket(
        @Path("id") id: String
    )
}

object RetrofitClient {
    private const val BASE_URL = "https://6a381fe1c105017aa639ad9a.mockapi.io/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
