package com.prush.justanotherplayer.audioplayer

import android.media.session.PlaybackState
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.prush.justanotherplayer.queue.NowPlayingInfo
import com.prush.justanotherplayer.queue.NowPlayingQueue

class PlaybackEventListener(
    private val audioPlayer: SimpleExoPlayer,
    private val nowPlayingQueue: NowPlayingQueue
) :
    Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            PlaybackState.STATE_PLAYING, PlaybackState.STATE_PAUSED -> {

                updateNowPlaying()
            }
        }
    }

    override fun onPositionDiscontinuity(reason: Int) {

        updateNowPlaying()
    }

    private fun updateNowPlaying() {
        if (audioPlayer.currentTag != null) {
            val nowPlayingInfo: NowPlayingInfo = audioPlayer.currentTag as NowPlayingInfo
            nowPlayingQueue.apply {
                currentPlayingTrackId = nowPlayingInfo.id
                nowPlayingTracksList.forEachIndexed { index, track ->
                    if (track.id == currentPlayingTrackId) {
                        currentPlayingTrackIndex = index
                        return@forEachIndexed
                    }
                }
            }

        }
    }

}