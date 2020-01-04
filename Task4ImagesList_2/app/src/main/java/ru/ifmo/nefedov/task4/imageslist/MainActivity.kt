package ru.ifmo.nefedov.task4.imageslist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ifmo.nefedov.task4.imageslist.data.SmallImage
import ru.ifmo.nefedov.task4.imageslist.imageListView.ImageAdapter
import ru.ifmo.nefedov.task4.imageslist.services.InternetService

class MainActivity : AppCompatActivity() {
    private lateinit var receiver: BroadcastReceiver
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(LOG_KEY, "onCreate..")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewManager = LinearLayoutManager(this)
        adapter = ImageAdapter(::openFullscreen)
        main_imagePreviewList.apply {
            layoutManager = viewManager
            adapter = this@MainActivity.adapter
        }

        startDownloadingImagePreviewList()
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
        main_progress.visibility = View.VISIBLE
        main_imagePreviewList.visibility = View.INVISIBLE

        InternetService.downloadPreviewList(this@MainActivity)
    }

    private fun openFullscreen(image: SmallImage) {
        val intent = Intent(this@MainActivity, FullscreenActivity::class.java).apply {
            putExtra(FullscreenActivity.IMAGE_INFO_KEY, image.getImageInfo())
        }
        startActivity(intent)
    }

    private inner class MainReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(LOG_KEY, "onReceive..")
            if (intent == null) {
                Log.e(LOG_KEY, "Intent is null")
                return
            }

            val images: List<SmallImage>? =
                intent.getParcelableArrayListExtra(InternetService.RESULT_KEY)

            if (images == null) {
                Log.e(LOG_KEY, "result is null or has undefined value")
                return
            }

            main_progress.visibility = View.INVISIBLE
            main_imagePreviewList.visibility = View.VISIBLE

            adapter.setImages(images)
        }

    }

    companion object {
        private const val LOG_KEY = "MainActivity"
    }
}
