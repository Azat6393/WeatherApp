package com.azatberdimyradov.weather.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azatberdimyradov.weather.data.locale.LocationItem
import com.azatberdimyradov.weather.data.remote.models.WeatherResponse
import com.azatberdimyradov.weather.other.Resource
import com.azatberdimyradov.weather.repositories.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel @ViewModelInject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    init {
        getCurrentLocation()
    }

    private val _weatherResponse = MutableStateFlow<Resource<WeatherResponse>>(Resource.Empty())
    val weatherResponse: StateFlow<Resource<WeatherResponse>> = _weatherResponse

    private val _locationsResponse = MutableStateFlow<Resource<List<LocationItem>>>(Resource.Empty())
    val locationResponse: StateFlow<Resource<List<LocationItem>>> = _locationsResponse

    private val _currentLocation = MutableStateFlow(LocationItem("", "", "", "", 0.0, 0.0))
    val currentLocation: StateFlow<LocationItem> = _currentLocation

    val observeAllLocations = repository.observeAllLocations()

    fun insertLocationIntoDb(locationItem: LocationItem) = viewModelScope.launch {
        repository.insertLocationIntoDb(locationItem)
    }

    fun deleteLocationFromDb(locationItem: LocationItem) = viewModelScope.launch {
        repository.deleteLocationFromDb(locationItem)
    }

    fun searchForLocation(locationName: String) {
        if (locationName.isEmpty()){
            _locationsResponse.value = Resource.Empty()
            return
        }
        viewModelScope.launch {
            val response = repository.searchForLocation(locationName)
            _locationsResponse.value = response
        }
    }

    fun searchForWeather(locationItem: LocationItem) = viewModelScope.launch {
        _weatherResponse.value = Resource.Loading()
        val response = repository.searchForWeather(locationItem)
        when(response){
            is Resource.Success -> _currentLocation.value = locationItem
        }
        _weatherResponse.value = response
    }

    fun getCurrentLocation() = viewModelScope.launch {
        repository.getCurrentLocationWeather {
            _currentLocation.value = it
            searchForWeather(it)
        }
    }
}