package ru.ifmo.nefedov.task8.weather.bd.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import ru.ifmo.nefedov.task8.weather.bd.openWeather.DayForecast
import ru.ifmo.nefedov.task8.weather.bd.openWeather.Icon
import ru.ifmo.nefedov.task8.weather.bd.openWeather.WeatherMain
import ru.ifmo.nefedov.task8.weather.bd.openWeather.WeatherWind

@Entity
@Parcelize
data class FlatDayForecast(
    @PrimaryKey val id: Int,
    val dateL: Long,
    val iconId: Int?,
    val iconName: String?,
    val temp: Float,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Float
) : Parcelable {
    constructor(id: Int, forecast: DayForecast) : this(
        id = id,
        dateL = forecast.dateL,
        iconId = forecast.icons.getGoodImage()?.id,
        iconName = forecast.icons.getGoodImage()?.name,
        temp = forecast.main.temp,
        pressure = forecast.main.pressure,
        humidity = forecast.main.pressure,
        windSpeed = forecast.wind.speed
    )

    fun convertToDayForecast(): DayForecast = DayForecast(
        dateL = dateL,
        icons = listOfNotNull(iconId?.let { Icon(iconId, iconName!!) }),
        main = WeatherMain(temp, pressure, humidity),
        wind = WeatherWind(windSpeed)
    )
}

private fun List<Icon>.getGoodImage(): Icon? = firstOrNull { it.getImageSourceId() != null }