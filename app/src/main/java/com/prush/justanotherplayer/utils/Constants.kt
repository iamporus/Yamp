package com.prush.justanotherplayer.utils

const val SELECTED_TRACK_POSITION = "selectedTrackPosition"
const val SELECTED_ALBUM_ID = "selectedAlbumId"
const val SELECTED_ARTIST_ID = "selectedArtistId"
const val SELECTED_GENRE_ID = "selectedGenreId"
const val SEARCH_QUERY = "searchQuery"
const val SHUFFLE_TRACKS = "shuffleTracks"
const val SELECTED_TRACK = "selectedTrack"
const val TRACKS_LIST = "tracksList"
const val PLAYBACK_CHANNEL_ID = "Yamp!"
const val PLAY_CONTEXT_TYPE = "playContextType"

enum class PLAY_CONTEXT {
    LIBRARY_TRACKS,
    ALBUM_TRACKS,
    ARTIST_TRACKS,
    SEARCH_TRACKS,
    GENRE_TRACKS,
    QUEUE_TRACKS
}
