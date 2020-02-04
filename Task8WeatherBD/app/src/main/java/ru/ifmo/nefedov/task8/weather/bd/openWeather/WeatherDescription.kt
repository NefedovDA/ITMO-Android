package ru.ifmo.nefedov.task8.weather.bd.openWeather

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import ru.ifmo.nefedov.task8.weather.bd.R
import java.util.*
import kotlin.math.roundToInt


@Parcelize
data class DayForecast(
    @Json(name = "dt") val dateL: Long,
    @Json(name = "weather") val icons: List<Icon>,
    @Json(name = "main") val main: WeatherMain,
    @Json(name = "wind") val wind: WeatherWind
) : Parcelable {
    val date: Date get() = Date(dateL * 1000)
}

@Parcelize
data class Icon(
    @Json(name = "id") val id: Int,
    @Json(name = "icon") val name: String
) : Parcelable {
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

@Parcelize
data class WeatherMain(
    @Json(name = "temp") val temp: Float,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "humidity") val humidity: Int
) : Parcelable {
    val roundTemp get() = (temp * 10).roundToInt().toFloat() / 10
}

@Parcelize
data class WeatherWind(
    @Json(name = "speed") val speed: Float
) : Parcelable

@Parcelize
data class WeekForecast(
    @Json(name = "list") val forecasts: List<DayForecast>
) : Parcelable