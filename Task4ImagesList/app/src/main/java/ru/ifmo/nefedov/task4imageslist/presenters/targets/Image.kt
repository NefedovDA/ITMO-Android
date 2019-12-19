package ru.ifmo.nefedov.task4imageslist.presenters.targets

data class Image(
    val id: String,
    val urls: ImageUrls,
    val description: ImageDescription
)

data class ImageUrls(
    val small: String,
    val big: String
)

data class ImageDescription(
    val description: String?,
    val author: String?
)

fun constructImage(
    id: String,
    small: String,
    big: String,
    description: String?,
    author: String?
) = Image(id, ImageUrls(small, big), ImageDescription(description, author))