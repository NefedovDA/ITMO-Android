package ru.ifmo.nefedov.task4imageslist.presenters

class MainActivityPresenter(private val mainView: MainView) {
    fun pickApi(apiKey: String) = mainView.startPreviewActivity(apiKey)

    interface MainView {
        fun startPreviewActivity(apiKey: String)
    }
}