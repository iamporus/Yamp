package com.prush.justanotherplayer.model

import com.prush.justanotherplayer.R
import java.io.Serializable

open class Album() : Serializable {

    constructor(albumId: Long, albumName: String, artistId: Long, artistName: String) : this() {
        this.albumId = albumId
        this.albumName = albumName
        this.artistId = artistId
        this.artistName = artistName
    }

    var albumId: Long = 0
    var albumName: String = ""
    var artistId: Long = 0
    var artistName: String = ""

    var defaultAlbumArtRes: Int = R.drawable.playback_track_icon

    var tracksList: MutableList<Track> = mutableListOf()
    var artistsList: MutableList<Artist> = mutableListOf()

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
            val album = Album(albumId, albumName, artistId, artistName)
            album.artistsList.add(Artist(artistId, artistName))
            return album
        }
    }

    fun getArtist(): Artist {
        val artist = Artist(artistId, artistName)
        artist.albumsList.add(this)
        return artist
    }

    override fun equals(other: Any?): Boolean {
        return albumId == (other as Album).albumId
    }

    override fun hashCode(): Int {
        return albumId.hashCode()
    }

}