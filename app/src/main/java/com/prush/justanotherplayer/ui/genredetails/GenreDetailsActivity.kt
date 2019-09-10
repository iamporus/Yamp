package com.prush.justanotherplayer.ui.genredetails

import android.os.Bundle
import android.view.MenuItem
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseNowPlayingActivity
import com.prush.justanotherplayer.ui.genredetails.GenreDetailsFragment.Companion.GENRE_ID

class GenreDetailsActivity : BaseNowPlayingActivity() {

    override fun getLayoutResourceId(): Int {
        return R.layout.now_playing_footer_with_container_layout
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        super.onViewCreated(savedInstanceState)

        val genreId = intent.getLongExtra(GENRE_ID, 0)

        val genreDetailsFragment = supportFragmentManager.findFragmentById(R.id.container)
                as GenreDetailsFragment? ?: GenreDetailsFragment.newInstance(genreId)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, genreDetailsFragment)
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}