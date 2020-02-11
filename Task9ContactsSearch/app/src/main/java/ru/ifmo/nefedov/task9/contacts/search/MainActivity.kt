package ru.ifmo.nefedov.task9.contacts.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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

    private fun hideInfoText() {
        infoTextView.visibility = View.INVISIBLE
    }

    private fun setInfoText(textId: Int) {
        infoTextView.text = getString(textId)
        infoTextView.visibility = View.VISIBLE
    }

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

        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) = filterListBy(s)
        })

        hideInfoText()

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
            val contacts =
                allContacts.filter { it.matchSubstring(substring.toString(), currentLocale) }

            adapter.updateContacts(contacts)

            if (contacts.isEmpty()) {
                setInfoText(R.string.empty_result_info)
            } else {
                hideInfoText()
            }
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
                    setInfoText(R.string.no_permission_message)
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
        hideInfoText()
        allContacts = fetchAllContacts().sortedBy { it.name.toLowerCase(currentLocale) }
        adapter.updateContacts(allContacts)
    }

    companion object {
        private const val CONTACTS_REQUEST_PERMISSION_ID: Int = 13
    }
}
