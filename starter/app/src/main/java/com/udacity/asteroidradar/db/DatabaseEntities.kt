package com.udacity.asteroidradar.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid

@Entity(tableName = "asteroid_table")
data class AsteroidEntity(
    @PrimaryKey(autoGenerate = true)
    var asteroidId: Long = 0L,

    @ColumnInfo(name = "name")
    var asteroidName: String = ""
)

fun List<AsteroidEntity>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.asteroidId,
            codename = "",
            closeApproachDate = "",
            absoluteMagnitude = 0.0,
            estimatedDiameter = 0.0,
            relativeVelocity = 0.0,
            distanceFromEarth = 0.0,
            isPotentiallyHazardous = false
        )
    }
}
