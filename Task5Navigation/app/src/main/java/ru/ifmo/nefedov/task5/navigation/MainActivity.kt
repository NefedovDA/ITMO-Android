package ru.ifmo.nefedov.task5.navigation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
        }

        main_bottom_navigation?.setOnNavigationItemSelectedListener {
            selectTab(it.itemId)
            true
        }

        main_navigation?.setNavigationItemSelectedListener {
            selectTab(it.itemId)
            true
        }
    }

    private fun selectTab(tabInd: Int) {
        val fragmentTag = "$tabInd"
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
