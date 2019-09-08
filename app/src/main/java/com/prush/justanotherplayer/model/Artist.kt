package com.prush.justanotherplayer.model

import com.prush.justanotherplayer.R
import java.io.Serializable

data class Artist(
    var artistId: Long,
    var artistName: String
) : Serializable {

    var albumsList: MutableList<Album> = mutableListOf()
    val defaultAlbumArtRes: Int = R.drawable.playback_track_icon

    override fun equals(other: Any?): Boolean {
        return artistId == (other as Artist).artistId
    }

    override fun hashCode(): Int {
        return artistId.hashCode()
    }
}