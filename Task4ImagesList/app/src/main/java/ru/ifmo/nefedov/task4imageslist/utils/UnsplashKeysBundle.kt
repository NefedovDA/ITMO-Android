package ru.ifmo.nefedov.task4imageslist.utils

import android.content.Context
import android.content.res.AssetManager
import java.io.IOException
import java.io.InputStream
import java.util.*


object UnsplashKeysBundle {
    private const val PROPERTY_FILE_NAME: String = "unsplash.access.properties"
    private lateinit var properties: Properties

    const val ACCESS_KEY: String = "access.key"
    const val SECRET_KEY: String = "secret.key"

    @Throws(IOException::class)
    fun key(context: Context, keyName: String): String {
        if (!UnsplashKeysBundle::properties.isInitialized) {
            properties = Properties()
            val assetManager: AssetManager = context.assets
            val inputStream: InputStream = assetManager.open(PROPERTY_FILE_NAME)
            properties.load(inputStream)
        }
        return properties.getProperty(keyName)
    }
}

fun Context.getUnsplashKey(keyName: String) =
    UnsplashKeysBundle.key(this, keyName)