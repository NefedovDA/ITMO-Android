package ru.ifmo.nefedov.task8.weather.bd

import android.app.Application
import androidx.room.Room
import ru.ifmo.nefedov.task8.weather.bd.openWeather.OpenWeatherApi
import ru.ifmo.nefedov.task8.weather.bd.room.WeatherDatabase

class WeatherApp : Application() {
    lateinit var openWeatherApi: OpenWeatherApi
        private set

    lateinit var weatherDatabase: WeatherDatabase

    override fun onCreate() {
        super.onCreate()
        openWeatherApi = OpenWeatherApi()
        weatherDatabase = Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java,
            WEATHER_DATABASE_NAME
        ).build()
        app = this
    }

    companion object {
        lateinit var app: WeatherApp
            private set

        lateinit var weatherDatabase: WeatherDatabase
            private set

        const val WEATHER_DATABASE_NAME = "weather_database.db"
    }
}