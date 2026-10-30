package com.example.rickandmortyapp.features.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.domain.usecase.network.ObserveNetworkStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class NetworkStatusViewModel @Inject constructor(
    observeNetworkStatusUseCase: ObserveNetworkStatusUseCase
) : ViewModel() {

    // Arranca en true para no mostrar "Sin conexión" un instante al abrir la aplicación
    val isOnline: StateFlow<Boolean> = observeNetworkStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )
}
