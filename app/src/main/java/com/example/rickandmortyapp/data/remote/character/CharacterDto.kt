package com.example.rickandmortyapp.data.remote.character

import com.example.rickandmortyapp.data.remote.idFromUrl
import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.model.CharacterGender
import com.example.rickandmortyapp.domain.model.CharacterStatus

// Representación de un personaje tal como llega de la API.
// Todos los campos tienen valor por defecto para garantizar el constructor vacío
data class CharacterDto(
    val id: Int = 0,
    val name: String = "",
    val status: String = "",
    val species: String = "",
    val type: String = "",
    val gender: String = "",
    val image: String = "",
    val origin: PlaceReferenceDto = PlaceReferenceDto(),
    val location: PlaceReferenceDto = PlaceReferenceDto(),
    val episode: List<String> = emptyList() // URLs de los episodios
) {
    fun toDomain(): Character = Character(
        id = id,
        name = name,
        status = status.toCharacterStatus(),
        species = species,
        type = type,
        gender = gender.toCharacterGender(),
        imageUrl = image,
        originName = origin.name,
        locationName = location.name,
        locationId = location.url.idFromUrl(),
        episodeIds = episode.mapNotNull { it.idFromUrl() }
    )
}

// El origen y la ubicación llegan como un nombre y la URL del recurso
data class PlaceReferenceDto(
    val name: String = "",
    val url: String = ""
)

private fun String.toCharacterStatus(): CharacterStatus = when (lowercase()) {
    "alive" -> CharacterStatus.ALIVE
    "dead" -> CharacterStatus.DEAD
    else -> CharacterStatus.UNKNOWN
}

private fun String.toCharacterGender(): CharacterGender = when (lowercase()) {
    "female" -> CharacterGender.FEMALE
    "male" -> CharacterGender.MALE
    "genderless" -> CharacterGender.GENDERLESS
    else -> CharacterGender.UNKNOWN
}
