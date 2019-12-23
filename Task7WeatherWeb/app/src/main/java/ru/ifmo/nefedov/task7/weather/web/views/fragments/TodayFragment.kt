package ru.ifmo.nefedov.task7.weather.web.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_today.*
import ru.ifmo.nefedov.task7.weather.web.R

class TodayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
    }

    private fun setupExtra(extraView: View, description: String, imageId: Int, value: String) {
        extraView.findViewById<TextView>(R.id.extra_header).text = description
        extraView.findViewById<ImageView>(R.id.extra_image).setImageResource(imageId)
        extraView.findViewById<TextView>(R.id.extra_value).text = value
    }
}