package ru.ifmo.nefedov.task8.weather.bd.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FlatDayForecast::class], version = WeatherDatabase.VERSION)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        const val VERSION = 1
    }
}