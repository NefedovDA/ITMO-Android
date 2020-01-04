package ru.ifmo.nefedov.task4.imageslist

import android.app.AlertDialog
import android.content.Context

fun Context.showOkDialog(message: String) {
    val builder = AlertDialog.Builder(this)
        .apply {
            setTitle(R.string.no_internet_connection)
            setMessage(message)
            setCancelable(false)
            setNegativeButton("OK") { dialog, _ -> dialog.cancel() }
        }
    val alert = builder.create()
    alert.show()
}

enum class State {
    NORMAL,
    FAIL
}