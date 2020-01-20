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
import ru.ifmo.nefedov.task7.weather.web.cache.Cache
import ru.ifmo.nefedov.task7.weather.web.openWeather.*

class MainActivity : AppCompatActivity() {
    private var todayCall: Call<DayForecast>? = null
    private var weekCall: Call<WeekForecast>? = null
    private lateinit var weekAdapter: WeekAdapter
    private var wasError: Boolean = false

    private fun setLoadMode() {
        main_today.visibility = View.INVISIBLE
        main_today_progress.visibility = View.VISIBLE

        week_days.visibility = View.INVISIBLE
        week_days_progress.visibility = View.VISIBLE
    }

    private enum class Select {
        TODAY,
        WEEK,
        BOTH
    }

    private fun setViewMode(select: Select = Select.BOTH) {
        if (select == Select.TODAY || select == Select.BOTH) {
            main_today.visibility = View.VISIBLE
            main_today_progress.visibility = View.INVISIBLE
        }

        if (select == Select.WEEK || select == Select.BOTH) {
            week_days.visibility = View.VISIBLE
            week_days_progress.visibility = View.INVISIBLE
        }
    }

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

        setData()
    }

    private fun setData() {
        val dayForecast = Cache.dayForecast
        val weekForecast = Cache.weekForecast

        if (dayForecast == null || weekForecast == null) {
            startLoadData()
        } else {
            todayForecastSetter(dayForecast)
            weekAdapter.setDataList(weekForecast.forecasts)
            setViewMode()
        }
    }

    private fun startLoadData() {
        wasError = false
        setLoadMode()

        todayCall = WeatherApp.app.openWeatherApi.getTodayForecast(
            SaintPetersburg.apiId,
            BuildConfig.API_KEY,
            Units.CELSIUS
        )
        todayCall?.enqueue(ForecastCallback {
            todayForecastSetter(it)
            Cache.dayForecast = it
            setViewMode(Select.TODAY)
        })

        weekCall = WeatherApp.app.openWeatherApi.getWeekForecast(
            SaintPetersburg.apiId,
            BuildConfig.API_KEY,
            Units.CELSIUS
        )
        weekCall?.enqueue(ForecastCallback {
            weekAdapter.setDataList(it.forecasts)
            Cache.weekForecast = it
            setViewMode(Select.WEEK)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        todayCall?.cancel()
        todayCall = null
        weekCall?.cancel()
        weekCall = null
    }

    private fun showOkDialog() {
        if (wasError) return
        wasError = true
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

    private fun todayForecastSetter(forecast: DayForecast) {
        today_city.text = getString(SaintPetersburg.nameId)
        today_weatherImg.setImageResource(forecast.icons.getImageSourceId())
        today_temp.text = getString(R.string.template_tempC, forecast.main.roundTemp)

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

    private fun setupExtra(extraView: View, description: String, imageId: Int, value: String) {
        extraView.findViewById<TextView>(R.id.extra_header).text = description
        extraView.findViewById<ImageView>(R.id.extra_image).setImageResource(imageId)
        extraView.findViewById<TextView>(R.id.extra_value).text = value
    }

    inner class ForecastCallback<T>(val setter: (T) -> Unit) : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = showOkDialog()
        override fun onResponse(call: Call<T>, response: Response<T>) {
            response.body()?.let { setter(it) }
                ?: showOkDialog()
        }
    }
}
