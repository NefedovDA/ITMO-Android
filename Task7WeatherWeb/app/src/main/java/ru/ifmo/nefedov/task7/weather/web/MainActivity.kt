package ru.ifmo.nefedov.task7.weather.web

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_header.view.*
import kotlinx.android.synthetic.main.main_today.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.ifmo.nefedov.task7.weather.web.adapters.WeekAdapter
import ru.ifmo.nefedov.task7.weather.web.openWeather.*

class MainActivity : AppCompatActivity() {
    private var todayCall: Call<TodayForecast>? = null
    private var weekCall: Call<WeekForecast>? = null
    private lateinit var weekAdapter: WeekAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_header.header_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            }
        }

        val viewManager = LinearLayoutManager(this)
        weekAdapter = WeekAdapter(this)
        week_days.apply {
            layoutManager = viewManager
            adapter = weekAdapter
        }

        startLoadData()
    }

    private fun startLoadData() {
        // add progress bar

        val city = SaintPetersburg
        
        todayCall = WeatherApp.app.openWeatherApi.getTodayForecast(
            city.apiId,
            BuildConfig.API_KEY,
            Units.CELSIUS
        )
        todayCall?.enqueue(TodayForecastCallback(city))

        weekCall = WeatherApp.app.openWeatherApi.getWeekForecast(
            city.apiId,
            BuildConfig.API_KEY,
            Units.CELSIUS,
            WEEK_FORECAST_SIZE
        )
        weekCall?.enqueue(WeekForecastCallback())
    }

    override fun onDestroy() {
        super.onDestroy()
        todayCall?.cancel()
        todayCall = null
        weekCall?.cancel()
        weekCall = null
    }

    private fun showOkDialog() {
        val builder = AlertDialog.Builder(this)
            .apply {
                setTitle(R.string.error_message_title)
                setMessage(R.string.error_message_connection)
                setCancelable(false)
                setNegativeButton("OK") { dialog, _ ->
                    dialog.cancel()
                }
            }
        val alert = builder.create()
        alert.show()
    }

    private fun setupExtra(extraView: View, description: String, imageId: Int, value: String) {
        extraView.findViewById<TextView>(R.id.extra_header).text = description
        extraView.findViewById<ImageView>(R.id.extra_image).setImageResource(imageId)
        extraView.findViewById<TextView>(R.id.extra_value).text = value
    }

    inner class TodayForecastCallback(val city: City) : Callback<TodayForecast> {
        override fun onFailure(call: Call<TodayForecast>, t: Throwable) {
            showOkDialog()
        }

        override fun onResponse(call: Call<TodayForecast>, response: Response<TodayForecast>) {
            val forecast = response.body()
            if (forecast == null) {
                showOkDialog()
                return
            }

            today_city.text = getString(city.nameId)
            today_weatherImg.setImageResource(forecast.icons.getImageSourceId())
            today_temp.text = getString(R.string.template_tempC, forecast.main.temp)

            setupExtra(
                today_wind,
                getString(R.string.windDescription),
                R.drawable._041_wind_sign,
                getString(R.string.template_windPower, forecast.wind.speed)
            )
            setupExtra(
                today_pressure,
                getString(R.string.pressureDescription),
                R.drawable._024_thermometer,
                getString(R.string.template_pressure, forecast.main.pressure)
            )
            setupExtra(
                today_humidity,
                getString(R.string.humidityDescription),
                R.drawable._019_thermometer,
                getString(R.string.template_percents, forecast.main.humidity)
            )
        }
    }

    inner class WeekForecastCallback : Callback<WeekForecast> {
        override fun onFailure(call: Call<WeekForecast>, t: Throwable) {
            showOkDialog()
        }

        override fun onResponse(call: Call<WeekForecast>, response: Response<WeekForecast>) {
            val weekForecast = response.body()
            if (weekForecast == null) {
                showOkDialog()
                return
            }
            weekAdapter.setDataList(weekForecast.forecasts)
        }
    }

    companion object {
        const val WEEK_FORECAST_SIZE = 7
    }
}
