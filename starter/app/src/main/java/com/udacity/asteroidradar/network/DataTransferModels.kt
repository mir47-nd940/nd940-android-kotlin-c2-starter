package com.udacity.asteroidradar.network

import android.os.Parcelable
import com.udacity.asteroidradar.db.AsteroidEntity
import com.udacity.asteroidradar.db.ImageEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetworkAsteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable

data class NetworkImageOfTheDay(
    val url: String,
    val media_type: String,
    val title: String
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

fun NetworkImageOfTheDay.asDatabaseModel() = ImageEntity(this.url, this.title)
