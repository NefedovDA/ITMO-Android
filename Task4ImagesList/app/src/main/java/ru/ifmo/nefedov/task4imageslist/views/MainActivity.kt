package ru.ifmo.nefedov.task4imageslist.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.presenters.MainActivityPresenter

class MainActivity : AppCompatActivity(), MainActivityPresenter.MainView {
    private lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainActivityPresenter(this)

        title = getString(presenter.headerTextId)

        main_images.setHasFixedSize(true)
        main_images.itemAnimator = null
        main_images.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        main_images.adapter = presenter.imagesAdapter

        presenter.loadImageList()
    }
}
