package ru.ifmo.nefedov.task4imageslist

import android.app.Application
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import ru.ifmo.nefedov.task4imageslist.model.UnsplashKeys.ACCESS_KEY
import ru.ifmo.nefedov.task4imageslist.model.UnsplashKeys.SECRET_KEY

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UnsplashPhotoPicker.init(
            this,
            ACCESS_KEY,
            SECRET_KEY
        ).setLoggingEnabled(true)
    }
}