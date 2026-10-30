package com.example.rickandmortyapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PortalGreenLight,
    secondary = MortyYellow,
    tertiary = PortalGreen
)

private val LightColorScheme = lightColorScheme(
    primary = PortalGreen,
    secondary = DarkSpace,
    secondaryContainer = PortalGreenContainer, // Fondo de los chips seleccionados
    tertiary = PortalGreenLight,
    background = LightBackground,
    surface = LightBackground,
    surfaceContainerHighest = CardSurface // Fondo de las tarjetas
)

@Composable
fun RickAndMortyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
