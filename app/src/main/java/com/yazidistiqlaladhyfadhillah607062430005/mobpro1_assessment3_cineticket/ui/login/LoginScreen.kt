package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Welcome to CineTicket",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = onSignInClick
            ) {
                Text(text = "Sign in with Google")
            }
        }
    }
}
