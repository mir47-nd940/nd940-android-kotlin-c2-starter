package com.udacity.asteroidradar.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.domain.*
import com.udacity.asteroidradar.util.getNextSevenDaysFormattedDates
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel(private val asteroidRepository: AsteroidRepository) : ViewModel() {
    val asteroids: LiveData<List<Asteroid>>
        get() = asteroidRepository.asteroids

    val imageOfTheDay: LiveData<ImageOfTheDay>
        get() = asteroidRepository.imageOfTheDay

    init {
        loadAsteroids(Weekly)
    }

    fun loadAsteroids(filter: AsteroidsFilter) = asteroidRepository.loadAsteroids(filter)

    fun updateAsteroids() = viewModelScope.launch {
        asteroidRepository.refreshAsteroids(getNextSevenDaysFormattedDates())
        asteroidRepository.refreshImage()
    }
}