package ru.ifmo.nefedov.task11.imagelist.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.json.JSONArray
import ru.ifmo.nefedov.task11.imagelist.BuildConfig
import ru.ifmo.nefedov.task11.imagelist.MainActivity
import ru.ifmo.nefedov.task11.imagelist.cache.Cache
import ru.ifmo.nefedov.task11.imagelist.data.ImageInfo
import java.io.IOException
import java.net.URL


class InternetService : IntentService("ru.ifmo.nefedov.task4.imageslist.services.InternetService") {

    private val apiUrl: String
        get() = "${BASE_API_URL}photos/?page=$PAGE_NUMBER&per_page=$PER_PAGE&client_id=${BuildConfig.API_KEY}"


    override fun onHandleIntent(intent: Intent?) {
        Log.i(LOG_KEY, "onHandleIntent..")

        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Loading image")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        Thread.sleep(5000)

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )

            if (manager == null) {
                Log.e(LOG_KEY, "notification manager is null")
                return
            }

            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun downloadSingleImage(url: String) {
        Cache.simpleCathe.getOrPut(url) {
            val imageBytes = URL(url).openStream().readBytes()
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
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

        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    private fun downloadFullscreen(intent: Intent, url: String) {
        try {
            downloadSingleImage(url)
            intent.sendResult {
                putExtra(RESULT_KEY, url)
            }
        } catch (e: IOException) {
            intent.sendResult {
                putExtra(FAIL_KEY, e)
            }
        }
    }

    private fun downloadPreviewList(intent: Intent) {
        try {
            val imageInfoList = downloadInfoList()
            imageInfoList.forEach { downloadSingleImage(it.smallUrl) }
            intent.sendResult {
                putExtra(RESULT_KEY, ArrayList(imageInfoList))
            }
        } catch (e: IOException) {
            intent.sendResult {
                putExtra(FAIL_KEY, e)
            }
        }
    }

    companion object {
        private const val LOG_KEY = "InternetService"

        private const val CHANNEL_ID = "InternetService.Chanel"
        private const val NOTIFICATION_ID = 1119191929


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
        const val FAIL_KEY = "fail_value"


        private const val MAX_PAGE_NUMBER = 100
        private var PAGE_NUMBER = 1
        private const val PER_PAGE = 10
        private const val BASE_API_URL: String = "https://api.unsplash.com/"

        private fun leafPage(): Unit =
            if (PAGE_NUMBER == MAX_PAGE_NUMBER) PAGE_NUMBER = 1 else PAGE_NUMBER += 1
    }
}