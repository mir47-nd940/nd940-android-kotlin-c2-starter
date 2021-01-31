package com.udacity.asteroidradar.ui.main

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.db.AsteroidDatabase
import com.udacity.asteroidradar.domain.*
import com.udacity.asteroidradar.util.setImageUrl

/**
 * Main screen for displaying the image of the day and a list of asteroids.
 */
class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        val database = AsteroidDatabase.getInstance(requireContext())
        val repository = AsteroidRepository(database.asteroidDao, database.imageDao)
        val viewModelFactory = MainViewModelFactory(repository)
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.progressImage.isVisible = true
        viewModel.imageOfTheDay.observe(viewLifecycleOwner, { image ->
            image?.let {
                binding.imageOfTheDay.contentDescription =
                    String.format(getString(R.string.content_description_image_of_the_day), it.title)
                setImageUrl(binding.imageOfTheDay, it.url) {
                    binding.progressImage.isVisible = false
                }
            }
        })

        val asteroidAdapter = AsteroidAdapter(object : AsteroidAdapter.AsteroidListEvents {
            override fun onItemClick(asteroid: Asteroid) {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
            }

            override fun onListChanged() {
                binding.list.layoutManager?.scrollToPosition(0)
            }
        })

        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = asteroidAdapter
        }

        binding.progressList.isVisible = true
        viewModel.asteroids.observe(viewLifecycleOwner, { asteroids ->
            binding.progressList.isVisible = false
            asteroids?.let { asteroidAdapter.submitList(it) }
        })

        /**
         * When app started for the first time, we should update asteroids from network, then
         * daily updates will be handled by [com.udacity.asteroidradar.work.RefreshDataWorker]
         */
        if (!isInitialDataLoaded()) {
            viewModel.updateAsteroids()
            setInitialDataLoaded()
        }

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.next_week_asteroids -> viewModel.loadAsteroids(Weekly)
            R.id.today_asteroids -> viewModel.loadAsteroids(Daily)
            R.id.saved_asteroids -> viewModel.loadAsteroids(All)
        }
        return true
    }

    private fun isInitialDataLoaded(): Boolean {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getBoolean(PREFS_KEY_IS_INITIAL_DATA_LOADED, false)
    }

    private fun setInitialDataLoaded() {
        val editor = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        editor.putBoolean(PREFS_KEY_IS_INITIAL_DATA_LOADED, true)
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "ASTEROID_RADAR_PREFS"
        private const val PREFS_KEY_IS_INITIAL_DATA_LOADED = "prefs_key_is_initial_data_loaded"
    }
}
