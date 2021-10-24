package com.demo.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.demo.model.Restaurant
import com.demo.ui.main.viewholder.RestaurantViewHolder
import com.demo.databinding.ItemRestaurantBinding

class RestaurantListAdapter(
    private val onItemClicked: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RestaurantViewHolder(
        ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) =
        holder.bind(getItem(position), onItemClicked)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Restaurant>() {
            override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean =
                oldItem == newItem
        }
    }
}
