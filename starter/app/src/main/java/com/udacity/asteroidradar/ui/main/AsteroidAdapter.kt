package com.udacity.asteroidradar.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.databinding.ItemAsteroidBinding
import com.udacity.asteroidradar.domain.Asteroid

// TODO: For an extra challenge, update this to use the paging library.
//  For now, we will use the standard ListAdapter from the the RecyclerView lesson.
class AsteroidAdapter(private val listener: AsteroidListEvents) :
    ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(ASTEROID_COMPARATOR) {

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val binding =
            ItemAsteroidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AsteroidViewHolder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let { holder.bind(it, listener) }
    }

    override fun onCurrentListChanged(previousList: List<Asteroid>, currentList: List<Asteroid>) {
        super.onCurrentListChanged(previousList, currentList)
        listener.onListChanged()
    }

    /**
     * ViewHolder for Asteroid items. All work is done by data binding.
     */
    class AsteroidViewHolder(private val binding: ItemAsteroidBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asteroid: Asteroid, listener: AsteroidListEvents) {
            binding.item = asteroid
            binding.listener = listener
        }
    }

    interface AsteroidListEvents {
        fun onItemClick(asteroid: Asteroid)
        fun onListChanged()
    }

    companion object {
        private val ASTEROID_COMPARATOR = object : DiffUtil.ItemCallback<Asteroid>() {
            override fun areItemsTheSame(old: Asteroid, new: Asteroid) = old.id == new.id
            override fun areContentsTheSame(old: Asteroid, new: Asteroid) = old == new
        }
    }
}
