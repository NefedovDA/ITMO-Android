package ru.ifmo.nefedov.task4.imageslist

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun showOkDialog(message: String) {
        val builder = AlertDialog.Builder(this@MainActivity)
            .apply {
                setTitle(R.string.no_internet_connection)
                setMessage(message)
                setCancelable(false)
                setNegativeButton("OK") { dialog, _ -> dialog.cancel() }
            }
        val alert = builder.create()
        alert.show()
    }
}
