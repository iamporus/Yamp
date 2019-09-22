package com.prush.justanotherplayer.ui.search

import android.os.Bundle
import android.view.MenuItem
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseNowPlayingActivity
import com.prush.justanotherplayer.ui.search.SearchFragment.Companion.SEARCH_QUERY

class SearchActivity : BaseNowPlayingActivity() {

    override fun getLayoutResourceId(): Int {
        return R.layout.now_playing_footer_with_container_layout
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        super.onViewCreated(savedInstanceState)

        val query = intent.getStringExtra(SEARCH_QUERY)

        val searchFragment = supportFragmentManager.findFragmentById(R.id.container)
                as SearchFragment? ?: SearchFragment.newInstance(query)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, searchFragment)
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