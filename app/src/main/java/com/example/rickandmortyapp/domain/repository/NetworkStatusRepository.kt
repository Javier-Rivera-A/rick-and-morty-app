package com.example.rickandmortyapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkStatusRepository {
    // Emite true cuando el dispositivo tiene conexión a internet y false cuando la pierde
    val isOnline: Flow<Boolean>
}
