package com.example.rickandmortyapp.features.character.list

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.model.CharacterFilter
import com.example.rickandmortyapp.domain.model.CharacterGender
import com.example.rickandmortyapp.domain.model.CharacterStatus
import com.example.rickandmortyapp.domain.usecase.character.GetCharactersUseCase
import com.example.rickandmortyapp.domain.usecase.network.ObserveNetworkStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class CharacterListUiState(
    val characters: List<Character> = emptyList(),
    val filter: CharacterFilter = CharacterFilter(),
    val isLoading: Boolean = false,
    val totalCount: Int = 0,
    val endReached: Boolean = false, // true cuando ya se cargó la última página
    @StringRes val errorMessage: Int? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    observeNetworkStatusUseCase: ObserveNetworkStatusUseCase
) : ViewModel() {

    // Arranca cargando: la primera página se pide cuando pasa el debounce de los filtros
    private val _uiState = MutableStateFlow(CharacterListUiState(isLoading = true))
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    // Página que se pedirá en el siguiente scroll; null cuando no quedan más
    private var nextPage: Int? = FIRST_PAGE
    private var loadJob: Job? = null

    init {
        // Cada cambio en los filtros reinicia la lista desde la primera página.
        // El debounce evita una petición por cada letra que se escribe en el buscador
        viewModelScope.launch {
            _uiState
                .map { it.filter }
                .distinctUntilChanged()
                .debounce(FILTER_DEBOUNCE_MS)
                .collect { reload() }
        }

        // Si la petición falló por falta de conexión, se reintenta sola al volver la red
        viewModelScope.launch {
            observeNetworkStatusUseCase()
                .filter { isOnline -> isOnline }
                .collect {
                    if (_uiState.value.errorMessage != null) loadNextPage()
                }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(filter = it.filter.copy(name = name)) }
    }

    fun onStatusSelected(status: CharacterStatus?) {
        _uiState.update { it.copy(filter = it.filter.copy(status = status)) }
    }

    fun onGenderSelected(gender: CharacterGender?) {
        _uiState.update { it.copy(filter = it.filter.copy(gender = gender)) }
    }

    // La pantalla la llama cuando el usuario se acerca al final de la lista
    fun loadNextPage() {
        val page = nextPage ?: return
        // Si ya hay una página en camino no se pide otra
        if (loadJob?.isActive == true) return

        val filter = _uiState.value.filter
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val result = getCharactersUseCase(page, filter)
                nextPage = result.nextPage
                _uiState.update {
                    it.copy(
                        characters = it.characters + result.characters,
                        totalCount = result.totalCount,
                        isLoading = false,
                        endReached = result.nextPage == null
                    )
                }
            } catch (e: CancellationException) {
                // La carga se canceló porque cambiaron los filtros; no es un error
                throw e
            } catch (e: IOException) {
                showError(R.string.error_no_connection)
            } catch (e: Exception) {
                showError(R.string.error_loading_characters)
            }
        }
    }

    private fun reload() {
        loadJob?.cancel()
        nextPage = FIRST_PAGE
        _uiState.update {
            it.copy(characters = emptyList(), isLoading = true, endReached = false, errorMessage = null)
        }
        loadNextPage()
    }

    // Se conservan los personajes ya cargados: es mejor mostrarlos que dejar la pantalla vacía
    private fun showError(@StringRes message: Int) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val FILTER_DEBOUNCE_MS = 400L
    }
}
