package com.prush.justanotherplayer.di

import com.prush.justanotherplayer.repositories.ITrackRepository
import com.prush.justanotherplayer.repositories.TrackRepository

/**
 * Enables Inversion of Control by depending on abstraction rather than concrete implementation.
 * Here we can pass FakeTrackRepository instead of the real one to test out the behavior of presenters.
 */
object Injection {

    fun provideTrackRepository(): ITrackRepository {
        return TrackRepository.getInstance()
    }
}