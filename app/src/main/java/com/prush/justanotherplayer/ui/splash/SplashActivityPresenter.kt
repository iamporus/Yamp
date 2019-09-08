package com.prush.justanotherplayer.ui.splash

class SplashActivityPresenter(splashActivityView: SplashActivityView) {

    private var view: SplashActivityView = splashActivityView

    fun gotoMainActivity() {
        view.gotoMainActivity()
    }

}
