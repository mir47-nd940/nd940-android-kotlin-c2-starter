package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.db.AsteroidDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay
import com.udacity.asteroidradar.repo.AsteroidRepository
import com.udacity.asteroidradar.repo.AsteroidsFilter
import com.udacity.asteroidradar.repo.WeeklyAsteroids
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

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getFilteredAsteroids(WeeklyAsteroids)
        viewModelScope.launch {
            // TODO: force load asteroids on first time app start, afterwards refreshes are handled by WorkManager
            asteroidRepository.refreshAsteroids()
            asteroidRepository.refreshImage()
        }
    }

    fun getFilteredAsteroids(filter: AsteroidsFilter) = asteroidRepository.getFilteredAsteroids(filter)
}