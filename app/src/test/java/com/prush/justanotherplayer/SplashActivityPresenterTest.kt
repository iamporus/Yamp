package com.prush.justanotherplayer

import com.prush.justanotherplayer.splash.SplashActivityPresenter
import com.prush.justanotherplayer.splash.SplashActivityView
import org.junit.Assert.assertEquals
import org.junit.Test

class SplashActivityPresenterTest {

    @Test
    fun shouldGotoMainActivity() {

        //given
        val splashActivityView: SplashActivityView = MockSplashActivityView()
        val splashActivityPresenter = SplashActivityPresenter(splashActivityView)

        //when
        splashActivityPresenter.gotoMainActivity()

        //then
        assertEquals(true, (splashActivityView as MockSplashActivityView).didGoToMainActivity)
    }

    class MockSplashActivityView : SplashActivityView {

        var didGoToMainActivity = false

        override fun gotoMainActivity() {
            didGoToMainActivity = true
        }

    }
}