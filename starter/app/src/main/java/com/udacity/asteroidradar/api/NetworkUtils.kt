package com.udacity.asteroidradar.api

import org.json.JSONObject

fun parseAsteroidsJsonResult(
    jsonResult: JSONObject,
    dateList: ArrayList<String>
): ArrayList<NetworkAsteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")
    val asteroidNeoList = ArrayList<NetworkAsteroid>()

    for (date in dateList) {
        val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(date)

        for (i in 0 until dateAsteroidJsonArray.length()) {
            val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
            val id = asteroidJson.getLong("id")
            val codename = asteroidJson.getString("name")
            val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
            val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                .getJSONObject("kilometers").getDouble("estimated_diameter_max")

            val closeApproachData = asteroidJson
                .getJSONArray("close_approach_data").getJSONObject(0)
            val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                .getDouble("kilometers_per_second")
            val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                .getDouble("astronomical")
            val isPotentiallyHazardous = asteroidJson
                .getBoolean("is_potentially_hazardous_asteroid")

            val asteroidNeo = NetworkAsteroid(id, codename, date, absoluteMagnitude,
                estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous)
            asteroidNeoList.add(asteroidNeo)
        }
    }

    return asteroidNeoList
}