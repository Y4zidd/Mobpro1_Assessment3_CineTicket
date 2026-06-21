package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

object ConnectivityObserver {
    enum class Status {
        Available, Unavailable
    }
}

class NetworkConnectivityObserver(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityObserver.Status.Available) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectivityObserver.Status.Unavailable) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectivityObserver.Status.Unavailable) }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            val currentNetwork = connectivityManager.activeNetwork
            if (currentNetwork == null) {
                launch { send(ConnectivityObserver.Status.Unavailable) }
            } else {
                launch { send(ConnectivityObserver.Status.Available) }
            }
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}
