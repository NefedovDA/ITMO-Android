package ru.ifmo.nefedov.task4imageslist.presenters.loaders

import android.os.AsyncTask
import org.json.JSONArray
import ru.ifmo.nefedov.task4imageslist.model.ApiKey
import ru.ifmo.nefedov.task4imageslist.model.ImageInfo
import java.net.URL

class ImageListLoader(apiKey: ApiKey) : AsyncTask<Any, Void, List<ImageInfo>>() {
    private val baseUrl: String = when (apiKey) {
        ApiKey.UNSPLASH_API_KEY -> TODO()
        ApiKey.VK_API_KEY -> TODO()
    }

    override fun doInBackground(vararg params: Any): List<ImageInfo> {
        val result = URL(baseUrl)
            .openConnection()
            .getInputStream()
            .reader()
            .readText()

        val jsonResult = JSONArray(result)

        return List(jsonResult.length()) {
            val jsonImage = jsonResult.getJSONObject(it)
            val jsonUrls = jsonImage.getJSONObject("urls")

            ImageInfo(
                id = jsonImage.getString("id"),
                description = jsonImage.getString("description").nullIfNull(),
                author = jsonImage.getString("author").nullIfNull(),
                regularUrl = jsonUrls.getString("regular"),
                smallUrl = jsonUrls.getString("small"),
                thumbUrl = jsonUrls.getString("thumb")
            )
        }
    }
}

private fun String.nullIfNull(): String? = if (this == "null") null else this