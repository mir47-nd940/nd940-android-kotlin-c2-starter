package com.udacity.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.db.AsteroidDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay
import com.udacity.asteroidradar.domain.AsteroidRepository
import com.udacity.asteroidradar.domain.AsteroidsFilter
import com.udacity.asteroidradar.domain.Weekly
import com.udacity.asteroidradar.util.getNextSevenDaysFormattedDates
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    val asteroids: LiveData<List<Asteroid>>
        get() = asteroidRepository.asteroids

    val imageOfTheDay: LiveData<ImageOfTheDay>
        get() = asteroidRepository.imageOfTheDay

    private val database = AsteroidDatabase.getInstance(application)
    private val asteroidRepository = AsteroidRepository(database)

    init {
        loadAsteroids(Weekly)
    }

    fun loadAsteroids(filter: AsteroidsFilter) = asteroidRepository.loadAsteroids(filter)

    fun updateAsteroids() = viewModelScope.launch {
        asteroidRepository.refreshAsteroids(getNextSevenDaysFormattedDates())
        asteroidRepository.refreshImage()
    }
}