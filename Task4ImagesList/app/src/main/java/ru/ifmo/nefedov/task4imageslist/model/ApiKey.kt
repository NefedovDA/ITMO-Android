package ru.ifmo.nefedov.task4imageslist.model

enum class ApiKey {
    UNSPLASH_API_KEY,
    VK_API_KEY;

    companion object {
        const val API_KEY: String = "ru.ifmo.nefedov.task4imageslist.keys.API_KEY"
    }
}