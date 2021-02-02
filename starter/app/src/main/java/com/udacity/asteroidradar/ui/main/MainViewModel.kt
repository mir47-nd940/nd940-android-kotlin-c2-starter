package com.udacity.asteroidradar.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.domain.*
import com.udacity.asteroidradar.util.getNextSevenDaysFormattedDates
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel(private val asteroidRepository: AsteroidRepository) : ViewModel() {
    val asteroids: LiveData<List<Asteroid>>
        get() = asteroidRepository.asteroids

    val imageOfTheDay: LiveData<ImageOfTheDay>
        get() = asteroidRepository.imageOfTheDay

    private val _showListProgress = MutableLiveData(false)
    val showListProgress: LiveData<Boolean> = _showListProgress

    private val _showListError = MutableLiveData(false)
    val showListError: LiveData<Boolean> = _showListError

    fun loadAsteroids(filter: AsteroidsFilter) = asteroidRepository.loadAsteroids(filter)

    fun updateAsteroids() = viewModelScope.launch {
        try {
            _showListProgress.value = true
            asteroidRepository.refreshAsteroids(getNextSevenDaysFormattedDates())
            _showListProgress.value = false
        } catch (e: Exception) {
            // Prevent app crash, in case there is an error loading data
            _showListProgress.value = false
            _showListError.value = true
            // TODO: report error to crash reporting service e.g. Firebase Crashlytics
            Timber.e(e)
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