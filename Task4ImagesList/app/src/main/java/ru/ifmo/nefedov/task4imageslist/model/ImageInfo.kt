package ru.ifmo.nefedov.task4imageslist.model

data class ImageInfo(
    val id: String,
    val description: String?,
    val author: String?,
    val regularUrl: String,
    val smallUrl: String,
    val thumbUrl: String
)