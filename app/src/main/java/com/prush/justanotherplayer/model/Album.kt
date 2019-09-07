package com.prush.justanotherplayer.model

import com.prush.justanotherplayer.R
import java.io.Serializable

data class Album(
    val albumId: Long,
    var albumName: String,
    var artistId: Long,
    var artistName: String
) : Serializable {

    var defaultAlbumArtRes: Int = R.drawable.playback_track_icon

    var tracksList: MutableList<Track> = mutableListOf()

    object Builder {

        var albumId: Long = 0
        var albumName: String = ""
        var artistId: Long = 0
        var artistName: String = ""

        fun albumId(id: Long): Builder {
            albumId = id
            return this
        }

        fun albumName(name: String): Builder {
            albumName = name
            return this
        }

        fun artistName(name: String): Builder {
            artistName = name
            return this
        }

        fun artistId(id: Long): Builder {
            artistId = id
            return this
        }

        fun build(): Album {
            return Album(albumId, albumName, artistId, artistName)
        }
    }

    override fun equals(other: Any?): Boolean {
        return albumId == (other as Album).albumId
    }

    override fun hashCode(): Int {
        return albumId.hashCode()
    }
}