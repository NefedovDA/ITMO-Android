package ru.ifmo.nefedov.task8.weather.bd

import android.app.Application
import ru.ifmo.nefedov.task8.weather.bd.openWeather.OpenWeatherApi

class WeatherApp : Application() {
    lateinit var openWeatherApi: OpenWeatherApi
        private set

    override fun onCreate() {
        super.onCreate()
        openWeatherApi = OpenWeatherApi()
        app = this
    }

    companion object {
        lateinit var app: WeatherApp
            private set
    }
}