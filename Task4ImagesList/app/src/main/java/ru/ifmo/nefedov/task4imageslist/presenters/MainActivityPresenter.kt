package ru.ifmo.nefedov.task4imageslist.presenters

import android.os.AsyncTask
import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.presenters.loaders.ImageListLoader
import ru.ifmo.nefedov.task4imageslist.views.items.ImagesAdapter

class MainActivityPresenter(private val mainView: MainView) {
    val headerTextId: Int = R.string.unsplash_header

    val imagesAdapter = ImagesAdapter()


    private val imageListLoader = ImageListLoader(imagesAdapter)

    private var currentLoadImageListTask: AsyncTask<Any, Void, List<Image>>? = null


    fun loadImageList() {
        if (currentLoadImageListTask == null) {
            imageListLoader.execute()
        }
    }

    interface MainView {

    }
}