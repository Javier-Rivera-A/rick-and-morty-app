package com.example.rickandmortyapp.data.repository.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.rickandmortyapp.domain.repository.NetworkStatusRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Singleton
class NetworkStatusRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : NetworkStatusRepository {

    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    // callbackFlow convierte los callbacks de ConnectivityManager en un Flow
    override val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(capabilities.hasInternetAccess())
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onUnavailable() {
                trySend(false)
            }
        }

        // Estado inicial, antes de que llegue el primer callback
        trySend(isCurrentlyOnline())
        connectivityManager.registerDefaultNetworkCallback(callback)

        // Al dejar de observar el Flow se quita el callback para no dejarlo registrado
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    private fun isCurrentlyOnline(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasInternetAccess() == true
    }

    // VALIDATED indica que Android comprobó que la red realmente llega a internet
    private fun NetworkCapabilities.hasInternetAccess(): Boolean =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
