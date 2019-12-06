package ru.ifmo.nefedov.task4imageslist.presenters.loaders

import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONObject
import ru.ifmo.nefedov.task4imageslist.model.Image
import ru.ifmo.nefedov.task4imageslist.model.UnsplashKeys
import ru.ifmo.nefedov.task4imageslist.views.items.ImagesAdapter
import java.net.URL

class ImageListLoader(private val imagesAdapter: ImagesAdapter) :
    AsyncTask<Any, Void, List<Image>>() {
    private val imageCount: Int = 30

    private val baseUrl: String =
        "https://api.unsplash.com/photos/" +
                "?page=1" +
                "&per_page=$imageCount" +
                "&client_id=${UnsplashKeys.ACCESS_KEY}"

    private fun parseJsonImage(jsonImage: JSONObject): Image {
        val jsonUrls = jsonImage.getJSONObject("urls")
        return Image(
            id = jsonImage.getString("id"),
            description = jsonImage.getString("description").nullIfNull(),
            author = null,//jsonImage.getString("author").nullIfNull(),
            regularUrl = jsonUrls.getString("regular"),
            smallUrl = jsonUrls.getString("small"),
            thumbUrl = jsonUrls.getString("thumb")
        )
    }

    override fun doInBackground(vararg params: Any): List<Image> {
        val result = URL(baseUrl)
            .openConnection()
            .getInputStream()
            .reader()
            .readText()

        val jsonResult = JSONArray(result)
        return List(jsonResult.length()) { parseJsonImage(jsonResult.getJSONObject(it)) }
    }

    override fun onPostExecute(result: List<Image>?) {
        if (result != null) {
            imagesAdapter.setImages(result)
        }
    }

    private fun String.nullIfNull(): String? = if (this == "null") null else this
}