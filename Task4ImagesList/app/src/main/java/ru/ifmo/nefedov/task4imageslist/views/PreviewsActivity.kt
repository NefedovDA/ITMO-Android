package ru.ifmo.nefedov.task4imageslist.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.presenters.PreviewsActivityPresenter
import ru.ifmo.nefedov.task4imageslist.utils.API_KEY

class PreviewsActivity : AppCompatActivity(), PreviewsActivityPresenter.PreviewsView {
    private lateinit var presenter: PreviewsActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previews)

        val apiKey = intent.getStringExtra(API_KEY)
        assert(apiKey != null) { "Open this Activity with apiKey!" }
        presenter = PreviewsActivityPresenter.getInstance(this, apiKey!!)

        title = getString(presenter.headerTextId)
    }
}