package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.auth.LoginScreen
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.auth.LoginViewModel
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.theme.Mobpro1_Assessment3_CineTicketTheme

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobpro1_Assessment3_CineTicketTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                Toast.makeText(this@MainActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}
