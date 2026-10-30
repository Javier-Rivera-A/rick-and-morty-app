package com.example.rickandmortyapp.domain.usecase.network

import com.example.rickandmortyapp.domain.repository.NetworkStatusRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveNetworkStatusUseCase @Inject constructor(
    private val networkStatusRepository: NetworkStatusRepository
) {
    operator fun invoke(): Flow<Boolean> = networkStatusRepository.isOnline
}
