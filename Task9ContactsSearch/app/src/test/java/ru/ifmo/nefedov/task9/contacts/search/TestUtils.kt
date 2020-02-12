package ru.ifmo.nefedov.task9.contacts.search

import org.junit.Assert.*

fun <T> T?.checkThatNotNull(): T {
    assertNotNull(this)
    return this!!
}

inline fun <reified T> Any.checkType(): T {
    assertTrue(this is T)
    return this as T
}