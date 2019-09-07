package com.prush.justanotherplayer.albumslibrary

import android.util.Log
import com.prush.justanotherplayer.main.IMainActivityView
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.IAlbumRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = AlbumPresenter::class.java.name


class AlbumPresenter(
    private val albumRepository: IAlbumRepository,
    private val albumsView: AlbumsContract.View,
    private val mainActivityView: IMainActivityView
) : AlbumsContract.Presenter, CoroutineScope {

    init {
        albumsView.albumsPresenter = this
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    override fun loadLibraryAlbums() {

        mainActivityView.showProgress()
        launch {

            try {
                delay(700)

                val albumsList = albumRepository.getAllAlbums(albumsView.getApplicationContext())
                withContext(Dispatchers.Main) {

                    if (albumsList.isEmpty()) {
                        albumsView.displayEmptyLibrary()
                    } else {
                        albumsView.displayAllAlbums(albumsList)
                    }
                    mainActivityView.hideProgress()
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")

                mainActivityView.hideProgress()
                mainActivityView.displayError()
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