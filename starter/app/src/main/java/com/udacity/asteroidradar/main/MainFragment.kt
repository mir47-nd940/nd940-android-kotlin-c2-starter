package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repo.AllAsteroids
import com.udacity.asteroidradar.repo.DailyAsteroids
import com.udacity.asteroidradar.repo.WeeklyAsteroids
import com.udacity.asteroidradar.util.setImageUrl

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    /**
     * RecyclerView Adapter for converting a list of Asteroid to cards.
     */
    private var viewModelAdapter: AsteroidAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.imageOfTheDay.observe(viewLifecycleOwner, Observer { image ->
            image?.let { setImageUrl(binding.activityMainImageOfTheDay, it.url) }
        })

        viewModelAdapter = AsteroidAdapter(AsteroidClick {
            // When an asteroid is clicked this block or lambda will be called by AsteroidAdapter
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })

        binding.asteroidRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroids.observe(viewLifecycleOwner, Observer { asteroids ->
            asteroids?.let {
                println("mmmmm asteroids size = ${asteroids.size}")
                viewModelAdapter?.asteroids = asteroids
            } ?: run {
                println("mmmmm asteroids null")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.next_week_asteroids -> viewModel.getFilteredAsteroids(WeeklyAsteroids)
            R.id.today_asteroids -> viewModel.getFilteredAsteroids(DailyAsteroids)
            R.id.saved_asteroids -> viewModel.getFilteredAsteroids(AllAsteroids)
        }
        return true
    }
}
