package ru.ifmo.nefedov.task4imageslist.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.presenters.MainActivityPresenter
import ru.ifmo.nefedov.task4imageslist.utils.API_KEY
import ru.ifmo.nefedov.task4imageslist.utils.UNSPLASH_API_KEY
import ru.ifmo.nefedov.task4imageslist.utils.VK_API_KEY

class MainActivity : AppCompatActivity(), MainActivityPresenter.MainView {
    private lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainActivityPresenter(this)

        main_unsplash_pick.setOnClickListener { presenter.pickApi(UNSPLASH_API_KEY) }
        main_vk_pick.setOnClickListener { presenter.pickApi(VK_API_KEY) }
    }

    override fun startPreviewActivity(apiKey: String) {
        val sendIntent = Intent(this, PreviewsActivity::class.java)
            .apply { putExtra(API_KEY, apiKey) }
        startActivity(sendIntent)
    }
}
