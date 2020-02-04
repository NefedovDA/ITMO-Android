package ru.ifmo.nefedov.task8.weather.bd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * FROM FlatDayForecast")
    fun getAll(): List<FlatDayForecast>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg data: FlatDayForecast)
}