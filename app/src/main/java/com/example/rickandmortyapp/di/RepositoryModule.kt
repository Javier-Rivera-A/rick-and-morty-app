package com.example.rickandmortyapp.di

import com.example.rickandmortyapp.data.repository.network.NetworkStatusRepositoryImpl
import com.example.rickandmortyapp.data.repository.remote.CharacterRepositoryImpl
import com.example.rickandmortyapp.domain.repository.CharacterRepository
import com.example.rickandmortyapp.domain.repository.NetworkStatusRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        characterRepositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindNetworkStatusRepository(
        networkStatusRepositoryImpl: NetworkStatusRepositoryImpl
    ): NetworkStatusRepository
}
