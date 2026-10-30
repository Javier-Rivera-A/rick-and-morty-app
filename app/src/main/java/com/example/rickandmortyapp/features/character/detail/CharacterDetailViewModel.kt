package com.example.rickandmortyapp.features.character.detail

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.model.Episode
import com.example.rickandmortyapp.domain.model.Location
import com.example.rickandmortyapp.domain.usecase.character.GetCharacterByIdUseCase
import com.example.rickandmortyapp.domain.usecase.episode.GetEpisodesByIdsUseCase
import com.example.rickandmortyapp.domain.usecase.location.GetLocationByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class CharacterDetailUiState(
    val character: Character? = null,
    val location: Location? = null,
    val episodes: List<Episode> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null
)

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
    private val getEpisodesByIdsUseCase: GetEpisodesByIdsUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    fun loadCharacter(characterId: Int) {
        // Si el personaje ya está cargado (por ejemplo, al rotar la pantalla) no se pide de nuevo
        if (_uiState.value.character?.id == characterId) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val character = getCharacterByIdUseCase(characterId)

                // Los episodios y la ubicación no dependen entre sí, así que se piden en paralelo
                val (episodes, location) = coroutineScope {
                    val episodes = async { getEpisodesByIdsUseCase(character.episodeIds) }
                    val location = async { character.locationId?.let { getLocationByIdUseCase(it) } }
                    episodes.await() to location.await()
                }

                _uiState.update {
                    it.copy(
                        character = character,
                        episodes = episodes,
                        location = location,
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                showError(R.string.error_no_connection)
            } catch (e: Exception) {
                showError(R.string.error_loading_detail)
            }
        }
    }

    private fun showError(@StringRes message: Int) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }
}
