package ru.ifmo.nefedov.task5.navigation

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            selectTab(R.id.navigation_home)
        } else {
            currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG_KEY)
            val menuItem = (main_navigation?.menu ?: main_bottom_navigation?.menu)!!
                .findItem(currentFragmentTag!!.toInt())
            menuItem.isChecked = true
        }

        main_bottom_navigation?.setOnNavigationItemSelectedListener(::selectItem)
        main_navigation?.setNavigationItemSelectedListener(::selectItem)
    }

    private fun selectItem(menuItem: MenuItem): Boolean {
        selectTab(menuItem.itemId)
        main_navigation?.menu?.forEach { it.isChecked = false }
        menuItem.isChecked = true
        return true
    }

    private fun selectTab(tabInd: Int) = selectTab("$tabInd")

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

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
        if (fragment == null) {
            Log.e(LOG_KEY, "No current fragment!")
            super.onBackPressed()
            return
        }


        val manager = fragment.childFragmentManager
        if (manager.backStackEntryCount == 0) {
            finish()
        } else {
            manager.popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FRAGMENT_TAG_KEY, currentFragmentTag)
    }

    companion object {
        private const val LOG_KEY: String = "MAinActivity"

        private const val CURRENT_FRAGMENT_TAG_KEY: String = "MainActivity_CurrentFragmentTag"
    }
}
