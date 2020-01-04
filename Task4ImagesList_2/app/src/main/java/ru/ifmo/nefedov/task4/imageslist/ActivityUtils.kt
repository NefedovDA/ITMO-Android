package ru.ifmo.nefedov.task4.imageslist

import android.app.AlertDialog
import android.content.Context

fun Context.showOkDialog(title_id: Int, message_id: Int, onClickOkExtra: () -> Unit = {}) {
    val builder = AlertDialog.Builder(this)
        .apply {
            setTitle(title_id)
            setMessage(message_id)
            setCancelable(false)
            setNegativeButton("OK") { dialog, _ ->
                dialog.cancel()
                onClickOkExtra()
            }
        }
    val alert = builder.create()
    alert.show()
}

enum class State {
    NORMAL,
    FAIL
}