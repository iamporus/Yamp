package com.prush.justanotherplayer.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.prush.justanotherplayer.ui.albumslibrary.AlbumsLibraryFragment
import com.prush.justanotherplayer.ui.artistslibrary.ArtistsLibraryFragment
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.ui.genreslibrary.GenresLibraryFragment
import com.prush.justanotherplayer.ui.trackslibrary.TracksLibraryFragment

class PagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> {
                return TracksLibraryFragment.newInstance()
            }
            1 -> {
                return AlbumsLibraryFragment.newInstance()
            }
            2 -> {
                return ArtistsLibraryFragment.newInstance()
            }
            3 -> {
                return GenresLibraryFragment.newInstance()
            }
        }
        return BaseRecyclerFragment()
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "TRACKS"
            }
            1 -> {
                return "ALBUMS"
            }
            2 -> {
                return "ARTISTS"
            }
            3 -> {
                return "GENRES"
            }
        }
        return super.getPageTitle(position)
    }
}