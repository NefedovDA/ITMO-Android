package ru.ifmo.nefedov.task11.imagelist.internet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONArray
import ru.ifmo.nefedov.task11.imagelist.BuildConfig
import ru.ifmo.nefedov.task11.imagelist.cache.Cache
import ru.ifmo.nefedov.task11.imagelist.data.ImageInfo
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val MAX_PAGE_NUMBER = 100
private var pageNumber = 1
private const val PER_PAGE = 10
private const val BASE_API_URL: String = "https://api.unsplash.com/"

private fun leafPage(): Unit =
    if (pageNumber == MAX_PAGE_NUMBER) pageNumber = 1 else pageNumber += 1

private val apiUrl: String
    get() = "${BASE_API_URL}photos/?page=${pageNumber}&per_page=${PER_PAGE}&client_id=${BuildConfig.API_KEY}"


private suspend fun downloadSingleImage(url: String) {
    if (Cache.simpleCathe.containsKey(url)) {
        return
    }

    val bitmap = suspendCoroutine<Bitmap> { cont ->
        val imageBytes = URL(url).openStream().readBytes()
        val result = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        cont.resume(result)
    }

    Cache.simpleCathe.getOrPut(url) { bitmap }
}

private suspend fun downloadInfoList(): List<ImageInfo> {
    val description = suspendCoroutine<String> { cont ->
        val result = URL(apiUrl)
            .openConnection()
            .getInputStream()
            .reader()
            .readText()
        cont.resume(result)
    }

    leafPage()
    val jsonResult = JSONArray(description)

    return List(jsonResult.length()) {
        val jsonImage = jsonResult.getJSONObject(it)
        val jsonUrls = jsonImage.getJSONObject("urls")
        ImageInfo(
            bigUrl = jsonUrls.getString("regular"),
            smallUrl = jsonUrls.getString("thumb"),
            description = jsonImage.getString("description").nullIfNull()
        )
    }
}

private fun String.nullIfNull(): String? = if (this == "null") null else this


suspend fun downloadFullscreen(url: String) {
    downloadSingleImage(url)
}

suspend fun downloadPreviewList(): List<ImageInfo> {
    val imageInfoList = downloadInfoList()
    imageInfoList.forEach { downloadSingleImage(it.smallUrl) }
    return imageInfoList
}