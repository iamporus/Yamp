package com.prush.justanotherplayer.ui.albumdetails

import android.util.Log
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.repositories.IAlbumRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = AlbumDetailsPresenter::class.java.name

class AlbumDetailsPresenter(
    private val albumsRepository: IAlbumRepository,
    private val view: AlbumDetailsContract.View
) : AlbumDetailsContract.Presenter, CoroutineScope {

    private var album: Album = Album()
    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    override fun fetchAlbumDetails(albumId: Long): Album {

        Log.d(TAG, "fetching details for album $albumId")

        view.showProgress()

        launch {

            try {

                album = albumsRepository.getAlbumById(view.getApplicationContext(), albumId)

                withContext(Dispatchers.Main) {
                    view.displayAlbumDetails(album)
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
        return album
    }

    override fun prepareTrackPlayback(selectedTrackPosition: Int) {
        view.startTrackPlayback(selectedTrackPosition, album.tracksList)
        //TODO: pass in albumID and selectedTrackPosition
    }

    override fun prepareTrackContextMenu(position: Int) {
        view.showContextMenu(album.tracksList[position])
    }

    override fun onCleanup() {
        job.cancel()
    }
}