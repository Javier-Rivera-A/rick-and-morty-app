package com.example.rickandmortyapp.data.remote.character

// Respuesta de /character: la información de paginación y los personajes de la página
data class CharacterPageDto(
    val info: PageInfoDto = PageInfoDto(),
    val results: List<CharacterDto> = emptyList()
)

data class PageInfoDto(
    val count: Int = 0,
    val pages: Int = 0,
    val next: String? = null, // URL de la siguiente página, null si es la última
    val prev: String? = null
)
