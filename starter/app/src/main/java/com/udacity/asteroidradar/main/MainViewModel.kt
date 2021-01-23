package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.network.NetworkAsteroid
import com.udacity.asteroidradar.network.NasaApi.retrofitService
import com.udacity.asteroidradar.network.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel : ViewModel() {
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<AsteroidApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _asteroids = MutableLiveData<List<NetworkAsteroid>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val asteroids: LiveData<List<NetworkAsteroid>>
        get() = _asteroids

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getAsteroids()
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
                _asteroids.value = emptyList()
            }
        }
    }

//    suspend fun refreshVideos() {
//        withContext(Dispatchers.IO) {
//            val playlist = Network.devbytes.getPlaylist().await()
//            database.videoDao.insertAll(*playlist.asDatabaseModel())
//        }
//    }

}