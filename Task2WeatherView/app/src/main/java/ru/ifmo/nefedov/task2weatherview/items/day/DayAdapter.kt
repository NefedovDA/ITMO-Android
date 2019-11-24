package ru.ifmo.nefedov.task2weatherview.items.day

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.nefedov.task2weatherview.R

class DayAdapter(
    private val context: Context,
    private val days: List<Day>
) : RecyclerView.Adapter<DayViewHolder>() {
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
        holder.descriptionText.text = DESCRIPTION_FORMATTER.format(days[position].date)
        holder.tempText.text = context.getString(R.string.template_tempC, 12)
    }


    companion object {
        private val DESCRIPTION_FORMATTER = SimpleDateFormat("EEE, dd MMM")
    }
}