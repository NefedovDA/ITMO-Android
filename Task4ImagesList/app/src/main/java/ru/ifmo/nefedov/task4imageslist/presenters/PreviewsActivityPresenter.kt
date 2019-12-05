package ru.ifmo.nefedov.task4imageslist.presenters

import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.model.ApiKey
import ru.ifmo.nefedov.task4imageslist.presenters.loaders.ImageListLoader


class PreviewsActivityPresenter(private val previewsView: PreviewsView, apiKey: ApiKey) {
    val headerTextId: Int = when (apiKey) {
        ApiKey.UNSPLASH_API_KEY -> R.string.unsplash_header
        ApiKey.VK_API_KEY -> R.string.vk_header
    }
    private val imageListLoader: ImageListLoader = TODO()

    interface PreviewsView {

    }
}