package com.example.rickandmortyapp.domain.model

data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String,
    val gender: CharacterGender,
    val imageUrl: String,
    val originName: String,
    val locationName: String,
    val locationId: Int?, // null cuando la API no conoce la ubicación del personaje
    val episodeIds: List<Int>
)
