package ru.ifmo.nefedov.task4.imageslist.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.json.JSONArray
import ru.ifmo.nefedov.task4.imageslist.BuildConfig
import ru.ifmo.nefedov.task4.imageslist.cache.Cache
import ru.ifmo.nefedov.task4.imageslist.data.ImageInfo
import ru.ifmo.nefedov.task4.imageslist.data.SmallImage
import java.net.URL

class InternetService : IntentService("ru.ifmo.nefedov.task4.imageslist.services.InternetService") {

    private val apiUrl: String
        get() = "${BASE_API_URL}photos/?page=$pageNumber&per_page=$PER_PAGE&client_id=${BuildConfig.API_KEY}"


    override fun onHandleIntent(intent: Intent?) {
        Log.i(LOG_KEY, "onHandleIntent..")
        if (intent == null) {
            Log.e(LOG_KEY, "Intent is null")
            return
        }

        val mode = intent.action
        when (mode) {
            DOWNLOAD_PREVIEW_LIST -> downloadPreviewList(intent)
            DOWNLOAD_FULLSCREEN -> {
                val url = intent.getStringExtra(DOWNLOAD_EXTRA_KEY)
                if (url == null) {
                    Log.e(LOG_KEY, "Url fo downloading fullscreen is null")
                    return
                }
                downloadFullscreen(intent, url)
            }
            else -> Log.e(LOG_KEY, "Mode is null or has undefined value")
        }
    }

    private fun downloadSingleImage(url: String): Bitmap =
        Cache.simpleCathe.getOrPut(url) {
            val imageBytes = URL(url).openStream().readBytes()
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

    private fun downloadInfoList(): List<ImageInfo> {
        val result = URL(apiUrl)
            .openConnection()
            .getInputStream()
            .reader()
            .readText()
        leafPage()

        val jsonResult = JSONArray(result)
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

    private fun Intent.sendResult(filler: Intent.() -> Unit) {
        filler()
        Log.i(LOG_KEY, "sendBroadcast..")
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(this)
    }

    private fun downloadFullscreen(intent: Intent, url: String) {
        val bitmap = downloadSingleImage(url)
        intent.sendResult {
            putExtra(RESULT_KEY, bitmap)
        }
    }

    private fun downloadPreviewList(intent: Intent) {
        Log.i(LOG_KEY, "downloadingPreviewList..")
        val imageInfoList = downloadInfoList()
        val smallImageList = imageInfoList.map { info ->
            val bitmap = downloadSingleImage(info.smallUrl)
            SmallImage(info, bitmap)
        }
        intent.sendResult {
            putExtra(RESULT_KEY, ArrayList(smallImageList))
        }
    }

    companion object {
        private const val LOG_KEY = "InternetService"


        const val DOWNLOAD_PREVIEW_LIST = "download_list"
        const val DOWNLOAD_FULLSCREEN = "download_fullscreen"

        private const val DOWNLOAD_EXTRA_KEY = "download_extra"

        private fun runDownloading(context: Context, mod: String, extra: String? = null) {
            Log.i(LOG_KEY, "runDownloading..")
            val intent = Intent(context, InternetService::class.java).apply {
                action = mod
                extra?.let { putExtra(DOWNLOAD_EXTRA_KEY, it) }
            }

            context.startService(intent)
        }

        fun downloadPreviewList(context: Context) = runDownloading(context, DOWNLOAD_PREVIEW_LIST)

        fun downloadFullscreen(context: Context, url: String) =
            runDownloading(context, DOWNLOAD_FULLSCREEN, url)


        const val RESULT_KEY = "result_value"


        private const val MAX_PAGE_NUMBER = 100
        private var pageNumber = 1
        private const val PER_PAGE = 10
        private const val BASE_API_URL: String = "https://api.unsplash.com/"

        private fun leafPage(): Unit =
            if (pageNumber == MAX_PAGE_NUMBER) pageNumber = 1 else pageNumber += 1
    }
}