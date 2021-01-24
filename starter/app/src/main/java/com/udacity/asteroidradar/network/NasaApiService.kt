package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getProperties] method
 */
interface NasaApiService {
    /**
     * Returns a Coroutine [List] of [MarsProperty] which can be fetched with await() if in a Coroutine scope.
     * The @GET annotation indicates that the "realestate" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("neo/rest/v1/feed")
    suspend fun getNeoJson(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Response<ResponseBody>

    /**
     *
     */
    @GET("planetary/apod")
    suspend fun getImageInfo(
        @Query("api_key") apiKey: String
    ): NetworkPictureOfDay
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object NasaApi {
    val retrofitService : NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
}
