package com.azatberdimyradov.weather.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.azatberdimyradov.weather.adapters.DailyWeatherAdapter
import com.azatberdimyradov.weather.adapters.HourlyWeatherAdapter
import com.azatberdimyradov.weather.adapters.SearchResultAdapter
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class WeatherFragmentFactory @Inject constructor(
    private val dailyWeatherAdapter: DailyWeatherAdapter,
    private val hourlyWeatherAdapter: HourlyWeatherAdapter,
    private val searchResultAdapter: SearchResultAdapter,
    private val glide: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            WeatherFragment::class.java.name -> WeatherFragment(
                dailyWeatherAdapter,
                hourlyWeatherAdapter,
                searchResultAdapter,
                glide
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}