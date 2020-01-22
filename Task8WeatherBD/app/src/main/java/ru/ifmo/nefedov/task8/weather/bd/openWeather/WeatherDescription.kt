package ru.ifmo.nefedov.task8.weather.bd.openWeather

import com.squareup.moshi.Json
import ru.ifmo.nefedov.task8.weather.bd.R
import java.util.*
import kotlin.math.roundToInt


data class DayForecast(
    @Json(name = "dt") private val dateL: Long,
    @Json(name = "weather") val icons: List<Icon>,
    @Json(name = "main") val main: WeatherMain,
    @Json(name = "wind") val wind: WeatherWind
) {
    val date: Date get() = Date(dateL * 1000)
}

data class Icon(
    @Json(name = "id") private val id: Int,
    @Json(name = "icon") val name: String
) {
    fun getImageSourceId(): Int? = when (id) {
        in (200 until 300) -> R.drawable._004_storm
        in (300 until 400) -> R.drawable._002_rain
        in (500 until 600) -> R.drawable._042_rain
        in (600 until 700) -> R.drawable._015_snow
        800 -> R.drawable._022_sun
        in (801 until 810) -> R.drawable._001_cloudy
        else -> null
    }
}

fun List<Icon>.getImageSourceId(): Int =
    mapNotNull { it.getImageSourceId() }.firstOrNull() ?: R.drawable._040_rainbow

data class WeatherMain(
    @Json(name = "temp") val temp: Float,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "humidity") val humidity: Int
) {
    val roundTemp get() = (temp * 10).roundToInt().toFloat() / 10
}

data class WeatherWind(
    @Json(name = "speed") val speed: Float
)

data class WeekForecast(
    @Json(name = "list") val forecasts: List<DayForecast>
)