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
                val forecast = WeatherApp.weatherDatabase.getWeatherDao().getAll()
                if (forecast.isEmpty()) {
                    intent.putExtra(RESULT_KEY, FAIL_VALUE)
                } else {
                    intent.putExtra(DAY_FORECAST_KEY, forecast[0].convertToDayForecast())
                    intent.putExtra(
                        WEEK_FORECAST_KEY,
                        WeekForecast(
                            forecast.subList(1, forecast.size).map { it.convertToDayForecast() }
                        )
                    )
                }
            }
            UPLOAD_TO_BD -> {
                val dayForecast = Cache.dayForecast
                val weekForecast = Cache.weekForecast
                if (dayForecast == null || weekForecast == null) {
                    intent.putExtra(RESULT_KEY, FAIL_VALUE)
                } else {
                    val forecastList = listOf(dayForecast, *weekForecast.forecasts.toTypedArray())
                        .mapIndexed { index, forecast ->
                            FlatDayForecast(index, forecast)
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

        private const val LOAD_FROM_BD = "${SERVICE_NAME}_load_from_bd"
        private const val UPLOAD_TO_BD = "${SERVICE_NAME}_upload_to_bd"

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
        fun upload(context: Context) = startService(context, UPLOAD_TO_BD)
    }
}