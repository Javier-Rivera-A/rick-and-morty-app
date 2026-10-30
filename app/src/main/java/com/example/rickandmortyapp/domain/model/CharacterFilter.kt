package com.example.rickandmortyapp.domain.model

// Criterios de búsqueda de la lista. Un valor null significa "sin filtro" para ese campo
data class CharacterFilter(
    val name: String = "",
    val status: CharacterStatus? = null,
    val gender: CharacterGender? = null
)
