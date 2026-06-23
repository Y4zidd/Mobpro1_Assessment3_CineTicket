package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.data

import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db.TicketDao
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db.toEntity
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db.toModel
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import retrofit2.HttpException

class TicketRepository(private val ticketDao: TicketDao) {

    fun getTicketsFromDb(email: String): Flow<List<Ticket>> {
        return ticketDao.getTickets(email).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun refreshTickets(email: String) {
        try {
            val remoteTickets = RetrofitClient.instance.getTickets(email)
            // Save remote tickets (always synced = true)
            ticketDao.insertTickets(remoteTickets.map { it.toEntity().copy(isSynced = true) })
        } catch (e: HttpException) {
            if (e.code() == 404) {
                // Ignore 404 Not Found from MockAPI, it just means no data.
            } else {
                e.printStackTrace()
                throw e
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun addTicket(ticket: Ticket) {
        try {
            val result = RetrofitClient.instance.addTicket(ticket)
            ticketDao.insertTicket(result.toEntity().copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
            // Offline save!
            val localTicket = ticket.copy(
                id = UUID.randomUUID().toString(),
                isSynced = false
            )
            ticketDao.insertTicket(localTicket.toEntity())
        }
    }

    suspend fun updateTicket(id: String, ticket: Ticket) {
        try {
            val result = RetrofitClient.instance.updateTicket(id, ticket)
            ticketDao.insertTicket(result.toEntity().copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
            // Offline save!
            val localTicket = ticket.copy(id = id, isSynced = false)
            ticketDao.insertTicket(localTicket.toEntity())
        }
    }

    suspend fun deleteTicket(id: String) {
        try {
            RetrofitClient.instance.deleteTicket(id)
        } catch (e: HttpException) {
            if (e.code() != 404) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ticketDao.deleteById(id)
    }

    suspend fun syncPendingTickets(email: String) {
        val unsynced = ticketDao.getUnsyncedTickets(email)
        for (entity in unsynced) {
            try {
                val ticket = entity.toModel()
                if (ticket.id?.contains("-") == true) {
                    // It was created offline (has UUID)
                    val result = RetrofitClient.instance.addTicket(ticket)
                    ticketDao.deleteById(entity.id) // delete local temporary UUID
                    ticketDao.insertTicket(result.toEntity().copy(isSynced = true))
                } else {
                    // It was updated offline
                    ticket.id?.let {
                        val result = RetrofitClient.instance.updateTicket(it, ticket)
                        ticketDao.insertTicket(result.toEntity().copy(isSynced = true))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Still offline or failed, skip for now
            }
        }
    }
}
