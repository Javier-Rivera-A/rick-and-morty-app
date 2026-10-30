package com.example.rickandmortyapp.data.remote

import com.example.rickandmortyapp.data.remote.character.CharacterDto
import com.example.rickandmortyapp.data.remote.character.CharacterPageDto
import com.example.rickandmortyapp.data.remote.episode.EpisodeDto
import com.example.rickandmortyapp.data.remote.location.LocationDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Lista paginada de personajes. Los filtros en null no se envían en la URL
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("gender") gender: String? = null
    ): CharacterPageDto

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto

    // Recibe varios ids en formato [1,2,3] y siempre devuelve una lista
    @GET("episode/{ids}")
    suspend fun getEpisodesByIds(@Path("ids") ids: String): List<EpisodeDto>

    @GET("location/{id}")
    suspend fun getLocationById(@Path("id") id: Int): LocationDto
}
