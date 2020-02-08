package ru.ifmo.nefedov.task8.weather.bd.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.ifmo.nefedov.task8.weather.bd.WeatherApp
import ru.ifmo.nefedov.task8.weather.bd.cache.Cache
import ru.ifmo.nefedov.task8.weather.bd.openWeather.WeekForecast
import ru.ifmo.nefedov.task8.weather.bd.room.FlatDayForecast

class WeatherDatabaseService : IntentService(SERVICE_NAME) {
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            Log.e(LOG_KEY, "Intent is null")
            return
        }

        when (intent.action) {
            LOAD_FROM_BD -> {
                val forecast = WeatherApp.app.weatherDatabase.getWeatherDao().getAll()
                if (forecast.isEmpty()) {
                    intent.putExtra(RESULT_KEY, FAIL_VALUE)
                } else {
                    intent.putExtra(RESULT_KEY, OK_VALUE)
                    intent.putExtra(DAY_FORECAST_KEY, forecast[0].convertToDayForecast())
                    intent.putExtra(
                        WEEK_FORECAST_KEY,
                        WeekForecast(
                            forecast.subList(1, forecast.size).map { it.convertToDayForecast() }
                        )
                    )
                }
            }
            UPLOAD_TO_BD_DAY -> {
                val dayForecast = Cache.dayForecast
                if (dayForecast == null) {
                    intent.putExtra(RESULT_KEY, FAIL_VALUE)
                } else {
                    try {
                        WeatherApp.app.weatherDatabase.getWeatherDao()
                            .insert(FlatDayForecast(0, dayForecast))
                        intent.putExtra(RESULT_KEY, OK_VALUE)
                    } catch (e: Exception) {
                        intent.putExtra(RESULT_KEY, FAIL_VALUE)
                    }
                }
            }
            UPLOAD_TO_BD_WEEK -> {
                val weekForecast = Cache.weekForecast
                if (weekForecast == null) {
                    intent.putExtra(RESULT_KEY, FAIL_VALUE)
                } else {
                    val forecastList = weekForecast.forecasts.mapIndexed { index, forecast ->
                        FlatDayForecast(index + 1, forecast)
                    }
                    try {
                        WeatherApp.app.weatherDatabase.getWeatherDao()
                            .insert(*forecastList.toTypedArray())
                        intent.putExtra(RESULT_KEY, OK_VALUE)
                    } catch (e: Exception) {
                        intent.putExtra(RESULT_KEY, FAIL_VALUE)
                    }
                }
            }
            else -> Log.e(LOG_KEY, "Mode is null or has undefined value")
        }

        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    companion object {
        private const val LOG_KEY = "WeatherDatabaseService"

        const val SERVICE_NAME = "ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService"

        const val LOAD_FROM_BD = "${SERVICE_NAME}_load_from_bd"
        const val UPLOAD_TO_BD_DAY = "${SERVICE_NAME}_upload_to_bd_day"
        const val UPLOAD_TO_BD_WEEK = "${SERVICE_NAME}_upload_to_bd_week"

        const val RESULT_KEY = "result_key"
        const val FAIL_VALUE = "fail_value"
        const val OK_VALUE = "ok_value"
        const val DAY_FORECAST_KEY = "day_forecast_value"
        const val WEEK_FORECAST_KEY = "week_forecast_value"

        private fun startService(context: Context, mode: String) {
            val intent = Intent(context, WeatherDatabaseService::class.java).apply { action = mode }
            context.startService(intent)
        }

        fun load(context: Context) = startService(context, LOAD_FROM_BD)
        fun uploadDay(context: Context) = startService(context, UPLOAD_TO_BD_DAY)
        fun uploadWeek(context: Context) = startService(context, UPLOAD_TO_BD_WEEK)
    }
}