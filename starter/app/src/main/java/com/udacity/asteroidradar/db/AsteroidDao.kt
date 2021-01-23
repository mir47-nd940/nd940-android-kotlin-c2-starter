package com.udacity.asteroidradar.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Defines methods for using the AsteroidEntity class with Room.
 */
@Dao
interface AsteroidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asteroid: AsteroidEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(asteroids: List<AsteroidEntity>)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param asteroid new value to write
     */
    @Update
    suspend fun update(asteroid: AsteroidEntity)

    /**
     * Selects and returns the row that matches the supplied id, which is our key.
     *
     * @param key unique id of the asteroid to be retrieved
     */
    @Query("SELECT * from asteroid_table WHERE asteroidId = :key")
    suspend fun get(key: Long): AsteroidEntity?

    /**
     * Selects and returns all rows in the table,
     * sorted by id in descending order.
     */
    @Query("SELECT * FROM asteroid_table ORDER BY asteroidId DESC")
    fun getAll(): LiveData<List<AsteroidEntity>>

    /**
     * Deletes all values from the table.
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM asteroid_table")
    suspend fun clear()

}