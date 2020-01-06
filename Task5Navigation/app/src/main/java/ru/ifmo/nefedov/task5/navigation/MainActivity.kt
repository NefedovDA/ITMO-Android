package ru.ifmo.nefedov.task5.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_bottom_navigation?.setOnNavigationItemSelectedListener {
            selectTab("${it.itemId}")
            true
        }

        main_navigation?.setNavigationItemSelectedListener {
            selectTab("${it.itemId}")
            true
        }
    }

    private fun selectTab(fragmentTag: String) {
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag) ?: TabFragment()

        val transaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.findFragmentByTag(currentFragmentTag)?.let {
            transaction.hide(it)
        }

        if (fragment.isAdded) {
            transaction.show(fragment)
        } else {
            transaction.add(R.id.main_fragmentContainer, fragment, fragmentTag)
        }

        currentFragmentTag = fragmentTag
        transaction.commit()
    }
}
