package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.data

import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db.TicketDao
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db.toEntity
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db.toModel
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TicketRepository(private val ticketDao: TicketDao) {

    fun getTicketsFromDb(email: String): Flow<List<Ticket>> {
        return ticketDao.getTickets(email).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun refreshTickets(email: String) {
        try {
            val remoteTickets = RetrofitClient.instance.getTickets(email)
            ticketDao.insertTickets(remoteTickets.map { it.toEntity() })
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addTicket(ticket: Ticket) {
        val result = RetrofitClient.instance.addTicket(ticket)
        ticketDao.insertTicket(result.toEntity())
    }

    suspend fun updateTicket(id: String, ticket: Ticket) {
        val result = RetrofitClient.instance.updateTicket(id, ticket)
        ticketDao.insertTicket(result.toEntity())
    }

    suspend fun deleteTicket(id: String) {
        RetrofitClient.instance.deleteTicket(id)
        ticketDao.deleteById(id)
    }
}
