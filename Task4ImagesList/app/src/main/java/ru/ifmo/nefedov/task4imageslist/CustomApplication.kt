package ru.ifmo.nefedov.task4imageslist

import android.app.Application
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import ru.ifmo.nefedov.task4imageslist.model.UnsplashKeysBundle.ACCESS_KEY
import ru.ifmo.nefedov.task4imageslist.model.UnsplashKeysBundle.SECRET_KEY
import ru.ifmo.nefedov.task4imageslist.model.getUnsplashKey

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UnsplashPhotoPicker.init(
            this,
            getUnsplashKey(ACCESS_KEY),
            getUnsplashKey(SECRET_KEY)
        ).setLoggingEnabled(true)
    }
}