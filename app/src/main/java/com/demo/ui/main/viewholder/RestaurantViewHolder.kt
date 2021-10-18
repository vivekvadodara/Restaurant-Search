package com.demo.ui.main.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.demo.model.Restaurant
import com.demo.databinding.ItemRestaurantBinding

class RestaurantViewHolder(private val binding: ItemRestaurantBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(restaurant: Restaurant, onItemClicked: (Restaurant, ImageView) -> Unit) {

        binding.restaurantMatch.text = restaurant.match
        binding.restaurantName.text = "Name : ${restaurant.name}"
        binding.restaurantCuisine.text = "Cuisine Type : ${restaurant.cuisineType}"
        binding.restaurantAddress.text = "Address Type : ${restaurant.address}"
        binding.restaurantId.text = "Id : ${restaurant.id}"


        binding.root.setOnClickListener {
            onItemClicked(restaurant, binding.imageView)
        }

        if (restaurant.match.contains("NAME")) {
            binding.root.setBackgroundColor(Color.GRAY)
            binding.restaurantName.textSize = 25.0f

        } else if (restaurant.match.contains("CUISINE")) {
            binding.root.setBackgroundColor(Color.BLACK)
            binding.restaurantCuisine.textSize = 25.0f
        } else if (restaurant.match.contains("MENU")) {
            binding.menuHighlight.text = "Menu :... ${restaurant.matchMenu}..."
            binding.menuHighlight.textSize = 25.0f
            binding.root.setBackgroundColor(Color.parseColor("#006666"))
        } else if (restaurant.match.contains("ADDRESS")) {
            binding.root.setBackgroundColor(Color.parseColor("#6699ff"))
            binding.restaurantAddress.textSize = 25.0f
        } else {
            binding.root.setBackgroundColor(Color.parseColor("#0DFFFFFF"))
        }
    }
}
