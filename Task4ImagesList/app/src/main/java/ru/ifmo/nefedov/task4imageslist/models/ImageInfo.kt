package ru.ifmo.nefedov.task4imageslist.models

import java.net.URI

data class ImageInfo(
    private val rawUri: URI,
    @Deprecated("Unrecommended for using due to bad performance") val fullUrl: URI,
    val regularUri: URI,
    val smallUri: URI,
    val thumbUri: URI,
    val author: String
) {
    fun customSizeUri(width: Int, height: Int, dpi: Int): URI =
        rawUri.resolve("&w=$width&h=$height&dpi=$dpi")
}