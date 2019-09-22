package com.prush.justanotherplayer.ui.search

import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.ISearchRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = SearchPresenter::class.java.name

class SearchPresenter(
    private val searchRepository: ISearchRepository,
    private val view: SearchContract.View
) : SearchContract.Presenter, CoroutineScope {


    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    private var tracksList = mutableListOf<Track>()


    override fun loadTracksStartingWith(query: String) {

        Log.d(TAG, "loadTracksStartingWith()")
        view.showProgress()

        launch {
            try {

                tracksList.clear()

                val searchResult = searchRepository.searchAll(view.getApplicationContext(), query)
                tracksList.addAll(searchResult.tracks)

                withContext(Dispatchers.Main) {
                    if (tracksList.isEmpty())
                        view.displayEmptyResult()
                    else {
                        view.displayFoundTracks(tracksList)
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

    override fun prepareTrackPlayback(selectedTrackPosition: Int) {

        Log.d(TAG, "Track selected for playback $selectedTrackPosition")
        view.startTrackPlayback(selectedTrackPosition, tracksList)
    }

    override fun onCleanup() {
        job.cancel()
        tracksList.clear()
        Log.d(TAG, "onCleanup()")
    }
}