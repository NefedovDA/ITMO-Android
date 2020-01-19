package ru.ifmo.nefedov.task7.weather.web.cache

import ru.ifmo.nefedov.task7.weather.web.openWeather.DayForecast
import ru.ifmo.nefedov.task7.weather.web.openWeather.WeekForecast

object Cache {
    private const val TEN_MINUTES: Long = 60 * 1000

    private var dayLastUpdate: Long? = null
    private var weekLastUpdate: Long? = null

    private fun isActual(lastUpdate: Long?): Boolean =
        lastUpdate?.let { it - System.currentTimeMillis() < TEN_MINUTES } ?: false

    var dayForecast: DayForecast? = null
        get() = if (isActual(dayLastUpdate)) field else null
        set(value) {
            dayLastUpdate = System.currentTimeMillis()
            field = value
        }

    var weekForecast: WeekForecast? = null
        get() = if (isActual(weekLastUpdate)) field else null
        set(value) {
            weekLastUpdate = System.currentTimeMillis()
            field = value
        }
}