package com.prush.justanotherplayer.di

import com.prush.justanotherplayer.audioplayer.AudioPlayer
import com.prush.justanotherplayer.audioplayer.ExoPlayer
import com.prush.justanotherplayer.repositories.*

/**
 * Enables Inversion of Control by depending on abstraction rather than concrete implementation.
 * Here we can pass FakeTrackRepository instead of the real one to test out the behavior of presenters.
 */
object Injection {

    fun provideTrackRepository(): ITrackRepository {
        return TrackRepository.getInstance()
    }

    fun provideAlbumRepository(): IAlbumRepository {
        return AlbumRepository.getInstance(provideTrackRepository())
    }

    fun provideArtistRepository(): IArtistRepository {
        return ArtistRepository.getInstance(provideAlbumRepository())
    }

    fun provideGenreRepository(): IGenreRepository {
        return GenreRepository.getInstance()
    }

    fun provideAudioPlayer(): AudioPlayer{
        return ExoPlayer()
    }
}