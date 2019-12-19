package ru.ifmo.nefedov.task4imageslist.views.items

import ru.ifmo.nefedov.task4imageslist.presenters.targets.ImageDescription

data class ImageForView(
    val id: String,
    val description: ImageDescription
)