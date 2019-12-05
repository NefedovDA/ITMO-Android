package ru.ifmo.nefedov.task4imageslist.presenters

import ru.ifmo.nefedov.task4imageslist.model.ApiKey

class MainActivityPresenter(private val mainView: MainView) {
    fun pickApi(apiKey: ApiKey) = mainView.startPreviewActivity(apiKey)

    interface MainView {
        fun startPreviewActivity(apiKey: ApiKey)
    }
}