package com.girigovardhan.basicmusicplayer.ui.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LibraryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 6 // Total number of tabs

    override fun createFragment(position: Int): Fragment {
        // Return the specific fragment for each tab
        // Replace with your actual Fragment classes
        return when (position) {
            0 -> PlaylistsFragment()
            1 -> AlbumsFragment()
            2 -> ArtistsFragment()
            3 -> SongsFragment()
            4 -> DownloadsFragment()
            else -> FavoritesFragment()
        }
    }
}