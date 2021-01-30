package com.udacity.asteroidradar.util

fun interface ImageLoadCallback {
    fun onImageLoaded(success: Boolean): Unit
}
