package com.azatberdimyradov.weather.data.remote

import com.azatberdimyradov.weather.BuildConfig
import com.azatberdimyradov.weather.data.remote.models.WeatherResponse
import com.azatberdimyradov.weather.other.Constants.EXCLUDE_ALERTS
import com.azatberdimyradov.weather.other.Constants.EXCLUDE_MINUTELY
import com.azatberdimyradov.weather.other.Constants.LANG
import com.azatberdimyradov.weather.other.Constants.UNITS
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("/data/2.5/onecall")
    suspend fun searchForWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String = "$EXCLUDE_MINUTELY,$EXCLUDE_ALERTS",
        @Query("appid") appId: String = BuildConfig.API_KEY,
        @Query("units") units: String = UNITS,
        @Query("lang") lang: String = LANG
    ): Response<WeatherResponse>

}