package com.example.rickandmortyapp.domain.usecase.location

import com.example.rickandmortyapp.domain.model.Location
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import jakarta.inject.Inject

class GetLocationByIdUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
) {
    suspend operator fun invoke(locationId: Int): Location =
        characterRepository.getLocationById(locationId)
}
