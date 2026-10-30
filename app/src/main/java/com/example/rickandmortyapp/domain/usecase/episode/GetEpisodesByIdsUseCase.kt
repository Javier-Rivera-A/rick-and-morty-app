package com.example.rickandmortyapp.domain.usecase.episode

import com.example.rickandmortyapp.domain.model.Episode
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import jakarta.inject.Inject

class GetEpisodesByIdsUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
) {
    suspend operator fun invoke(episodeIds: List<Int>): List<Episode> {
        // Si no hay episodios no vale la pena hacer la petición
        if (episodeIds.isEmpty()) return emptyList()
        return characterRepository.getEpisodesByIds(episodeIds)
    }
}
