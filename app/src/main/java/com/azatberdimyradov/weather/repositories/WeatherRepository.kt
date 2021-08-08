package com.azatberdimyradov.weather.repositories

import com.azatberdimyradov.weather.data.locale.LocationItem
import com.azatberdimyradov.weather.data.remote.models.WeatherResponse
import com.azatberdimyradov.weather.other.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun insertLocationIntoDb(locationItem: LocationItem)

    suspend fun deleteLocationFromDb(locationItem: LocationItem)

    fun observeAllLocations(): Flow<List<LocationItem>>

    suspend fun searchForLocation(locationName: String): Resource<List<LocationItem>>

    suspend fun searchForWeather(locationItem: LocationItem): Resource<WeatherResponse>

    suspend fun getCurrentLocationWeather(function: (LocationItem) -> Unit)

}