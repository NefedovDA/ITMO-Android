package ru.ifmo.nefedov.task8.weather.bd.adapters

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.week_day.view.*
import ru.ifmo.nefedov.task8.weather.bd.R
import ru.ifmo.nefedov.task8.weather.bd.openWeather.DayForecast
import ru.ifmo.nefedov.task8.weather.bd.openWeather.getImageSourceId

class WeekAdapter(private val context: Context) : RecyclerView.Adapter<WeekAdapter.DayViewHolder>() {
    class DayViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val date: TextView = root.day_description
        val image: ImageView = root.day_image
        val temp: TextView = root.day_temp
    }

    private var days: List<DayForecast> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder =
        DayViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.week_day,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = days.size

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.date.text = DATE_FORMATTER.format(day.date)
        holder.image.setImageResource(day.icons.getImageSourceId())
        holder.temp.text = context.getString(R.string.template_tempC, day.main.roundTemp)
    }

    fun setDataList(days: List<DayForecast>) {
        this.days = days
        notifyDataSetChanged()
    }

    companion object {
        private val DATE_FORMATTER = SimpleDateFormat("EEE, dd MMM hh:mm")
    }
}