package com.example.rickandmortyapp.features.character.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.core.component.ConnectionIndicator
import com.example.rickandmortyapp.core.component.ErrorMessage
import com.example.rickandmortyapp.core.component.OfflineBanner
import com.example.rickandmortyapp.core.util.color
import com.example.rickandmortyapp.core.util.labelRes
import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.model.Episode
import com.example.rickandmortyapp.domain.model.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    isOnline: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Con characterId como clave, la búsqueda solo se repite si llega otro id
    LaunchedEffect(characterId) {
        viewModel.loadCharacter(characterId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = state.character?.name.orEmpty(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = { ConnectionIndicator(isOnline = isOnline) }
                )
                OfflineBanner(isOnline = isOnline)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            val character = state.character
            val errorMessage = state.errorMessage
            when {
                state.isLoading -> CircularProgressIndicator()

                errorMessage != null -> ErrorMessage(
                    message = stringResource(errorMessage),
                    onRetry = { viewModel.loadCharacter(characterId) }
                )

                character != null -> CharacterDetailContent(
                    character = character,
                    location = state.location,
                    episodes = state.episodes
                )
            }
        }
    }
}

@Composable
private fun CharacterDetailContent(
    character: Character,
    location: Location?,
    episodes: List<Episode>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(character.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = character.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(character.status.color(), CircleShape)
                    )
                    Text(
                        text = stringResource(character.status.labelRes()),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        item {
            DetailCard {
                DetailRow(label = stringResource(R.string.detail_species), value = character.species)
                DetailRow(label = stringResource(R.string.detail_type), value = character.type)
                DetailRow(
                    label = stringResource(R.string.detail_gender),
                    value = stringResource(character.gender.labelRes())
                )
                DetailRow(label = stringResource(R.string.detail_origin), value = character.originName)
            }
        }

        item {
            DetailCard {
                Text(
                    text = stringResource(R.string.detail_location),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = character.locationName,
                    style = MaterialTheme.typography.bodyLarge
                )
                // La ubicación solo existe si la API la conoce
                location?.let {
                    DetailRow(label = stringResource(R.string.detail_location_type), value = it.type)
                    DetailRow(label = stringResource(R.string.detail_dimension), value = it.dimension)
                    DetailRow(
                        label = stringResource(R.string.detail_residents),
                        value = it.residentsCount.toString()
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.detail_episodes, episodes.size),
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(episodes, key = { it.id }) { episode ->
            ListItem(
                overlineContent = { Text(text = episode.code) },
                headlineContent = { Text(text = episode.name) },
                supportingContent = { Text(text = episode.airDate) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun DetailCard(content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            // La API deja algunos campos vacíos (por ejemplo el tipo)
            text = value.ifBlank { stringResource(R.string.not_available) },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.4f)
        )
    }
}
