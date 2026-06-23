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
import androidx.core.net.toUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.network.ImgbbClient
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.BuildConfig

class TicketRepository(private val ticketDao: TicketDao) {

    private suspend fun uploadImagesToImgbb(ticket: Ticket): Ticket {
        val urls = ticket.personalPhotoUrls?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        if (urls.isEmpty() || BuildConfig.IMGBB_API_KEY.isEmpty()) return ticket
        
        val newUrls = mutableListOf<String>()

        for (url in urls) {
            if (url.startsWith("file://")) {
                val uri = url.toUri()
                val file = File(uri.path!!)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                
                val response = ImgbbClient.instance.uploadImage(BuildConfig.IMGBB_API_KEY, body)
                newUrls.add(response.data.url)
            } else {
                newUrls.add(url)
            }
        }
        return ticket.copy(personalPhotoUrls = newUrls.joinToString(","))
    }

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
        val id = UUID.randomUUID().toString()
        try {
            val uploadedTicket = uploadImagesToImgbb(ticket)
            val result = RetrofitClient.instance.addTicket(uploadedTicket)
            ticketDao.insertTicket(result.toEntity().copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
            // Offline save!
            val localTicket = ticket.copy(
                id = id,
                isSynced = false
            )
            ticketDao.insertTicket(localTicket.toEntity())
        }
    }

    suspend fun updateTicket(id: String, ticket: Ticket) {
        try {
            val uploadedTicket = uploadImagesToImgbb(ticket)
            val result = RetrofitClient.instance.updateTicket(id, uploadedTicket)
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
                val uploadedTicket = uploadImagesToImgbb(ticket)
                
                if (uploadedTicket.id?.contains("-") == true) {
                    // It was created offline (has UUID)
                    val result = RetrofitClient.instance.addTicket(uploadedTicket)
                    ticketDao.deleteById(entity.id) // delete local temporary UUID
                    ticketDao.insertTicket(result.toEntity().copy(isSynced = true))
                } else {
                    // It was updated offline
                    uploadedTicket.id?.let {
                        val result = RetrofitClient.instance.updateTicket(it, uploadedTicket)
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
