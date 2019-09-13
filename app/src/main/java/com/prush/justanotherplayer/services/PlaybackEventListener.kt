package com.prush.justanotherplayer.services

import android.media.session.PlaybackState
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class PlaybackEventListener(
    private val audioPlayer: SimpleExoPlayer,
    private val nowPlayingQueue: NowPlayingQueue
) :
    Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            PlaybackState.STATE_PLAYING, PlaybackState.STATE_PAUSED -> {

                if (audioPlayer.currentTag != null) {
                    val nowPlayingInfo: NowPlayingInfo = audioPlayer.currentTag as NowPlayingInfo
                    nowPlayingQueue.apply {
                        currentPlayingTrackId = nowPlayingInfo.id
                        nowPlayingQueue.currentPlayingTrackIndex = nowPlayingInfo.index
                    }

                }
            }
        }
    }

    override fun onPositionDiscontinuity(reason: Int) {

        if (audioPlayer.currentTag != null) {

            val nowPlayingInfo: NowPlayingInfo = audioPlayer.currentTag as NowPlayingInfo
            nowPlayingQueue.apply {
                currentPlayingTrackId = nowPlayingInfo.id
                currentPlayingTrackIndex = nowPlayingInfo.index
            }

        }
    }
}