package ru.ifmo.nefedov.task2weatherview.items.day

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.week_day.view.*

class DayViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val descriptionText: TextView = root.day_description
    val image: ImageView = root.day_image
    val tempText: TextView = root.day_temp
}