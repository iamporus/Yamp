package com.prush.justanotherplayer.ui.genredetails

import android.util.Log
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Genre
import com.prush.justanotherplayer.repositories.IGenreRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = GenreDetailsPresenter::class.java.name

class GenreDetailsPresenter(
    private val genreRepository: IGenreRepository,
    private val view: GenreDetailsContract.View
) : GenreDetailsContract.Presenter, CoroutineScope {

    private var genre: Genre = Genre()
    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    override fun fetchGenreDetails(genreId: Long): Genre {

        Log.d(TAG, "fetching details for genre $genreId")

        view.showProgress()

        launch {

            try {

                genre = genreRepository.getGenreById(view.getApplicationContext(), genreId)

                withContext(Dispatchers.Main) {
                    view.displayGenreDetails(genre)
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
        return genre
    }

    override fun loadAlbumDetails(album: Album) {
        view.displayAlbumDetails(album)
    }

    override fun prepareTrackPlayback(selectedTrackPosition: Int) {
        view.startTrackPlayback(selectedTrackPosition, genre.tracksList)
        //TODO: pass in genreID and selectedTrackPosition
    }

    override fun prepareTrackContextMenu(position: Int) {
        view.showContextMenu(genre.tracksList[position])
    }

    override fun onCleanup() {
        job.cancel()
    }
}