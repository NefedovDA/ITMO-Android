package ru.ifmo.nefedov.task5.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_clicker.view.*

class ClickerFragment : Fragment() {

    private var number: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(NUMBER_KEY)?.let { number = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clicker, container, false).apply {
            clicker_text.text = (1..number).fold("0") { acc, i -> "$acc -> $i" }
            clicker_button.setOnClickListener {
                requireFragmentManager()
                    .beginTransaction()
                    .replace(R.id.tab_fragmentContainer, nextFragment())
                    .addToBackStack("$number")
                    .commit()
            }
        }
    }

    private fun nextFragment(): ClickerFragment {
        return ClickerFragment().apply {
            arguments = Bundle().apply {
                putInt(NUMBER_KEY, this@ClickerFragment.number + 1)
            }
        }
    }

    companion object {
        private const val NUMBER_KEY = "ClickerFragment_Number"
    }
}