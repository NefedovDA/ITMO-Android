package ru.ifmo.nefedov.task4imageslist.presenters

import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.models.ImageLoader
import ru.ifmo.nefedov.task4imageslist.utils.UNSPLASH_API_KEY
import ru.ifmo.nefedov.task4imageslist.utils.VK_API_KEY

abstract class PreviewsActivityPresenter(protected val previewsView: PreviewsView) {
    abstract val headerTextId: Int
    protected abstract val imageLoader: ImageLoader

    interface PreviewsView {

    }

    companion object {
        @Throws(IllegalArgumentException::class)
        fun getInstance(previewsView: PreviewsView, apiKey: String): PreviewsActivityPresenter =
            when (apiKey) {
                UNSPLASH_API_KEY -> object : PreviewsActivityPresenter(previewsView) {
                    override val headerTextId: Int = R.string.unsplash_header
                    override val imageLoader: ImageLoader
                        get() = TODO("not implemented")
                }
                VK_API_KEY -> object : PreviewsActivityPresenter(previewsView) {
                    override val headerTextId: Int = R.string.vk_header
                    override val imageLoader: ImageLoader
                        get() = TODO("not implemented")

                }
                else -> throw IllegalArgumentException("No such API key: `$apiKey`")
            }
    }
}