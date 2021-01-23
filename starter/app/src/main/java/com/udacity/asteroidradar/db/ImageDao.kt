package com.udacity.asteroidradar.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Defines methods for using the ImageEntity class with Room.
 */
@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageEntity)

    /**
     * Selects and returns the row
     */
    @Query("SELECT * from image_table")
    fun get(): LiveData<ImageEntity>

    /**
     * Deletes all values from the table.
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM image_table")
    suspend fun clear()
}