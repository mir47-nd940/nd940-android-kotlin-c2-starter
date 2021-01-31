package com.udacity.asteroidradar.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.domain.AsteroidRepository

// TODO: Use DI (hilt/koin/dagger) to provide the AsteroidRepository instance,
//  which we will learn about in a future lesson.
class MainViewModelFactory(
    private val asteroidRepository: AsteroidRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(asteroidRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}