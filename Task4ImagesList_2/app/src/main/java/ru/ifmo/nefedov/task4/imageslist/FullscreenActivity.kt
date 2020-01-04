package ru.ifmo.nefedov.task4.imageslist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_fullscreen.*
import ru.ifmo.nefedov.task4.imageslist.data.ImageInfo
import ru.ifmo.nefedov.task4.imageslist.services.InternetService

class FullscreenActivity : AppCompatActivity() {

    private lateinit var receiver: BroadcastReceiver
    private lateinit var imageInfo: ImageInfo

    private var state = State.WAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(LOG_KEY, "onCreate..")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        savedInstanceState?.getParcelable<State>(STATE_KEY)?.let { state = it }

        val imageInfo: ImageInfo? = intent?.getParcelableExtra(IMAGE_INFO_KEY)

        if (imageInfo == null) {
            Log.e(LOG_KEY, "Start activity without small image")

            showOkDialog(
                R.string.logic_error_title,
                R.string.no_intent_field_error_text
            ) {
                finish()
            }
            return
        }

        this.imageInfo = imageInfo

        if (state == State.WAIT) {
            runDownloadingFullscreenImage()
        }
    }

    private fun runDownloadingFullscreenImage() {
        fullscreen_progress.visibility = View.VISIBLE
        fullscreen_image_group.visibility = View.INVISIBLE
        InternetService.downloadFullscreen(this@FullscreenActivity, imageInfo.bigUrl)
    }

    override fun onResume() {
        receiver = FullscreenReceiver()

        val intentFilter = IntentFilter().apply {
            addAction(InternetService.DOWNLOAD_FULLSCREEN)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

        super.onResume()

        if (state == State.WAIT) {
            runDownloadingFullscreenImage()
        }
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_KEY, state)
    }

    private inner class FullscreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (state != State.WAIT) {
                return
            }

            if (intent == null) {
                Log.e(LOG_KEY, "Intent is null")
                return
            }

            val bitmap: Bitmap? =
                intent.getParcelableExtra(InternetService.RESULT_KEY)

            if (bitmap == null) {
                Log.e(LOG_KEY, "result is null or has undefined value")
                return
            }

            state = State.SHOW

            fullscreen_image.setImageBitmap(bitmap)
            imageInfo.description?.let { fullscreen_description.text = it }

            fullscreen_progress.visibility = View.INVISIBLE
            fullscreen_image_group.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val LOG_KEY = "FullscreenActivity"

        private const val STATE_KEY = "State"

        const val IMAGE_INFO_KEY = "ImageInfo"
    }
}