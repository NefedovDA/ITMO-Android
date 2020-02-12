package ru.ifmo.nefedov.task9.contacts.search.contact

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ContactTest {
    private val contact = Contact(
        name = "Vasily",
        phone = "8 (800) 555-35-35"
    )
    private val locale = Locale.getDefault()

    @Test
    fun `matching contact by name substring`() {
        assertTrue(contact.matchSubstring("sil", locale))
        assertTrue(contact.matchSubstring("", locale))

        assertFalse(contact.matchSubstring("zz", locale))
    }

    @Test
    fun `matching contact by phone substring`() {
        assertTrue(contact.matchSubstring("8 (800) 555-35-35", locale))
        assertTrue(contact.matchSubstring("88005553535", locale))
        assertTrue(contact.matchSubstring("88--  005()-55-35+35", locale))

        assertFalse(contact.matchSubstring("7-77", locale))
    }
}