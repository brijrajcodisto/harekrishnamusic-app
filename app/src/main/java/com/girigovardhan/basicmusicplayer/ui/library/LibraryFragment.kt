package com.girigovardhan.basicmusicplayer.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.girigovardhan.basicmusicplayer.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupListeners()
    }

    private fun setupViewPager() {
        val adapter = LibraryPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Sync TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Playlists"
                1 -> "Albums"
                2 -> "Artists"
                3 -> "Songs"
                4 -> "Downloads"
                else -> "Favorites"
            }
        }.attach()
    }

    private fun setupListeners() {
        binding.fabCreatePlaylist.setOnClickListener {
            // TODO: Open dialog to create new playlist
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                // Handle library_menu actions like Sort or Settings
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}