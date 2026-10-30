package com.example.rickandmortyapp.features.character.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.core.component.ConnectionIndicator
import com.example.rickandmortyapp.core.component.ErrorMessage
import com.example.rickandmortyapp.core.component.OfflineBanner
import com.example.rickandmortyapp.core.util.labelRes
import com.example.rickandmortyapp.domain.model.CharacterGender
import com.example.rickandmortyapp.domain.model.CharacterStatus

// Cuántos elementos antes del final se empieza a pedir la siguiente página
private const val PREFETCH_DISTANCE = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    isOnline: Boolean,
    onNavigateToCharacterDetail: (Int) -> Unit,
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Scroll infinito: true cuando el último elemento visible está cerca del final
    val isNearEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            layoutInfo.totalItemsCount > 0 &&
                lastVisibleIndex >= layoutInfo.totalItemsCount - PREFETCH_DISTANCE
        }
    }

    // Se vuelve a evaluar cuando llega una página nueva, por si la lista sigue siendo corta
    LaunchedEffect(isNearEnd, state.characters.size) {
        if (isNearEnd) viewModel.loadNextPage()
    }

    // Con filtros nuevos la lista empieza otra vez desde arriba
    LaunchedEffect(state.filter) {
        listState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.character_list_title)) },
                    actions = { ConnectionIndicator(isOnline = isOnline) }
                )
                OfflineBanner(isOnline = isOnline)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CharacterFilters(
                name = state.filter.name,
                selectedStatus = state.filter.status,
                selectedGender = state.filter.gender,
                onNameChange = viewModel::onNameChange,
                onStatusSelected = viewModel::onStatusSelected,
                onGenderSelected = viewModel::onGenderSelected
            )

            // Cuántos personajes van cargados con el scroll y cuántos hay en total
            if (state.characters.isNotEmpty()) {
                Text(
                    text = pluralStringResource(
                        R.plurals.results_count,
                        state.totalCount,
                        state.characters.size,
                        state.totalCount
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val errorMessage = state.errorMessage
                when {
                    // Primera carga: todavía no hay nada que mostrar
                    state.characters.isEmpty() && state.isLoading -> CircularProgressIndicator()

                    // Falló la primera página
                    state.characters.isEmpty() && errorMessage != null -> ErrorMessage(
                        message = stringResource(errorMessage),
                        onRetry = viewModel::loadNextPage
                    )

                    // La búsqueda terminó y no hubo coincidencias
                    state.characters.isEmpty() && state.endReached -> Text(
                        text = stringResource(R.string.no_results),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    else -> LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.characters, key = { it.id }) { character ->
                            CharacterItem(
                                character = character,
                                onClick = { onNavigateToCharacterDetail(character.id) }
                            )
                        }

                        // Pie de la lista: cargando la siguiente página o error al pedirla
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    state.isLoading -> CircularProgressIndicator()
                                    errorMessage != null -> ErrorMessage(
                                        message = stringResource(errorMessage),
                                        onRetry = viewModel::loadNextPage
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterFilters(
    name: String,
    selectedStatus: CharacterStatus?,
    selectedGender: CharacterGender?,
    onNameChange: (String) -> Unit,
    onStatusSelected: (CharacterStatus?) -> Unit,
    onGenderSelected: (CharacterGender?) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            label = { Text(text = stringResource(R.string.search_by_name)) },
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (name.isNotEmpty()) {
                    IconButton(onClick = { onNameChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = stringResource(R.string.clear_search)
                        )
                    }
                }
            },
            singleLine = true,
            // El botón de buscar del teclado solo lo cierra; la búsqueda ya se hace mientras se escribe
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        FilterChipRow(
            title = stringResource(R.string.filter_status),
            options = CharacterStatus.entries,
            selected = selectedStatus,
            optionLabel = { it.labelRes() },
            onSelected = onStatusSelected
        )

        FilterChipRow(
            title = stringResource(R.string.filter_gender),
            options = CharacterGender.entries,
            selected = selectedGender,
            optionLabel = { it.labelRes() },
            onSelected = onGenderSelected
        )
    }
}
