package com.prush.justanotherplayer

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.ITrackRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class MainActivityPresenterTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var mainActivityPresenter: MainActivityPresenter
    private lateinit var trackRepository: ITrackRepository
    private val mainActivityView = mock<IMainActivityView> {}

    @Before
    fun setUp() {
        mainActivityPresenter = MainActivityPresenter(mainActivityView, trackRepository)
    }

    @Test
    fun shouldDisplayAllTracks() {

        val manyTracks = mutableListOf(
            Track("First"),
            Track("Second"),
            Track("Third")
        )

        trackRepository = mock {
            on { getAllTracks() } doReturn manyTracks
        }

        mainActivityPresenter.displayAllTracks()

        verify(mainActivityView).displayLibraryTracks(manyTracks)
    }

    @Test
    fun shouldDisplayNoTracks() {

        trackRepository = mock {
            on { getAllTracks() } doReturn mutableListOf()
        }

        mainActivityPresenter.displayAllTracks()

        verify(mainActivityView).displayEmptyLibrary()
    }
}