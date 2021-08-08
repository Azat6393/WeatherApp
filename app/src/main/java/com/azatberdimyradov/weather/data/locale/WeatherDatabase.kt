package com.azatberdimyradov.weather.data.locale

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocationItem::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase: RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
}