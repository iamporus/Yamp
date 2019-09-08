package com.prush.justanotherplayer.ui.main

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.ITrackRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class MainActivityPresenterTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var mainActivityPresenter: MainActivityPresenter
    private val mainActivityView = mock<IMainActivityView> {}

    @Before
    fun setUp() {

    }

    @Test
    fun shouldDisplayAllTracks() {

        val manyTracks = mutableListOf(
            Mockito.mock(Track::class.java),
            Mockito.mock(Track::class.java),
            Mockito.mock(Track::class.java)
        )

        val trackRepository = mock<ITrackRepository> {
            on { getAllTracks() } doReturn manyTracks
        }

        mainActivityPresenter = MainActivityPresenter(
            mainActivityView,
            trackRepository
        )
        mainActivityPresenter.loadLibraryTracks()

        verify(mainActivityView).displayLibraryTracks(manyTracks)
    }

    @Test
    fun shouldDisplayNoTracks() {

        val trackRepository = mock<ITrackRepository> {
            on { getAllTracks() } doReturn mutableListOf()
        }

        mainActivityPresenter = MainActivityPresenter(
            mainActivityView,
            trackRepository
        )
        mainActivityPresenter.loadLibraryTracks()

        verify(mainActivityView).displayEmptyLibrary()
    }

    @Test
    fun shouldHandleExceptions() {

        val trackRepository = mock<ITrackRepository> {
            on { getAllTracks() } doThrow RuntimeException("Boom")
        }

        mainActivityPresenter = MainActivityPresenter(
            mainActivityView,
            trackRepository
        )
        mainActivityPresenter.loadLibraryTracks()

        verify(mainActivityView).displayError()
    }

}