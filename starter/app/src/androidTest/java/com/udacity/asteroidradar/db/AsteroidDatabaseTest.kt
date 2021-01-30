package com.udacity.asteroidradar.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AsteroidDatabaseTest {
    private lateinit var asteroidDao: AsteroidDao
    private lateinit var db: AsteroidDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AsteroidDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        asteroidDao = db.asteroidDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAsteroid() = runBlocking {
        val entity = AsteroidEntity(
            asteroidId = 123L,
            asteroidName = "name",
            closeApproachDate = "2012-01-31",
            absoluteMagnitude = 0.0,
            estimatedDiameter = 0.0,
            relativeVelocity = 0.0,
            distanceFromEarth = 0.0,
            isPotentiallyHazardous = false
        )
        asteroidDao.insert(entity)
        val entityGet = asteroidDao.get(123L)
        Assert.assertEquals(entityGet?.asteroidName, "name")
        Assert.assertEquals(entityGet?.isPotentiallyHazardous, false)
    }
}