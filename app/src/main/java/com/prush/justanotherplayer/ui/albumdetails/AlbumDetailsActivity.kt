package com.prush.justanotherplayer.ui.albumdetails

import android.os.Bundle
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseNowPlayingActivity
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsFragment.Companion.ALBUM_ID

class AlbumDetailsActivity : BaseNowPlayingActivity() {

    override fun getLayoutResourceId(): Int {
        return R.layout.now_playing_footer_with_container_layout
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        super.onViewCreated(savedInstanceState)

        val albumId = intent.getLongExtra(ALBUM_ID, 0)

        val albumDetailsFragment = supportFragmentManager.findFragmentById(R.id.container)
                as AlbumDetailsFragment? ?: AlbumDetailsFragment.newInstance(albumId)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, albumDetailsFragment)
            commit()
        }
    }

}