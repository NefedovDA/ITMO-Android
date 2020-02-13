package ru.ifmo.nefedov.task11.imagelist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import ru.ifmo.nefedov.task11.imagelist.data.ImageInfo
import ru.ifmo.nefedov.task11.imagelist.data.SmallImage
import ru.ifmo.nefedov.task11.imagelist.data.convertToSmallImageList
import ru.ifmo.nefedov.task11.imagelist.imageListView.ImageAdapter
import ru.ifmo.nefedov.task11.imagelist.internet.downloadPreviewList
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var adapter: ImageAdapter

    private var state = State.WAIT
    private var currentJob: Job? = null

    private fun setOnWait() {
        state = State.WAIT

        main_progress.visibility = View.VISIBLE
        main_refresh.visibility = View.INVISIBLE
        main_imagePreviewList.visibility = View.VISIBLE
    }

    private fun setOnShow() {
        state = State.SHOW

        main_progress.visibility = View.INVISIBLE
        main_refresh.visibility = View.VISIBLE
        main_imagePreviewList.visibility = View.VISIBLE
    }

    private fun setOnFail() {
        state = State.FAIL

        main_progress.visibility = View.INVISIBLE
        main_refresh.visibility = View.VISIBLE
        main_imagePreviewList.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(LOG_KEY, "onCreate..")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.getParcelable<State>(STATE_KEY)?.let { state = it }

        main_refresh.setOnRefreshListener(::onRefresh)

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

            adapter.setImages(
                imageInfoList.convertToSmallImageList(LOG_KEY)
            )
        }
    }

    private fun onRefresh() {
        if (state != State.WAIT) {
            startDownloadingImagePreviewList()
        }
        main_refresh.isRefreshing = false
    }

    private fun startDownloadingImagePreviewList() {
        Log.i(LOG_KEY, "startDownloadingImagePreviewList..")
        setOnWait()
        currentJob = launch {
            try {
                val images = withContext(Dispatchers.IO) { downloadPreviewList() }
                setOnShow()
                adapter.setImages(
                    images.convertToSmallImageList(LOG_KEY)
                )
            } catch (e: IOException) {
                setOnFail()
                showOkDialog(
                    R.string.no_internet_connection_title,
                    R.string.no_internet_connection_text
                )
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        currentJob?.cancel()
    }

    companion object {
        private const val LOG_KEY = "MainActivity"

        private const val STATE_KEY = "MainActivity_State"
        private const val PREVIEW_LIST_KEY = "MainActivity_PreviewList"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}
