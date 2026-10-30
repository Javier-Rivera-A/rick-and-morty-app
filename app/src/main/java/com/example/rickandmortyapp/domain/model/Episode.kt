package com.example.rickandmortyapp.domain.model

data class Episode(
    val id: Int,
    val name: String,
    val code: String, // Por ejemplo S01E01
    val airDate: String
)
