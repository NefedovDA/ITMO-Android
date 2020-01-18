package ru.ifmo.nefedov.task7.weather.web

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
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
import ru.ifmo.nefedov.task7.weather.web.items.day.Day
import ru.ifmo.nefedov.task7.weather.web.items.day.DayAdapter
import java.util.*

class MainActivity : AppCompatActivity() {

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

        today_city.text = "Санкт-Петербург"
        today_weatherImg.setImageResource(R.drawable._028_snowfall)
        today_temp.text = getString(R.string.template_tempC, -12)

        setupExtra(
            today_wind,
            getString(R.string.windDescription),
            R.drawable._041_wind_sign,
            getString(R.string.template_windPower, 7.2)
        )
        setupExtra(
            today_pressure,
            getString(R.string.pressureDescription),
            R.drawable._024_thermometer,
            getString(R.string.template_pressure, 1002)
        )
        setupExtra(
            today_humidity,
            getString(R.string.humidityDescription),
            R.drawable._019_thermometer,
            getString(R.string.template_percents, 72)
        )

        val days = List(6) { Day(Calendar.getInstance().time) }
        val viewManager = LinearLayoutManager(this)

        week_days.apply {
            layoutManager = viewManager
            adapter = DayAdapter(context, days)
        }
    }

    private fun setupExtra(extraView: View, description: String, imageId: Int, value: String) {
        extraView.findViewById<TextView>(R.id.extra_header).text = description
        extraView.findViewById<ImageView>(R.id.extra_image).setImageResource(imageId)
        extraView.findViewById<TextView>(R.id.extra_value).text = value
    }
}
