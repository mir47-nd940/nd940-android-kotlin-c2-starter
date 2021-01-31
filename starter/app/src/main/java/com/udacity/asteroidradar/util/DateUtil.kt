package com.udacity.asteroidradar.util

import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        formattedDateList.add(formatDate(calendar.time))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}

fun formatDate(date: Date): String =
    SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault()).format(date)
