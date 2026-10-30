package com.example.rickandmortyapp.core.util

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.domain.model.CharacterGender
import com.example.rickandmortyapp.domain.model.CharacterStatus

// Textos y colores con los que la UI muestra los valores del dominio

@StringRes
fun CharacterStatus.labelRes(): Int = when (this) {
    CharacterStatus.ALIVE -> R.string.status_alive
    CharacterStatus.DEAD -> R.string.status_dead
    CharacterStatus.UNKNOWN -> R.string.status_unknown
}

fun CharacterStatus.color(): Color = when (this) {
    CharacterStatus.ALIVE -> Color(0xFF43A047)
    CharacterStatus.DEAD -> Color(0xFFE53935)
    CharacterStatus.UNKNOWN -> Color(0xFF9E9E9E)
}

@StringRes
fun CharacterGender.labelRes(): Int = when (this) {
    CharacterGender.FEMALE -> R.string.gender_female
    CharacterGender.MALE -> R.string.gender_male
    CharacterGender.GENDERLESS -> R.string.gender_genderless
    CharacterGender.UNKNOWN -> R.string.gender_unknown
}
