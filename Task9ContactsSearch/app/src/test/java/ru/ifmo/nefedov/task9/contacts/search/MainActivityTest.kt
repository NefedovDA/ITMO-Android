package ru.ifmo.nefedov.task9.contacts.search

import android.app.Activity
import android.content.ContentResolver
import android.provider.ContactsContract
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.rule.GrantPermissionRule
import com.google.android.material.textfield.TextInputEditText
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboCursor
import ru.ifmo.nefedov.task9.contacts.search.contact.Contact
import ru.ifmo.nefedov.task9.contacts.search.contact.ContactAdapter


@Config(sdk = [Config.OLDEST_SDK])
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)

    @Before
    fun setUp() {
        contactsRoboCursor = RoboCursor().apply {
            setColumnNames(CONTACTS_COLUMNS)
            setResults(
                contacts.map {
                    arrayOf(it.name, it.phone)
                }.toTypedArray()
            )
        }

        contentResolver = mock {
            on {
                query(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            } doReturn contactsRoboCursor
        }

        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.get()

        shadowOf(activity.contentResolver).setCursor(contactsRoboCursor)
    }

    @After
    fun cleanUp() {
        activityController.stop()
    }

    @Test
    fun `validate contacts after start activity`() {
        activity = activityController.create().get()

        checkRecyclerViewState(contacts)
    }

    @Test
    fun `search contact AAA by name`() {
        activity = activityController.create().get()

        setSearchText("aaa")
        checkRecyclerViewState(CONTACT_AAA_900, CONTACT_AAA_901, CONTACT_aaa_902)
    }

    @Test
    fun `search contact BoB by part of name`() {
        activity = activityController.create().get()

        setSearchText("b")
        checkRecyclerViewState(CONTACT_BoB_900)
    }

    @Test
    fun `search contact by number`() {
        activity = activityController.create().get()

        setSearchText("900")
        checkRecyclerViewState(CONTACT_AAA_900, CONTACT_BoB_900)
    }

    @Test
    fun `search specific contact by primitive number`() {
        activity = activityController.create().get()

        setSearchText("700")
        checkRecyclerViewState(CONTACT_LoL_SPECIFIC)
    }

    @Test
    fun `search undefined contact`() {
        activity = activityController.create().get()

        setSearchText("zzz")
        checkRecyclerViewState()
        checkInfoMessage()
    }

    private fun checkInfoMessage() {
        val infoTextView: TextView = activity.findViewById<TextView?>(R.id.infoTextView)
            .checkThatNotNull()
        assertEquals(infoTextView.visibility, View.VISIBLE)
    }

    private fun setSearchText(text: String) {
        val inputField: TextInputEditText =
            activity.findViewById<TextInputEditText?>(R.id.inputField)
                .checkThatNotNull()
        inputField.setText(text)
    }


    private fun checkRecyclerViewState(vararg expectedContacts: Contact) =
        checkRecyclerViewState(expectedContacts.toList())

    private fun checkRecyclerViewState(expectedContacts: List<Contact>) {
        val recyclerView: RecyclerView = activity.findViewById<RecyclerView?>(R.id.recyclerView)
            .checkThatNotNull()
        val adapter: ContactAdapter = recyclerView.adapter
            .checkThatNotNull()
            .checkType()

        assertEquals(adapter.contacts, expectedContacts)
    }


    companion object {
        private val CONTACTS_COLUMNS = listOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        private val CONTACT_AAA_900 = Contact("AAA", "900")
        private val CONTACT_AAA_901 = Contact("AAA", "901")
        private val CONTACT_aaa_902 = Contact("aaa", "902")
        private val CONTACT_BoB_900 = Contact("BoB", "900")
        private val CONTACT_LoL_SPECIFIC = Contact("LoL", "+7 --- 0+ 0")

        private val contacts: List<Contact> = listOf(
            CONTACT_AAA_900,
            CONTACT_AAA_901,
            CONTACT_aaa_902,
            CONTACT_BoB_900,
            CONTACT_LoL_SPECIFIC
        ).sortedBy { it.name.toLowerCase() }

        private lateinit var activity: Activity
        private lateinit var activityController: ActivityController<MainActivity>

        private lateinit var contactsRoboCursor: RoboCursor
        private lateinit var contentResolver: ContentResolver
    }
}