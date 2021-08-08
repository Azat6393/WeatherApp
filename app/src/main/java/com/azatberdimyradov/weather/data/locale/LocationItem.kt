package com.azatberdimyradov.weather.data.locale

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationItem(
    var countyName: String,
    var cityName: String,
    var sub: String? = null,
    var countryCode: String,
    var lat: Double,
    var lon: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)