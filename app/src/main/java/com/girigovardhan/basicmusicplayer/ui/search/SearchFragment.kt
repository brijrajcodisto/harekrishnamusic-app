package com.girigovardhan.basicmusicplayer.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.girigovardhan.basicmusicplayer.databinding.FragmentSearchBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchLogic()
        setupViewPager()
    }

    private fun setupSearchLogic() {
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString()
            if (query.isNotEmpty()) {
                showSearchResults(true)
                performSearch(query)
            } else {
                showSearchResults(false)
            }
        }

        // Back button logic
        binding.toolbar.setNavigationOnClickListener {
            // If searching, clear search, else go back
            if (binding.searchResultsContainer.visibility == View.VISIBLE) {
                binding.searchEditText.text?.clear()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showSearchResults(isSearching: Boolean) {
        if (isSearching) {
            binding.searchResultsContainer.visibility = View.VISIBLE
            binding.browseContainer.visibility = View.GONE
        } else {
            binding.searchResultsContainer.visibility = View.GONE
            binding.browseContainer.visibility = View.VISIBLE
        }
    }

    private fun setupViewPager() {
        // You will need a FragmentStateAdapter for this
        // val adapter = SearchResultAdapter(this)
        // binding.searchResultsViewPager.adapter = adapter

        // Connecting TabLayout with ViewPager2
        TabLayoutMediator(binding.searchResultsTabLayout, binding.searchResultsViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "All"
                1 -> "Songs"
                2 -> "Artists"
                3 -> "Albums"
                else -> "Playlists"
            }
        }.attach()
    }

    private fun performSearch(query: String) {
        // Logic to update your ViewPager fragments or ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}