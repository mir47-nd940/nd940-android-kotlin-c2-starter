package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.db.AsteroidDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay
import com.udacity.asteroidradar.network.NasaApi.retrofitService
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.repo.AsteroidRepository
import com.udacity.asteroidradar.repo.AsteroidsFilter
import com.udacity.asteroidradar.repo.WeeklyAsteroids
import com.udacity.asteroidradar.util.getNextSevenDaysFormattedDates
import kotlinx.coroutines.launch
import org.json.JSONObject

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
            asteroidRepository.refreshAsteroids()
            asteroidRepository.refreshImage()
        }
    }

    /**
     * Gets filtered Mars real estate property information from the Mars API Retrofit service and
     * updates the [MarsProperty] [List] and [MarsApiStatus] [LiveData]. The Retrofit service
     * returns a coroutine Deferred, which we await to get the result of the transaction.
     * @param filter the [MarsApiFilter] that is sent as part of the web server request
     */
    private fun getAsteroids() {
        viewModelScope.launch {
            _status.value = AsteroidApiStatus.LOADING
            try {
                val dates = getNextSevenDaysFormattedDates()
                val j = retrofitService.getNeoJson(
                    startDate = dates.first(),
                    endDate = dates.last(),
                    apiKey = "DEMO_KEY"
                )

                @Suppress("BlockingMethodInNonBlockingContext")
                val jj = parseAsteroidsJsonResult(JSONObject(j.body()?.string() ?: ""))

                println("mmmmm jj $jj")
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                println("mmmmm $e")
                _status.value = AsteroidApiStatus.ERROR
            }
        }
    }

    fun getFilteredAsteroids(filter: AsteroidsFilter) = asteroidRepository.getFilteredAsteroids(filter)
}