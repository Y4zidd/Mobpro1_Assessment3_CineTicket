package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.auth.LoginViewModel
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.navigation.NavGraph
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.navigation.Screen
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.theme.Mobpro1_Assessment3_CineTicketTheme

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobpro1_Assessment3_CineTicketTheme {
                val navController = rememberNavController()
                val user by loginViewModel.user.collectAsState()
                
                // Tentukan destinasi awal berdasarkan sesi Firebase
                val startDestination = if (user != null) Screen.Home.route else Screen.Login.route
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(
                            navController = navController,
                            loginViewModel = loginViewModel,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
