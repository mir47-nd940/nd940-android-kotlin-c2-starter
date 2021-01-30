package com.udacity.asteroidradar.main

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repo.All
import com.udacity.asteroidradar.repo.Daily
import com.udacity.asteroidradar.repo.Weekly
import com.udacity.asteroidradar.util.setImageUrl

/**
 * Main screen for displaying the image of the day and a list of asteroids.
 */
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    /**
     * RecyclerView Adapter for converting a list of asteroids to views.
     */
    private var asteroidAdapter: AsteroidAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.imageOfTheDay.observe(viewLifecycleOwner, { image ->
            image?.let { setImageUrl(binding.activityMainImageOfTheDay, it.url) }
        })

        asteroidAdapter = AsteroidAdapter(AsteroidClick {
            // asteroid click handler
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })

        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = asteroidAdapter
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.asteroids.observe(viewLifecycleOwner, { asteroids ->
            asteroids?.let {
                println("mmmmm asteroids size = ${asteroids.size}")
                asteroidAdapter?.asteroids = asteroids
            } ?: run {
                println("mmmmm asteroids null")
            }
        })

        /**
         * When app started for the first time, we should update asteroids from network, then
         * daily updates will be handled by [com.udacity.asteroidradar.work.RefreshDataWorker]
         */
        if (isInitialDataLoaded()) {
            viewModel.loadAsteroids(Weekly)
        } else {
            viewModel.updateAsteroids()
            setInitialDataLoaded()
        }
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
