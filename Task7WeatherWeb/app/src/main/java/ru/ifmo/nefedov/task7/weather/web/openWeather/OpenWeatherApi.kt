package ru.ifmo.nefedov.task7.weather.web.openWeather

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("weather")
    fun getTodayForecast(
        @Query("id") cityId: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String?
    ): Call<DayForecast>

    @GET("forecast")
    fun getWeekForecast(
        @Query("id") cityId: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String?
    ): Call<WeekForecast>

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        operator fun invoke(): OpenWeatherApi {
            val client = OkHttpClient()
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(OpenWeatherApi::class.java)
        }
    }
}