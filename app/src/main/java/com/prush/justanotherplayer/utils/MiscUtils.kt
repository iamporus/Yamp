package com.prush.justanotherplayer.utils

import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.Track_State
import java.util.*
import kotlin.math.abs

fun getTimeStringFromSeconds(secs: Long): String {

    val absSeconds = abs(secs)
    val stringBuilder = StringBuilder()
    stringBuilder.setLength(0)

    return Formatter(stringBuilder, Locale.getDefault()).format(
        "%s %d:%02d",
        if (secs < 0) "- " else "",
        absSeconds / 60,
        absSeconds % 60
    ).toString()
}

fun shuffleTracks(shuffleFromIndex: Int, tracksList: List<Track>) {

    val subList = tracksList.subList(shuffleFromIndex, tracksList.size)
    val shuffledTracks = subList.shuffled()

    (tracksList as ArrayList).apply {
        clear()
        addAll(tracksList.subList(0, shuffleFromIndex))
        addAll(shuffledTracks)
    }

    tracksList.mapIndexed { index, track ->
        when {
            index == shuffleFromIndex -> track.state = Track_State.PLAYING
            index < shuffleFromIndex -> track.state = Track_State.PLAYED
            else -> track.state = Track_State.IN_QUEUE
        }
    }

}