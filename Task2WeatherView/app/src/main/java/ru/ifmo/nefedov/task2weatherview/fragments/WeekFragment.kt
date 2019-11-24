package ru.ifmo.nefedov.task2weatherview.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_week.*
import ru.ifmo.nefedov.task2weatherview.R
import ru.ifmo.nefedov.task2weatherview.items.day.Day
import ru.ifmo.nefedov.task2weatherview.items.day.DayAdapter
import java.util.*

class WeekFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_week, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val days = List(6) { Day(Calendar.getInstance().time) }
        val viewManager = LinearLayoutManager(activity)

        week_days.apply {
            layoutManager = viewManager
            adapter = DayAdapter(context, days)
        }
    }
}