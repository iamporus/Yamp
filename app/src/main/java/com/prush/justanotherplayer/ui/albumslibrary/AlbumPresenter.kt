package com.prush.justanotherplayer.ui.albumslibrary

import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.IAlbumRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = AlbumPresenter::class.java.name


class AlbumPresenter(
    private val albumRepository: IAlbumRepository,
    private val view: AlbumsContract.View
) : AlbumsContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    override fun loadLibraryAlbums() {

        view.showProgress()
        launch {

            try {
                val albumsList = albumRepository.getAllAlbums(view.getApplicationContext())
                withContext(Dispatchers.Main) {

                    if (albumsList.isEmpty()) {
                        view.displayEmptyLibrary()
                    } else {
                        view.displayAllAlbums(albumsList)
                    }
                    view.hideProgress()
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")

                withContext(Dispatchers.Main){
                    view.hideProgress()
                    view.displayError()
                }
            }

        }

    }

    override fun loadAlbumDetails(albumId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startAlbumPlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCleanup() {
        job.cancel()
    }
}