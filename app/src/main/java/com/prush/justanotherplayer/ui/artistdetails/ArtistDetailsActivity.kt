package com.prush.justanotherplayer.ui.artistdetails

import android.os.Bundle
import android.view.MenuItem
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseNowPlayingActivity
import com.prush.justanotherplayer.ui.artistdetails.ArtistDetailsFragment.Companion.ARTIST_ID

class ArtistDetailsActivity : BaseNowPlayingActivity() {

    override fun getLayoutResourceId(): Int {
        return R.layout.now_playing_footer_with_container_layout
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        super.onViewCreated(savedInstanceState)

        val artistId = intent.getLongExtra(ARTIST_ID, 0)

        val artistDetailsFragment = supportFragmentManager.findFragmentById(R.id.container)
                as ArtistDetailsFragment? ?: ArtistDetailsFragment.newInstance(artistId)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, artistDetailsFragment)
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}