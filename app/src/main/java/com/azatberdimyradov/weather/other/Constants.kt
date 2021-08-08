package com.azatberdimyradov.weather.other

import android.Manifest
import com.azatberdimyradov.weather.ui.MainActivity

object Constants {

    const val BASE_URL = "http://api.openweathermap.org"
    const val DATABASE_NAME = "weather_db"
    const val EXCLUDE_MINUTELY = "minutely"
    const val EXCLUDE_ALERTS = "alerts"
    const val UNITS = "metric"
    const val LANG = "en"

    const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    const val PERMISSION_REQUEST = 200

    const val SEARCH_QUERY_IS_EMPTY = "Empty"
    const val SEARCH_QUERY_IS_NOT_EMPTY = "IsNotEmpty"

}