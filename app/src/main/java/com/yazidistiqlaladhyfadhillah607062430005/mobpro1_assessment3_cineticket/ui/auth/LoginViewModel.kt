package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sessionManager = SessionManager(application)

    private val _user = MutableStateFlow(auth.currentUser)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    sessionManager.saveSession(
                        email = firebaseUser.email ?: "",
                        name = firebaseUser.displayName ?: ""
                    )
                }
                _user.value = firebaseUser
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
            sessionManager.clearSession()
            _user.value = null
        }
    }

    fun clearError() {
        _error.value = null
    }
}
