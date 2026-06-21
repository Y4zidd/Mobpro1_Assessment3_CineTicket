package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(user) {
        if (user != null) onLoginSuccess()
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = stringResource(R.string.login_tagline))
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                handleGoogleSignIn(context, viewModel)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(stringResource(R.string.login_google))
                    }
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
private suspend fun handleGoogleSignIn(context: Context, viewModel: LoginViewModel) {
    val credentialManager = CredentialManager.create(context)
    val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    val webClientId = if (resId != 0) context.getString(resId) else ""

    if (webClientId.isEmpty()) {
        Log.e("Auth", "Missing WEB_CLIENT_ID. Ensure google-services.json is present.")
        Toast.makeText(context, context.getString(R.string.login_error_config), Toast.LENGTH_SHORT).show()
        return
    }

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val result = credentialManager.getCredential(context, request)
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
        val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        viewModel.signInWithCredential(credential)
    } catch (e: GetCredentialException) {
        Log.e("Auth", "Credential Manager Error: ${e.message}")
    } catch (e: Exception) {
        Log.e("Auth", "Unknown Error: ${e.message}")
    }
}
