package com.azatberdimyradov.weather.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.azatberdimyradov.weather.data.locale.LocationItem
import com.azatberdimyradov.weather.data.locale.WeatherDao
import com.azatberdimyradov.weather.data.remote.WeatherAPI
import com.azatberdimyradov.weather.data.remote.models.WeatherResponse
import com.azatberdimyradov.weather.other.Constants
import com.azatberdimyradov.weather.other.Resource
import com.azatberdimyradov.weather.other.checkPermission
import com.azatberdimyradov.weather.other.convertToCity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val weatherAPI: WeatherAPI,
    @ApplicationContext private val context: Context
): WeatherRepository {

    override suspend fun insertLocationIntoDb(locationItem: LocationItem) {
        weatherDao.insertLocation(locationItem)
    }

    override suspend fun deleteLocationFromDb(locationItem: LocationItem) {
        weatherDao.deleteLocation(locationItem)
    }

    override fun observeAllLocations(): Flow<List<LocationItem>> {
        return weatherDao.observeAllLocation()
    }

    override suspend fun searchForLocation(locationName: String): Resource<List<LocationItem>> {
        return try {
            val geocoder = Geocoder(context)
            val locations = geocoder.getFromLocationName(locationName,5)
            if (locations.isNotEmpty()){
                val locationItems = listOf(locations[0].convertToCity())
                locationItems.let {
                    return@let Resource.Success(it)
                }
            } else {
                Resource.Empty()
            }
        }catch (e: Exception){
            Resource.Error(e.localizedMessage)
        }
    }

    override suspend fun searchForWeather(locationItem: LocationItem): Resource<WeatherResponse> {
        return try {
            val response = weatherAPI.searchForWeather(
                lat = locationItem.lat.toString(),
                lon = locationItem.lon.toString()
            )
            if (response.isSuccessful){
                response.body()?.let {
                    return@let Resource.Success(it)
                } ?: Resource.Error(response.errorBody().toString())
            } else {
                Resource.Error(response.errorBody().toString())
            }
        }catch (e: Exception){
            Resource.Error(e.localizedMessage)
        }
    }

    @SuppressLint("VisibleForTests")
    override suspend fun getCurrentLocationWeather(function: (LocationItem) -> Unit ) {
        checkPermission(Constants.ACCESS_COARSE_LOCATION, context)
        try {
            FusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener {
                    val geocoder = Geocoder(context)
                    val location = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    function(location[0].convertToCity())
                }
        }catch (e: Exception){
            println("Error: ${e.localizedMessage}")
        }
    }
}