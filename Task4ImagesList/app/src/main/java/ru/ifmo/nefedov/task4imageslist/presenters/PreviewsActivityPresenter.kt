package ru.ifmo.nefedov.task4imageslist.presenters

import android.os.AsyncTask
import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.model.ApiKey
import ru.ifmo.nefedov.task4imageslist.model.Image
import ru.ifmo.nefedov.task4imageslist.presenters.loaders.ImageListLoader
import ru.ifmo.nefedov.task4imageslist.views.items.ImagesAdapter


class PreviewsActivityPresenter(private val previewsView: PreviewsView, apiKey: ApiKey) {
    val headerTextId: Int = when (apiKey) {
        ApiKey.UNSPLASH_API_KEY -> R.string.unsplash_header
        ApiKey.VK_API_KEY -> R.string.vk_header
    }

    val imagesAdapter = ImagesAdapter()


    private val imageListLoader = ImageListLoader(apiKey, imagesAdapter)

    private var currentLoadImageListTask: AsyncTask<Any, Void, List<Image>>? = null


    fun loadImageList() {
        if (currentLoadImageListTask == null) {
            imageListLoader.execute()
        }
    }

    interface PreviewsView {

    }
}