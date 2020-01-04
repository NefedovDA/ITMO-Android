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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ifmo.nefedov.task4.imageslist.cache.Cache
import ru.ifmo.nefedov.task4.imageslist.data.ImageInfo
import ru.ifmo.nefedov.task4.imageslist.data.SmallImage
import ru.ifmo.nefedov.task4.imageslist.imageListView.ImageAdapter
import ru.ifmo.nefedov.task4.imageslist.services.InternetService

class MainActivity : AppCompatActivity() {

    private lateinit var receiver: BroadcastReceiver
    private lateinit var adapter: ImageAdapter

    private var state = State.WAIT

    private fun setOnWait() {
        state = State.WAIT

        main_progress.visibility = View.VISIBLE
        main_imagePreviewList.visibility = View.INVISIBLE
    }

    private fun setOnShow() {
        state = State.SHOW

        main_progress.visibility = View.INVISIBLE
        main_imagePreviewList.visibility = View.VISIBLE
    }

    private fun setOnFail() {
        state = State.FAIL

        main_progress.visibility = View.INVISIBLE
        main_imagePreviewList.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(LOG_KEY, "onCreate..")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.getParcelable<State>(STATE_KEY)?.let { state = it }

        val viewManager = LinearLayoutManager(this)
        adapter = ImageAdapter(::openFullscreen)
        main_imagePreviewList.apply {
            layoutManager = viewManager
            adapter = this@MainActivity.adapter
        }

        if (state != State.SHOW) {
            startDownloadingImagePreviewList()
        } else {
            val imageInfoList =
                savedInstanceState?.getParcelableArrayList<ImageInfo>(PREVIEW_LIST_KEY)

            if (imageInfoList == null) {
                Log.e(LOG_KEY, "State is SHOW, but no saved images")
                startDownloadingImagePreviewList()
                return
            }

            setOnShow()

            val images = imageInfoList.mapNotNull { info ->
                val bitmap: Bitmap? = Cache.simpleCathe[info.smallUrl]
                if (bitmap == null) {
                    Log.e(LOG_KEY, "Image with url `${info.smallUrl}` not in cache")
                    null
                } else {
                    SmallImage(info, bitmap)
                }
            }

            adapter.setImages(images)
        }
    }

    override fun onResume() {
        Log.i(LOG_KEY, "onStart..")
        receiver = MainReceiver()

        val intentFilter = IntentFilter().apply {
            addAction(InternetService.DOWNLOAD_PREVIEW_LIST)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onPause()
    }

    private fun startDownloadingImagePreviewList() {
        Log.i(LOG_KEY, "startDownloadingImagePreviewList..")
        setOnWait()
        InternetService.downloadPreviewList(this@MainActivity)
    }

    private fun openFullscreen(image: SmallImage) {
        val intent = Intent(this@MainActivity, FullscreenActivity::class.java).apply {
            putExtra(FullscreenActivity.IMAGE_INFO_KEY, image.getImageInfo())
        }
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_KEY, state)
        val images = adapter.getImages().map { it.getImageInfo() }
        outState.putParcelableArrayList(PREVIEW_LIST_KEY, ArrayList(images))
    }

    private inner class MainReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (state != State.WAIT) {
                return
            }

            Log.i(LOG_KEY, "onReceive..")
            if (intent == null) {
                Log.e(LOG_KEY, "Intent is null")
                return
            }

            setOnShow()

            val images: List<SmallImage>? =
                intent.getParcelableArrayListExtra(InternetService.RESULT_KEY)

            if (images == null) {
                Log.e(LOG_KEY, "result is null or has undefined value")
                return
            }

            adapter.setImages(images)
        }

    }

    companion object {
        private const val LOG_KEY = "MainActivity"

        private const val STATE_KEY = "MainActivity_State"
        private const val PREVIEW_LIST_KEY = "MainActivity_PreviewList"
    }
}
