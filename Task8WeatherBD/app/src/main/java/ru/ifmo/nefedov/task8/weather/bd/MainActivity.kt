package ru.ifmo.nefedov.task8.weather.bd

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_header.view.*
import kotlinx.android.synthetic.main.main_today.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.ifmo.nefedov.task8.weather.bd.adapters.WeekAdapter
import ru.ifmo.nefedov.task8.weather.bd.cache.Cache
import ru.ifmo.nefedov.task8.weather.bd.openWeather.*
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService.Companion.DAY_FORECAST_KEY
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService.Companion.FAIL_VALUE
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService.Companion.LOAD_FROM_BD
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService.Companion.OK_VALUE
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService.Companion.RESULT_KEY
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService.Companion.UPLOAD_TO_BD_DAY
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService.Companion.UPLOAD_TO_BD_WEEK
import ru.ifmo.nefedov.task8.weather.bd.services.WeatherDatabaseService.Companion.WEEK_FORECAST_KEY

class MainActivity : AppCompatActivity() {
    private var todayCall: Call<DayForecast>? = null
    private var weekCall: Call<WeekForecast>? = null

    private var wasConnectionError: Boolean = false
    private var wasBdError: Boolean = false

    private var hasInternetConnection: Boolean = false

    private lateinit var weekAdapter: WeekAdapter
    private lateinit var receiver: MainReceiver

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

        registerInternetConnectionChecker()

        main_header.header_refresh_btn.setOnClickListener {
            if (!setData()) {
                showOkDialog(R.string.actual_data_title, R.string.actual_data_message)
            }
        }
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

    private fun setData(): Boolean {
        wasConnectionError = false

        val dayForecast = Cache.dayForecast
        val weekForecast = Cache.weekForecast

        return if (dayForecast == null || weekForecast == null) {
            startLoadData()
            true
        } else {
            todayForecastSetter(dayForecast)
            weekAdapter.setDataList(weekForecast.forecasts)
            setViewMode()
            false
        }
    }

    private fun startLoadData() {
        setLoadMode()
        if (!hasInternetConnection) {
            loadDataFromBD()
            return
        }

        todayCall = WeatherApp.app.openWeatherApi.getTodayForecast(
            SaintPetersburg.apiId,
            BuildConfig.API_KEY,
            Units.CELSIUS
        )
        todayCall?.enqueue(ForecastCallback {
            todayForecastSetter(it)
            Cache.dayForecast = it
            setViewMode(Select.TODAY)
            WeatherDatabaseService.uploadDay(this@MainActivity)
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
            WeatherDatabaseService.uploadWeek(this@MainActivity)
        })
    }

    private fun loadDataFromBD() {
        showOkDialog(
            R.string.use_bd_title,
            R.string.use_bd_message
        )
        wasBdError = false
        WeatherDatabaseService.load(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        todayCall?.cancel()
        todayCall = null
        weekCall?.cancel()
        weekCall = null
    }

    private fun showConnectionErrorOkDialog() {
        if (wasConnectionError) return
        wasConnectionError = true
        showOkDialog(R.string.error_message_title, R.string.error_message_connection)
    }

    private fun showWorkWithDbErrorOkDialog(message: String) {
        if (wasBdError) return
        wasBdError = true
        showOkDialog(getString(R.string.error_working_with_db_title), message)
    }

    private fun showOkDialog(titleId: Int, messageId: Int) =
        showOkDialog(getString(titleId), getString(messageId))

    private fun showOkDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
            .apply {
                setTitle(title)
                setMessage(message)
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

    private fun registerInternetConnectionChecker() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    hasInternetConnection = true
                }

                override fun onLost(network: Network) {
                    hasInternetConnection = false
                }
            }
        )
    }

    override fun onResume() {
        receiver = MainReceiver()

        val intentFilter = IntentFilter().apply {
            addAction(LOAD_FROM_BD)
            addAction(UPLOAD_TO_BD_DAY)
            addAction(UPLOAD_TO_BD_WEEK)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onPause()
    }

    inner class ForecastCallback<T>(val setter: (T) -> Unit) : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = showConnectionErrorOkDialog()
        override fun onResponse(call: Call<T>, response: Response<T>) {
            response.body()?.let { setter(it) }
                ?: showConnectionErrorOkDialog()
        }
    }

    inner class MainReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null) {
                return
            }

            if (intent == null) {
                return
            }

            val resultStatus = intent.getStringExtra(RESULT_KEY)
            when (intent.action) {
                LOAD_FROM_BD -> when (resultStatus) {
                    null, FAIL_VALUE -> showWorkWithDbErrorOkDialog(getString(R.string.error_load_from_db_message))
                    OK_VALUE -> {
                        val dayForecast: DayForecast =
                            intent.getParcelableExtra(DAY_FORECAST_KEY) ?: return
                        val weekForecast: WeekForecast =
                            intent.getParcelableExtra(WEEK_FORECAST_KEY) ?: return

                        todayForecastSetter(dayForecast)
                        weekAdapter.setDataList(weekForecast.forecasts)
                        setViewMode()
                    }
                }

                UPLOAD_TO_BD_DAY -> when (resultStatus) {
                    null, FAIL_VALUE -> showWorkWithDbErrorOkDialog(
                        getString(R.string.error_upload_to_db_message, getString(R.string.day))
                    )
                }
                UPLOAD_TO_BD_WEEK -> when (resultStatus) {
                    null, FAIL_VALUE -> showWorkWithDbErrorOkDialog(
                        getString(R.string.error_upload_to_db_message, getString(R.string.week))
                    )
                }
            }

        }

    }
}
