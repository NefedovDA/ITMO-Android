package ru.ifmo.nefedov.task4imageslist.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.model.ApiKey
import ru.ifmo.nefedov.task4imageslist.model.ApiKey.Companion.API_KEY
import ru.ifmo.nefedov.task4imageslist.presenters.PreviewsActivityPresenter

class PreviewsActivity : AppCompatActivity(), PreviewsActivityPresenter.PreviewsView {
    private lateinit var presenter: PreviewsActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previews)

        val apiKey = intent.getSerializableExtra(API_KEY) as ApiKey

        presenter = PreviewsActivityPresenter(this, apiKey)

        title = getString(presenter.headerTextId)
    }
}