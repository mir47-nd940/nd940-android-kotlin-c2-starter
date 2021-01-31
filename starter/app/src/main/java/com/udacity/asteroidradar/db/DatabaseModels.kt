package com.udacity.asteroidradar.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay

@Entity(tableName = "asteroid_table")
data class AsteroidEntity(
    @PrimaryKey
    var asteroidId: Long,

    @ColumnInfo(name = "name")
    var asteroidName: String,

    var closeApproachDate: String,
    var absoluteMagnitude: Double,
    var estimatedDiameter: Double,
    var relativeVelocity: Double,
    var distanceFromEarth: Double,
    var isPotentiallyHazardous: Boolean
)

@Entity(tableName = "image_table")
data class ImageEntity(
    @PrimaryKey
    var url: String,
    var title: String
)

fun List<AsteroidEntity>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.asteroidId,
            codename = it.asteroidName,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

fun ImageEntity.asDomainModel() = ImageOfTheDay(this.url, this.title)