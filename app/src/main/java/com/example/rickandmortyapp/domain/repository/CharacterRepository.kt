package com.example.rickandmortyapp.domain.repository

import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.model.CharacterFilter
import com.example.rickandmortyapp.domain.model.CharacterPage
import com.example.rickandmortyapp.domain.model.Episode
import com.example.rickandmortyapp.domain.model.Location

interface CharacterRepository {
    suspend fun getCharacters(page: Int, filter: CharacterFilter): CharacterPage
    suspend fun getCharacterById(id: Int): Character
    suspend fun getEpisodesByIds(ids: List<Int>): List<Episode>
    suspend fun getLocationById(id: Int): Location
}
