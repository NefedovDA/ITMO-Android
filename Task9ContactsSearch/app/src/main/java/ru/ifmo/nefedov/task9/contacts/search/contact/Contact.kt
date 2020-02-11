package ru.ifmo.nefedov.task9.contacts.search.contact

import java.util.*

data class Contact(
    val name: String,
    val phone: String
) {
    fun matchSubstring(substring: String, locale: Locale): Boolean {
        return name.toLowerCase(locale).contains(substring.toLowerCase(locale)) ||
                phone.normalizeAsPhone().contains(substring.normalizeAsPhone())
    }

    private fun String.normalizeAsPhone() = replace("""[-+(\s)]""".toRegex(), "")
}

