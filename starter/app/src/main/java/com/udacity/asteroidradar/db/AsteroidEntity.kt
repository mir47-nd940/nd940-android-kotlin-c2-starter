package com.udacity.asteroidradar.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asteroid_table")
data class AsteroidEntity(
    @PrimaryKey(autoGenerate = true)
    var asteroidId: Long = 0L,

    @ColumnInfo(name = "name")
    var asteroidName: String = ""
)