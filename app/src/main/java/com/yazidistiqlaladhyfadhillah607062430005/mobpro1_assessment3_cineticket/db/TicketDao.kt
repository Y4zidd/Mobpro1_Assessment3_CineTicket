package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets WHERE userEmail = :email ORDER BY id DESC")
    fun getTickets(email: String): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE userEmail = :email AND isSynced = 0")
    suspend fun getUnsyncedTickets(email: String): List<TicketEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<TicketEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)

    @Query("DELETE FROM tickets WHERE id = :id")
    suspend fun deleteById(id: String)
}
