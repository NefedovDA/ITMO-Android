package ru.ifmo.nefedov.task4imageslist.presenters.loaders

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONObject
import ru.ifmo.nefedov.task4imageslist.model.ApiKey
import ru.ifmo.nefedov.task4imageslist.model.Image
import ru.ifmo.nefedov.task4imageslist.model.UnsplashKeys
import ru.ifmo.nefedov.task4imageslist.views.items.ImagesAdapter
import java.net.URL

abstract class ImageListLoader(private val imagesAdapter: ImagesAdapter) :
    AsyncTask<Any, Void, List<Image>>() {
    protected val imageCount: Int = 10

    protected abstract val baseUrl: String

    protected abstract fun parseJsonImage(jsonImage: JSONObject): Image

    override fun doInBackground(vararg params: Any): List<Image> {
        val result = URL(baseUrl)
            .openConnection()
            .getInputStream()
            .reader()
            .readText()

        val jsonResult = JSONArray(result)
        return List(jsonResult.length()) { parseJsonImage(jsonResult.getJSONObject(it)) }
    }

    protected fun loadImage(url: String): Bitmap {
        val imageBytes = URL(url).openStream().readBytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    override fun onPostExecute(result: List<Image>?) {
        if (result != null) {
            imagesAdapter.setImages(result)
        }
    }

    companion object {
        operator fun invoke(apiKey: ApiKey, imagesAdapter: ImagesAdapter): ImageListLoader =
            when (apiKey) {
                ApiKey.UNSPLASH_API_KEY -> UnsplashImpl(imagesAdapter)
                ApiKey.VK_API_KEY -> TODO()
            }
    }
}

private fun String.nullIfNull(): String? = if (this == "null") null else this

private class UnsplashImpl(imagesAdapter: ImagesAdapter) : ImageListLoader(imagesAdapter) {
    override val baseUrl: String =
        "https://api.unsplash.com/photos/" +
                "?page=1" +
                "&per_page=$imageCount" +
                "&client_id=${UnsplashKeys.ACCESS_KEY}"

    override fun parseJsonImage(jsonImage: JSONObject): Image {
        val jsonUrls = jsonImage.getJSONObject("urls")
        val smallUrl = jsonUrls.getString("small")
        return Image(
            id = jsonImage.getString("id"),
            description = jsonImage.getString("description").nullIfNull(),
            author = null,//jsonImage.getString("author").nullIfNull(),
            bitmap = loadImage(smallUrl),
            regularUrl = jsonUrls.getString("regular"),
            smallUrl = smallUrl,
            thumbUrl = jsonUrls.getString("thumb")
        )
    }
}