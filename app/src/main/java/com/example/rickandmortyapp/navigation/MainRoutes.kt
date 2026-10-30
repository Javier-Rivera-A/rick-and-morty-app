package com.example.rickandmortyapp.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data object CharacterList : MainRoutes()

    @Serializable
    data class CharacterDetail(val characterId: Int) : MainRoutes() // Recibe el id del personaje
}
