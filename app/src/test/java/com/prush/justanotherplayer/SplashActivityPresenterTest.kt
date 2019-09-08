package com.prush.justanotherplayer

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.prush.justanotherplayer.ui.splash.SplashActivityPresenter
import com.prush.justanotherplayer.ui.splash.SplashActivityView
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class SplashActivityPresenterTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Test
    fun shouldGotoMainActivity() {

        //given
        val splashActivityView = mock<SplashActivityView> {}

        val splashActivityPresenter = SplashActivityPresenter(splashActivityView)

        //when
        splashActivityPresenter.gotoMainActivity()

        //then
        verify(splashActivityView).gotoMainActivity()
    }

}