package com.udacity.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.db.AsteroidDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay
import com.udacity.asteroidradar.repo.AsteroidRepository
import com.udacity.asteroidradar.repo.AsteroidsFilter
import com.udacity.asteroidradar.repo.Weekly
import com.udacity.asteroidradar.util.getNextSevenDaysFormattedDates
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<AsteroidApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<AsteroidApiStatus>
        get() = _status

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