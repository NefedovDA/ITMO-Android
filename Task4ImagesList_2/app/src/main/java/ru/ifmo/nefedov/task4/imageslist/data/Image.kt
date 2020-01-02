package ru.ifmo.nefedov.task4.imageslist.data

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

interface Image : Parcelable {
    val url: String
    val description: String?
    val bitmap: Bitmap
}

data class ImageInfo(
    val smallUrl: String,
    val bigUrl: String,
    val description: String?
)

@Parcelize
class SmallImage(
    override val url: String,
    val bigVersionUrl: String,
    override val description: String?,
    override val bitmap: Bitmap
) : Image {
    constructor(imageInfo: ImageInfo, bitmap: Bitmap) : this(
        url = imageInfo.smallUrl,
        bigVersionUrl = imageInfo.bigUrl,
        description = imageInfo.description,
        bitmap = bitmap
    )
}

@Parcelize
class BigImage(
    override val url: String,
    override val description: String?,
    override val bitmap: Bitmap
) : Image {
    constructor(smallVersion: SmallImage, bitmap: Bitmap) : this(
        url = smallVersion.bigVersionUrl,
        description = smallVersion.description,
        bitmap = bitmap
    )
}