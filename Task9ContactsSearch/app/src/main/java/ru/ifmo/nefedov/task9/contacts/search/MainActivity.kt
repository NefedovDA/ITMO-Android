package ru.ifmo.nefedov.task9.contacts.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ifmo.nefedov.task9.contacts.search.contact.Contact
import ru.ifmo.nefedov.task9.contacts.search.contact.ContactAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ContactAdapter
    private lateinit var allContacts: List<Contact>

    private val currentLocale: Locale
        get() = resources.configuration.locales[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout.setOnRefreshListener {
            checkPermissionAndHandleResult()
            swipeRefreshLayout.isRefreshing = false
        }

        goUpButton.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }

        inputField.setOnEditorActionListener { v, actionId, event ->
            filterListBy(v.text)
            true
        }

        adapter = ContactAdapter { dialPhoneNumber(it.phone) }
        val viewManager = LinearLayoutManager(this)
        recyclerView.apply {
            layoutManager = viewManager
            adapter = this@MainActivity.adapter
        }

        checkPermissionAndHandleResult()
    }

    private fun filterListBy(substring: CharSequence) {
        if (::allContacts.isInitialized) {
            adapter.updateContacts(
                allContacts.filter { it.name.contains(substring, true) }
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            CONTACTS_REQUEST_PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showContacts()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.no_permission_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun checkPermissionAndHandleResult(): Unit =
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_REQUEST_PERMISSION_ID
            )
        } else {
            showContacts()
        }

    private fun showContacts() {
        allContacts = fetchAllContacts().sortedBy { it.name.toLowerCase(currentLocale) }
        adapter.updateContacts(allContacts)
    }

    companion object {
        private const val CONTACTS_REQUEST_PERMISSION_ID: Int = 13
    }
}
