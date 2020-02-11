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
        assertTrue(contact.matchSubstring("Vasily", locale))
        assertTrue(contact.matchSubstring("vasily", locale))
        assertTrue(contact.matchSubstring("vaSiLy", locale))
        assertTrue(contact.matchSubstring("Vas", locale))
        assertTrue(contact.matchSubstring("ly", locale))
        assertTrue(contact.matchSubstring("a", locale))
    }

    @Test
    fun `mismatching contact by name substring`() {
        assertFalse(contact.matchSubstring("z", locale))
        assertFalse(contact.matchSubstring("zzz", locale))
        assertFalse(contact.matchSubstring("zVasily", locale))
    }

    @Test
    fun `matching contact by phone substring`() {
        assertTrue(contact.matchSubstring("8 (800) 555-35-35", locale))
        assertTrue(contact.matchSubstring("88005553535", locale))
        assertTrue(contact.matchSubstring("88--  005()-55-35+35", locale))
        assertTrue(contact.matchSubstring("88005", locale))
        assertTrue(contact.matchSubstring("5553535", locale))
        assertTrue(contact.matchSubstring("5", locale))
    }

    @Test
    fun `mismatching contact by phone substring`() {
        assertFalse(contact.matchSubstring("8 (800) 855-35-35", locale))
        assertFalse(contact.matchSubstring("7-77", locale))
        assertFalse(contact.matchSubstring("880055535a35", locale))
        assertFalse(contact.matchSubstring("880*", locale))
    }
}