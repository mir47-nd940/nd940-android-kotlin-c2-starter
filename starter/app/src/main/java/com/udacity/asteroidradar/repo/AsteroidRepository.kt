package com.udacity.asteroidradar.repo

import androidx.lifecycle.*
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.db.AsteroidDatabase
import com.udacity.asteroidradar.db.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.util.formatDate
import com.udacity.asteroidradar.util.getNextSevenDaysFormattedDates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

sealed class AsteroidsFilter
object WeeklyAsteroids : AsteroidsFilter()
object DailyAsteroids : AsteroidsFilter()
object AllAsteroids : AsteroidsFilter()

class AsteroidRepository(private val database: AsteroidDatabase) {
    /**
     * A list of asteroids that can be shown on the screen.
     */
    private val _asteroids: MutableLiveData<AsteroidsFilter> = MutableLiveData()

    val asteroids: LiveData<List<Asteroid>> = Transformations.switchMap(_asteroids) { filter ->
        when (filter) {
            WeeklyAsteroids -> Transformations.map(database.asteroidDao.getFrom(formatDate(Calendar.getInstance().time))) { it.asDomainModel() }
            DailyAsteroids -> Transformations.map(database.asteroidDao.getFor(formatDate(Calendar.getInstance().time))) { it.asDomainModel() }
            AllAsteroids -> Transformations.map(database.asteroidDao.getAll()) { it.asDomainModel() }
        }
    }

    fun getFilteredAsteroids(filter: AsteroidsFilter) = _asteroids.postValue(filter)

    /**
     * A list of asteroids that can be shown on the screen.
     */
    val imageOfTheDay: LiveData<ImageOfTheDay> =
        Transformations.map(database.imageDao.get()) { it?.asDomainModel() }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val dates = getNextSevenDaysFormattedDates()
                val json = NasaApi.retrofitService.getNeoJson(
                    startDate = dates.first(),
                    endDate = dates.last(),
                    apiKey = BuildConfig.NASA_API_KEY
                )

                @Suppress("BlockingMethodInNonBlockingContext")
                val networkAsteroids = parseAsteroidsJsonResult(JSONObject(json.body()?.string() ?: ""))
                database.asteroidDao.clear()
                database.asteroidDao.insertAll(networkAsteroids.asDatabaseModel())
            } catch (e: Exception) {
                // Prevent app crash, in case there is an error loading data
                // TODO: these type of errors should be reported to a crash reporting service e.g. Firebase Crashlytics
                println(e)
            }
        }
    }

    /**
     * Refresh the image stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshImage() {
        withContext(Dispatchers.IO) {
            try {
                val image = NasaApi.retrofitService.getImageInfo(apiKey = BuildConfig.NASA_API_KEY)
                if ("image" == image.mediaType) {
                    database.imageDao.clear()
                    database.imageDao.insert(image.asDatabaseModel())
                }
            } catch (e: Exception) {
                // Prevent app crash, in case there is an error loading data.
                // TODO: these type of errors should be reported to a crash reporting service e.g. Firebase Crashlytics
                println(e)
            }
        }
    }
}