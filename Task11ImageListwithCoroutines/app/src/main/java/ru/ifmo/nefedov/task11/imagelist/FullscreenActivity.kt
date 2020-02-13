package ru.ifmo.nefedov.task11.imagelist

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_fullscreen.*
import kotlinx.coroutines.*
import ru.ifmo.nefedov.task11.imagelist.cache.Cache
import ru.ifmo.nefedov.task11.imagelist.data.ImageInfo
import ru.ifmo.nefedov.task11.imagelist.internet.downloadFullscreen
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class FullscreenActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var imageInfo: ImageInfo

    private var state = State.WAIT
    private var currentJob: Job? = null

    private fun setOnWait() {
        state = State.WAIT

        fullscreen_progress.visibility = View.VISIBLE
        fullscreen_image_group.visibility = View.INVISIBLE
    }

    private fun setOnShow() {
        state = State.SHOW

        fullscreen_progress.visibility = View.INVISIBLE
        fullscreen_image_group.visibility = View.VISIBLE
    }

    private fun setOnFail() {
        state = State.FAIL

        fullscreen_progress.visibility = View.INVISIBLE
        fullscreen_image_group.visibility = View.INVISIBLE
    }

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

        if (state != State.SHOW) {
            runDownloadingFullscreenImage()
        } else {
            val bitmap: Bitmap? = Cache.simpleCathe[imageInfo.bigUrl]
            if (bitmap == null) {
                Log.e(LOG_KEY, "Saved state is SHOW, but image not in cache")
                runDownloadingFullscreenImage()
                return
            }
            fillViewContent(bitmap)
        }
    }

    private fun runDownloadingFullscreenImage() {
        setOnWait()
        currentJob = launch {
            try {
                val url = imageInfo.bigUrl
                withContext(Dispatchers.IO) { downloadFullscreen(url) }
                val bitmap = Cache.simpleCathe[url]
                if (bitmap != null) {
                    setOnShow()
                    fillViewContent(bitmap)
                } else {
                    Log.e(LOG_KEY, "result is not null but bitmap not in the cache")
                }
            } catch (e: IOException) {
                setOnFail()
                showOkDialog(
                    R.string.no_internet_connection_title,
                    R.string.no_internet_connection_text
                ) {
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentJob?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_KEY, state)
    }

    private fun fillViewContent(bitmap: Bitmap) {
        setOnShow()
        fullscreen_image.setImageBitmap(bitmap)
        imageInfo.description?.let { fullscreen_description.text = it }
    }

    companion object {
        private const val LOG_KEY = "FullscreenActivity"

        private const val STATE_KEY = "FullscreenActivity_State"

        const val IMAGE_INFO_KEY = "ImageInfo"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}