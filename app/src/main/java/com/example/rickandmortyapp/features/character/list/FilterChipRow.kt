package com.example.rickandmortyapp.features.character.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rickandmortyapp.R

// Fila de chips para elegir un valor de un filtro. El chip "Todos" quita el filtro (null)
@Composable
fun <T> FilterChipRow(
    title: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> Int, // Devuelve el id del string que se muestra en el chip
    onSelected: (T?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
        }
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelected(null) },
                label = { Text(text = stringResource(R.string.filter_all)) }
            )
        }
        items(options) { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = { Text(text = stringResource(optionLabel(option))) }
            )
        }
    }
}
