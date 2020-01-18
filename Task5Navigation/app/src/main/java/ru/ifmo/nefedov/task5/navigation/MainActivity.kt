package ru.ifmo.nefedov.task5.navigation

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var tabState: LinkedList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null) {
            tabState = LinkedList()
            selectTab(R.id.navigation_home)
        } else {
            tabState = LinkedList(savedInstanceState.getStringArrayList(TAB_STATE_KEY)!!)
            selectTab(tabState.first.toInt())
        }

        main_navigation?.setNavigationItemSelectedListener(::selectItem)
        main_bottom_navigation?.setOnNavigationItemSelectedListener(::selectItem)
    }

    private fun selectItem(menuItem: MenuItem): Boolean {
        selectTab(menuItem.itemId)
        return true
    }

    private fun selectTab(tabInd: Int) {
        selectTabFragment("$tabInd")
        main_navigation?.menu?.forEach { it.isChecked = false }
        val menuItem = (main_navigation?.menu ?: main_bottom_navigation?.menu)!!.findItem(tabInd)
        menuItem.isChecked = true
    }

    private fun selectTabFragment(fragmentTag: String) {
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag) ?: TabFragment()

        val transaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.findFragmentByTag(tabState.firstOrNull())?.let {
            if (it.childFragmentManager.backStackEntryCount == 0) {
                tabState.removeFirst()
                transaction.remove(it)
            } else {
                transaction.hide(it)
            }
        }

        if (fragment.isAdded) {
            transaction.show(fragment)
        } else {
            transaction.add(R.id.main_fragmentContainer, fragment, fragmentTag)
        }

        tabState.apply {
            remove(fragmentTag)
            addFirst(fragmentTag)
        }
        transaction.commit()
    }

    override fun onBackPressed() {
        if (tabState.isEmpty()) {
            finish()
        }
        val currentFragmentTag = tabState.first
        val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
        if (fragment == null) {
            Log.e(LOG_KEY, "No current fragment!")
            super.onBackPressed()
            return
        }


        val manager = fragment.childFragmentManager
        if (manager.backStackEntryCount == 0) {
            if (tabState.size <= 1) {
                finish()
            } else {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
                tabState.removeFirst()
                selectTab(tabState.first.toInt())
            }
        } else {
            manager.popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(TAB_STATE_KEY, ArrayList(tabState))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val LOG_KEY: String = "MainActivity"

        private const val TAB_STATE_KEY: String = "MainActivity_TabState"
    }
}
