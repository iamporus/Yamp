package com.prush.justanotherplayer.ui.artistslibrary

import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.IArtistRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = ArtistPresenter::class.java.name

class ArtistPresenter(
    private val artistRepository: IArtistRepository,
    private val view: ArtistsContract.View
) : ArtistsContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    override fun loadLibraryArtists() {

        view.showProgress()
        launch {

            try {
                val artistsList = artistRepository.getAllArtists(view.getApplicationContext())
                withContext(Dispatchers.Main) {

                    if (artistsList.isEmpty()) {
                        view.displayEmptyLibrary()
                    } else {
                        view.displayAllArtists(artistsList)
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

    override fun loadArtistDetails(artistId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startArtistPlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCleanup() {
        job.cancel()
    }
}