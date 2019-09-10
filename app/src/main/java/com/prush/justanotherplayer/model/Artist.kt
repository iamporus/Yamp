package com.prush.justanotherplayer.model

import com.prush.justanotherplayer.R
import java.io.Serializable

open class Artist() : Serializable {

    constructor(artistId: Long, artistName: String) : this() {

        this.artistId = artistId
        this.artistName = artistName
    }

    var artistId: Long = 0
    var artistName: String = ""

    var albumsList: MutableList<Album> = mutableListOf()
    var tracksList: MutableList<Track> = mutableListOf()

    val defaultAlbumArtRes: Int = R.drawable.ic_artist

    override fun equals(other: Any?): Boolean {
        return artistId == (other as Artist).artistId
    }

    override fun hashCode(): Int {
        return artistId.hashCode()
    }
}