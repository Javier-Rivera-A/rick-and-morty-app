package com.example.rickandmortyapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.rickandmortyapp.features.character.detail.CharacterDetailScreen
import com.example.rickandmortyapp.features.character.list.CharacterListScreen
import com.example.rickandmortyapp.features.network.NetworkStatusViewModel

@Composable
fun AppNavigation(
    // Se obtiene fuera del NavHost para que el estado de conexión sea el mismo en todas las pantallas
    networkStatusViewModel: NetworkStatusViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isOnline by networkStatusViewModel.isOnline.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = MainRoutes.CharacterList
        ) {
            composable<MainRoutes.CharacterList> {
                CharacterListScreen(
                    isOnline = isOnline,
                    onNavigateToCharacterDetail = { characterId ->
                        navController.navigate(MainRoutes.CharacterDetail(characterId))
                    }
                )
            }
            composable<MainRoutes.CharacterDetail> {
                val args = it.toRoute<MainRoutes.CharacterDetail>()
                CharacterDetailScreen(
                    characterId = args.characterId,
                    isOnline = isOnline,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
