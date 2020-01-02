package ru.ifmo.nefedov.task4.imageslist

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log

class InternetService : IntentService("ru.ifmo.nefedov.task4.imageslist.InternetService") {
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            Log.e(LOG_KEY, "Intent is null")
            return
        }

        val mode = intent.getStringExtra(DOWNLOAD_MODE_KEY)
        when (mode) {
            DOWNLOAD_PREVIEW_LIST -> downloadPreviewList()
            DOWNLOAD_FULLSCREEN -> {
                val url = intent.getStringExtra(DOWNLOAD_EXTRA_KEY)
                if (url == null) {
                    Log.e(LOG_KEY, "Url fo downloading fullscreen is null")
                    return
                }
                downloadFullscreen(url)
            }
            else -> Log.e(LOG_KEY, "Mode is null or has undefined value")
        }
    }

    private fun downloadSingleImage(url: String): Bitmap {
        TODO()
    }

    private fun downloadInfoList(): List<Any> {
        TODO()
    }

    private fun downloadFullscreen(url: String) {
        // TODO
    }

    private fun downloadPreviewList() {
        // TODO
    }

    companion object {
        private const val LOG_KEY = "InternetService"


        private const val DOWNLOAD_MODE_KEY = "download_mode"
        private const val DOWNLOAD_PREVIEW_LIST = "download_list"
        private const val DOWNLOAD_FULLSCREEN = "download_fullscreen"

        private const val DOWNLOAD_EXTRA_KEY = "download_extra"

        private fun runDownloading(context: Context, mode: String, extra: String? = null) {
            val intent = Intent(context, InternetService::class.java).apply {
                putExtra(DOWNLOAD_MODE_KEY, mode)
                extra?.let { putExtra(DOWNLOAD_EXTRA_KEY, it) }
            }

            context.startService(intent)
        }

        fun downloadPreviewList(context: Context) = runDownloading(context, DOWNLOAD_PREVIEW_LIST)
        fun downloadFullscreen(context: Context, url: String) =
            runDownloading(context, DOWNLOAD_FULLSCREEN, url)


        const val RESULT_KEY = "result_value"


        private var pageNumber = 1
        private const val PER_PAGE = 10
        private const val BASE_API_URL: String = "https://api.unsplash.com/"
        private val API_URL: String =
            "${BASE_API_URL}photos/?page=${pageNumber}&per_page=${PER_PAGE}&client_id=${BuildConfig.API_KEY}"
    }
}