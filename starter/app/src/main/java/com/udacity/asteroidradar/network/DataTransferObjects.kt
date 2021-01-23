package com.udacity.asteroidradar.network

import android.os.Parcelable
import com.udacity.asteroidradar.db.AsteroidEntity
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
