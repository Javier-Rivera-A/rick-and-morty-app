package com.example.rickandmortyapp.data.remote.episode

import com.example.rickandmortyapp.domain.model.Episode
import com.google.gson.annotations.SerializedName

data class EpisodeDto(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("air_date") val airDate: String = "",
    val episode: String = "" // Código del episodio, por ejemplo S01E01
) {
    fun toDomain(): Episode = Episode(
        id = id,
        name = name,
        code = episode,
        airDate = airDate
    )
}
