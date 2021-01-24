package com.udacity.asteroidradar.network

import com.squareup.moshi.Json
import com.udacity.asteroidradar.db.AsteroidEntity
import com.udacity.asteroidradar.db.ImageEntity

data class NetworkAsteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

data class NetworkPictureOfDay(
    @Json(name = "media_type")
    val mediaType: String,
    val title: String,
    val url: String
)

fun List<NetworkAsteroid>.asDatabaseModel(): List<AsteroidEntity> {
    return map {
        AsteroidEntity(
            asteroidId = it.id,
            asteroidName = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

fun NetworkPictureOfDay.asDatabaseModel() = ImageEntity(this.url, this.title)
