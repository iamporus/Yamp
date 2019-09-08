package com.prush.justanotherplayer.genreslibrary

import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.IGenreRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = GenrePresenter::class.java.name

class GenrePresenter(
    private val genreRepository: IGenreRepository,
    private val view: GenresContract.View
) : GenresContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    override fun loadLibraryGenres() {

        view.showProgress()
        launch {

            try {
                val genresList = genreRepository.getAllGenres(view.getApplicationContext())
                withContext(Dispatchers.Main) {

                    if (genresList.isEmpty()) {
                        view.displayEmptyLibrary()
                    } else {
                        view.displayAllGenres(genresList)
                    }
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

    }

    override fun loadGenreDetails(genreId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startGenrePlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCleanup() {
        job.cancel()
    }
}