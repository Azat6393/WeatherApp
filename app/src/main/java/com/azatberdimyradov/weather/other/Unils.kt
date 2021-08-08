package com.azatberdimyradov.weather.other

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.azatberdimyradov.weather.data.locale.LocationItem
import com.azatberdimyradov.weather.other.Constants.PERMISSION_REQUEST
import java.text.SimpleDateFormat
import java.util.*

fun Address.convertToCity(): LocationItem {
    return LocationItem(
        countyName = this.countryName,
        cityName = this.adminArea,
        sub = this.subAdminArea ?: null,
        countryCode = countryCode,
        lat = latitude,
        lon = longitude
    )
}

fun checkPermission(permission: String, context: Context): Boolean {
    return !(Build.VERSION.SDK_INT >= 23
            && ContextCompat.checkSelfPermission(
        context,
        permission
    ) != PackageManager.PERMISSION_GRANTED)
}

@SuppressLint("SimpleDateFormat")
fun Int.unixToDay(): String {
    val sdf = SimpleDateFormat("EEEE")
    val date = Date(this.toLong() * 1000)
    return sdf.format(date)
}

fun Int.unixToTime(): String {
    val sdf = SimpleDateFormat("kk : mm")
    val date = Date(this.toLong() * 1000)
    return sdf.format(date)
}

fun String.iconUrl(): String {
    return "http://openweathermap.org/img/wn/${this}@2x.png"
}