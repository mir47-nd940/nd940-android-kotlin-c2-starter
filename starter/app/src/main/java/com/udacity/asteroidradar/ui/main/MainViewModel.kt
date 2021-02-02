package com.udacity.asteroidradar.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.domain.*
import com.udacity.asteroidradar.util.getNextSevenDaysFormattedDates
import kotlinx.coroutines.launch
import timber.log.Timber

enum class NasaApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel(private val asteroidRepository: AsteroidRepository) : ViewModel() {
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<NasaApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<NasaApiStatus>
        get() = _status

    val asteroids: LiveData<List<Asteroid>>
        get() = asteroidRepository.asteroids

    val imageOfTheDay: LiveData<ImageOfTheDay>
        get() = asteroidRepository.imageOfTheDay

    fun loadAsteroids(filter: AsteroidsFilter) = asteroidRepository.loadAsteroids(filter)

    fun updateAsteroids() = viewModelScope.launch {
        try {
            _status.value = NasaApiStatus.LOADING
            asteroidRepository.refreshAsteroids(getNextSevenDaysFormattedDates())
            _status.value = NasaApiStatus.DONE
        } catch (e: Exception) {
            // Prevent app crash, in case there is an error loading data
            // TODO: report error to crash reporting service e.g. Firebase Crashlytics
            Timber.e(e)
            _status.value = NasaApiStatus.ERROR
        }
    }

    fun updateImage() = viewModelScope.launch {
        try {
            asteroidRepository.refreshImage()
        } catch (e: Exception) {
            // Prevent app crash, in case there is an error loading data
            // TODO: report error to crash reporting service e.g. Firebase Crashlytics
            Timber.e(e)
        }
    }
}