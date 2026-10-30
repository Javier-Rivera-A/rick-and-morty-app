package com.example.rickandmortyapp.domain.usecase.character

import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import jakarta.inject.Inject

class GetCharacterByIdUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
) {
    suspend operator fun invoke(characterId: Int): Character =
        characterRepository.getCharacterById(characterId)
}
