package com.prush.justanotherplayer.ui.artistdetails

import android.util.Log
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Artist
import com.prush.justanotherplayer.repositories.IArtistRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = ArtistDetailsPresenter::class.java.name

class ArtistDetailsPresenter(
    private val artistRepository: IArtistRepository,
    private val view: ArtistDetailsContract.View
) : ArtistDetailsContract.Presenter, CoroutineScope {

    private var artist: Artist = Artist()
    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    override fun fetchArtistDetails(artistId: Long): Artist {

        Log.d(TAG, "fetching details for artist $artistId")

        view.showProgress()

        launch {

            try {

                artist = artistRepository.getArtistById(view.getApplicationContext(), artistId)

                withContext(Dispatchers.Main) {
                    view.displayArtistDetails(artist)
                    view.hideProgress()
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")

                withContext(Dispatchers.Main) {
                    view.hideProgress()
                    view.displayError()
                }
            }
        }
        return artist
    }

    override fun loadAlbumDetails(album: Album) {
        view.displayAlbumDetails(album)
    }

    override fun prepareTrackPlayback(selectedTrackPosition: Int) {
        view.startTrackPlayback(selectedTrackPosition, artist.tracksList)
        //TODO: pass in artistId and selectedTrackPosition
    }

    override fun prepareTrackContextMenu(position: Int) {
        view.showContextMenu(artist.tracksList[position])
    }

    override fun onCleanup() {
        job.cancel()
    }
}