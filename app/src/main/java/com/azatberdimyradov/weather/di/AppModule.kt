package com.azatberdimyradov.weather.di

import android.content.Context
import androidx.room.Room
import com.azatberdimyradov.weather.R
import com.azatberdimyradov.weather.data.locale.WeatherDao
import com.azatberdimyradov.weather.data.locale.WeatherDatabase
import com.azatberdimyradov.weather.data.remote.WeatherAPI
import com.azatberdimyradov.weather.other.Constants.BASE_URL
import com.azatberdimyradov.weather.other.Constants.DATABASE_NAME
import com.azatberdimyradov.weather.repositories.DefaultWeatherRepository
import com.azatberdimyradov.weather.repositories.WeatherRepository
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, WeatherDatabase::class.java, DATABASE_NAME).build()


    @Singleton
    @Provides
    fun provideDefaultWeatherRepository(
        dao: WeatherDao,
        api: WeatherAPI,
        @ApplicationContext context: Context
    ) = DefaultWeatherRepository(dao, api, context) as WeatherRepository


    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
    )

    @Singleton
    @Provides
    fun provideWeatherDao(
        database: WeatherDatabase
    ) = database.weatherDao()

    @Singleton
    @Provides
    fun provideWeatherAPI(): WeatherAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(WeatherAPI::class.java)
    }

}