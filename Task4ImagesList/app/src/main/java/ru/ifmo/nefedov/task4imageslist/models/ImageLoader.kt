package ru.ifmo.nefedov.task4imageslist.models

import android.graphics.Bitmap
import java.net.URI

interface ImageLoader {
    fun loadInfoList(): List<ImageInfo>
    fun loadImage(uri: URI): Bitmap
}