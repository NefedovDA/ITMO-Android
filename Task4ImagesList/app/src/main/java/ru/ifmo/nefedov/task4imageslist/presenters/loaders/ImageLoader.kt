package ru.ifmo.nefedov.task4imageslist.presenters.loaders

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import ru.ifmo.nefedov.task4imageslist.presenters.targets.Image
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class ImageLoader : AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg params: String): Bitmap {
        assert(params.size == 2) { "Incorrect count of arguments" }
        val imageBytes = URL(params[0]).openStream().readBytes()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    companion object {
        private val cache = ConcurrentHashMap<String, Image>()
    }
}