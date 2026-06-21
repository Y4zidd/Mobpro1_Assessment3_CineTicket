@file:Suppress("SpellCheckingInspection")
package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model.TmdbMovie
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.network.TmdbRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TmdbSearchViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<TmdbMovie>>(emptyList())
    val searchResults: StateFlow<List<TmdbMovie>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = TmdbRetrofitClient.instance.searchMovies(query)
                _searchResults.value = response.results
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error occurred"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
