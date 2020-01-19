package ru.ifmo.nefedov.task7.weather.web

import android.app.Application
import ru.ifmo.nefedov.task7.weather.web.openWeather.OpenWeatherApi

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