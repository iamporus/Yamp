package com.prush.justanotherplayer.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.prush.justanotherplayer.albumslibrary.AlbumsLibraryFragment
import com.prush.justanotherplayer.artistslibrary.ArtistsLibraryFragment
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.trackslibrary.TracksLibraryFragment

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
        }
        return BaseRecyclerFragment()
    }

    override fun getCount(): Int {
        return 3
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
        }
        return super.getPageTitle(position)
    }
}