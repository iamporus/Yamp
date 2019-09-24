package com.prush.justanotherplayer.ui.search

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseServiceBoundedActivity
import kotlinx.android.synthetic.main.activity_main.*

private const val SEARCH_FRAGMENT_TAG = "SearchFragment"

class SearchActivity : BaseServiceBoundedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.base_container_layout)

        val searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_FRAGMENT_TAG)
                as SearchFragment? ?: SearchFragment.newInstance("")

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, searchFragment, SEARCH_FRAGMENT_TAG)
            commit()
        }

        toolbar.title = ""
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handleIntent(intent)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d("Search", "onNewIntent()")
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {

        if (intent != null) {
            if (intent.action == Intent.ACTION_SEARCH) {
                val query = intent.getStringExtra(SearchManager.QUERY)

                val searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_FRAGMENT_TAG)
                        as SearchFragment? ?: SearchFragment.newInstance(query)

                searchFragment.searchTracks(query)
            }
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_action_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (menu != null) {

            var searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.apply {
                setSearchableInfo(
                    searchManager.getSearchableInfo(
                        ComponentName(
                            applicationContext,
                            SearchActivity::class.java
                        )
                    )
                )

                isIconified = false
            }
        }
        return true
    }


}