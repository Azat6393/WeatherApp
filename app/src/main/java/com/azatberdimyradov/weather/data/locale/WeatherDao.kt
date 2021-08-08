package com.azatberdimyradov.weather.data.locale

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert
    suspend fun insertLocation(locationItem: LocationItem)

    @Delete
    suspend fun deleteLocation(locationItem: LocationItem)

    @Query("SELECT * FROM location")
    fun observeAllLocation(): Flow<List<LocationItem>>

}