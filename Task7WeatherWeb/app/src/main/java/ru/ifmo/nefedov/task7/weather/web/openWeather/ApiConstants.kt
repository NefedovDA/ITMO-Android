package ru.ifmo.nefedov.task7.weather.web.openWeather

import ru.ifmo.nefedov.task7.weather.web.R

interface City {
    val nameId: Int
    val apiId: String
}

object SaintPetersburg : City {
    override val nameId: Int = R.string.saint_petersburg_name
    override val apiId: String = "498817"
}

object Units {
    const val FAHRENHEIT = "imperial"
    const val CELSIUS = "metric"
    val KELVIN: String? = null
}