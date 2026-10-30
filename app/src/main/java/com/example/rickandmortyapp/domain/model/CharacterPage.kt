package com.example.rickandmortyapp.domain.model

// Una página de resultados. nextPage es null cuando ya no hay más páginas por cargar
data class CharacterPage(
    val characters: List<Character>,
    val nextPage: Int?,
    val totalCount: Int // Total de personajes que cumplen los filtros, sumando todas las páginas
)
