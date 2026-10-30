package com.example.rickandmortyapp.domain.usecase.character

import com.example.rickandmortyapp.domain.model.CharacterFilter
import com.example.rickandmortyapp.domain.model.CharacterPage
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import jakarta.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
) {
    // Se quitan los espacios del nombre para que " rick " y "rick" den el mismo resultado
    suspend operator fun invoke(page: Int, filter: CharacterFilter): CharacterPage =
        characterRepository.getCharacters(page, filter.copy(name = filter.name.trim()))
}
