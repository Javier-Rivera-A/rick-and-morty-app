package com.example.rickandmortyapp.data.remote.location

import com.example.rickandmortyapp.domain.model.Location

data class LocationDto(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val dimension: String = "",
    val residents: List<String> = emptyList() // URLs de los personajes que viven allí
) {
    fun toDomain(): Location = Location(
        id = id,
        name = name,
        type = type,
        dimension = dimension,
        residentsCount = residents.size
    )
}
