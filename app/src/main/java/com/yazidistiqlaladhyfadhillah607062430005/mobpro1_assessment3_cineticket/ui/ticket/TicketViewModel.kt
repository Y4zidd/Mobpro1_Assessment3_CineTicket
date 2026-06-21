package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.data.TicketRepository
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.db.AppDatabase
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.Ticket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TicketViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TicketRepository
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _addSuccess = MutableStateFlow(false)
    val addSuccess = _addSuccess.asStateFlow()

    private val _userEmail = MutableStateFlow("")

    init {
        val dao = AppDatabase.getDatabase(application).ticketDao()
        repository = TicketRepository(dao)
    }

    val tickets: StateFlow<List<Ticket>> = _userEmail
        .flatMapLatest { email ->
            if (email.isBlank()) flowOf(emptyList())
            else repository.getTicketsFromDb(email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchTickets(email: String) {
        _userEmail.value = email
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshTickets(email)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Offline mode: Gagal sinkronisasi"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTicket(ticket: Ticket) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _addSuccess.value = false
            try {
                repository.addTicket(ticket)
                _addSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Gagal menambah tiket"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTicket(id: String, ticket: Ticket) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _addSuccess.value = false
            try {
                repository.updateTicket(id, ticket)
                _addSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Gagal memperbarui tiket"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTicket(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteTicket(id)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Gagal menghapus tiket"
            }
        }
    }

    fun resetAddSuccess() {
        _addSuccess.value = false
    }
}
