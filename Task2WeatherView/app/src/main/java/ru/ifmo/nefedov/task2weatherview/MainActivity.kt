package ru.ifmo.nefedov.task2weatherview

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_header.view.*

class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_header.header_switch.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) =
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }
}
