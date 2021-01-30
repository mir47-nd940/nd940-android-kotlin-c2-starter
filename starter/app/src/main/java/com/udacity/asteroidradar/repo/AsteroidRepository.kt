package com.udacity.asteroidradar.repo

import androidx.lifecycle.*
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

    private val _asteroidsFilter: MutableLiveData<AsteroidsFilter> = MutableLiveData()
    /**
     * A list of asteroids that can be shown on the screen.
     */
    val asteroids: LiveData<List<Asteroid>> = Transformations.switchMap(_asteroidsFilter) { filter ->
        val list = when (filter) {
            WeeklyAsteroids -> database.asteroidDao.getFrom(formatDate(Calendar.getInstance().time))
            DailyAsteroids -> database.asteroidDao.getFor(formatDate(Calendar.getInstance().time))
            AllAsteroids -> database.asteroidDao.getAll()
        }
        Transformations.map(list) { it.asDomainModel() }
    }

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
                val json = NasaApi.retrofitService.getNeoJson(dates.first(), dates.last())

                @Suppress("BlockingMethodInNonBlockingContext")
                val jsonObject = JSONObject(json.body()?.string() ?: "")

                val networkAsteroids = parseAsteroidsJsonResult(jsonObject, dates)
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
                val image = NasaApi.retrofitService.getImageInfo()
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

    fun getFilteredAsteroids(filter: AsteroidsFilter) = _asteroidsFilter.postValue(filter)
}