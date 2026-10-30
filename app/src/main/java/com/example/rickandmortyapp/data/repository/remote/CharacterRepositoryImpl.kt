package com.example.rickandmortyapp.data.repository.remote

import com.example.rickandmortyapp.data.remote.ApiService
import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.model.CharacterFilter
import com.example.rickandmortyapp.domain.model.CharacterGender
import com.example.rickandmortyapp.domain.model.CharacterPage
import com.example.rickandmortyapp.domain.model.CharacterStatus
import com.example.rickandmortyapp.domain.model.Episode
import com.example.rickandmortyapp.domain.model.Location
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import retrofit2.HttpException

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CharacterRepository {

    override suspend fun getCharacters(page: Int, filter: CharacterFilter): CharacterPage {
        return try {
            val response = apiService.getCharacters(
                page = page,
                name = filter.name.ifBlank { null },
                status = filter.status?.toQueryValue(),
                gender = filter.gender?.toQueryValue()
            )
            CharacterPage(
                characters = response.results.map { it.toDomain() },
                nextPage = if (response.info.next != null) page + 1 else null,
                totalCount = response.info.count
            )
        } catch (e: HttpException) {
            // La API responde 404 cuando ningún personaje cumple los filtros.
            // Para la aplicación eso no es un error, es una lista vacía
            if (e.code() == HTTP_NOT_FOUND) CharacterPage(emptyList(), nextPage = null, totalCount = 0) else throw e
        }
    }

    override suspend fun getCharacterById(id: Int): Character {
        return apiService.getCharacterById(id).toDomain()
    }

    override suspend fun getEpisodesByIds(ids: List<Int>): List<Episode> {
        // Con el formato [1,2,3] la API devuelve una lista aunque solo haya un id
        val idsParam = ids.joinToString(separator = ",", prefix = "[", postfix = "]")
        return apiService.getEpisodesByIds(idsParam).map { it.toDomain() }
    }

    override suspend fun getLocationById(id: Int): Location {
        return apiService.getLocationById(id).toDomain()
    }

    private fun CharacterStatus.toQueryValue(): String = when (this) {
        CharacterStatus.ALIVE -> "alive"
        CharacterStatus.DEAD -> "dead"
        CharacterStatus.UNKNOWN -> "unknown"
    }

    private fun CharacterGender.toQueryValue(): String = when (this) {
        CharacterGender.FEMALE -> "female"
        CharacterGender.MALE -> "male"
        CharacterGender.GENDERLESS -> "genderless"
        CharacterGender.UNKNOWN -> "unknown"
    }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
