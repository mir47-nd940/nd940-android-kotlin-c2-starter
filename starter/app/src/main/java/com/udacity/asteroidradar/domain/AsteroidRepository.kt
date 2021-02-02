package com.udacity.asteroidradar.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.db.AsteroidDao
import com.udacity.asteroidradar.db.ImageDao
import com.udacity.asteroidradar.db.asDomainModel
import com.udacity.asteroidradar.util.formatDate
import com.udacity.asteroidradar.util.parseAsteroidsJsonResult
import org.json.JSONObject
import java.util.*

sealed class AsteroidsFilter
object Weekly : AsteroidsFilter()
object Daily : AsteroidsFilter()
object All : AsteroidsFilter()

class AsteroidRepository(private val asteroidDao: AsteroidDao, private val imageDao: ImageDao) {

    private val _today = formatDate(Calendar.getInstance().time)

    private val _asteroidsFilter: MutableLiveData<AsteroidsFilter> = MutableLiveData()

    /**
     * A list of asteroids that can be shown on the screen.
     */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.switchMap(_asteroidsFilter) { filter ->
            val list = when (filter) {
                Weekly -> asteroidDao.getFromDate(_today)
                Daily -> asteroidDao.getFor(_today)
                All -> asteroidDao.getAll()
            }
            Transformations.map(list) { it.asDomainModel() }
        }

    /**
     * Image data to be loaded into an image view.
     */
    val imageOfTheDay: LiveData<ImageOfTheDay> =
        Transformations.map(imageDao.get()) { it?.asDomainModel() }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshAsteroids(dates: ArrayList<String>) {
        val json = NasaApi.retrofitService.getNeoJson(dates.first(), dates.last())

        @Suppress("BlockingMethodInNonBlockingContext")
        val jsonObject = JSONObject(json.body()?.string() ?: "")

        val networkAsteroids = parseAsteroidsJsonResult(jsonObject, dates)
        asteroidDao.insertAll(networkAsteroids.asDatabaseModel())

        _asteroidsFilter.postValue(Weekly)
    }

    suspend fun purgeAsteroidsBeforeDate(date: String) {
        asteroidDao.deleteBeforeDate(date)
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
        val image = NasaApi.retrofitService.getImageInfo()
        if (image.isImageType) {
            imageDao.clear()
            imageDao.insert(image.asDatabaseModel())
        }
    }

    fun loadAsteroids(filter: AsteroidsFilter) = _asteroidsFilter.postValue(filter)
}