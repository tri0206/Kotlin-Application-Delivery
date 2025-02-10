package com.example.kotlinapplicationdelivery.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.products.list.ClientProductsListActivity
import com.example.kotlinapplicationdelivery.activities.client.restaurant.ClientRestaurantDetailActivity
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.utils.SharedPref

class RestaurantsAdapter (val context: Activity, private val restaurants: ArrayList<Restaurant>): RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder>() {

    val sharedPref = SharedPref(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_restaurant, parent, false)
        return RestaurantsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    override fun onBindViewHolder(holder: RestaurantsViewHolder, position: Int) {

        val restaurant = restaurants[position]

        holder.textViewRestaurantName.text = restaurant.name
        holder.textViewRestaurantAddress.text = buildString {
            append(restaurant.res_address)
            append(", ")
            append(restaurant.res_neighborhood)
        }
        Glide.with(context).load(restaurant.image).into(holder.imageViewRestaurant)

        holder.itemView.setOnClickListener { goToProducts(restaurant) }
    }

    private fun goToProducts(restaurant: Restaurant) {
        val i = Intent(context, ClientRestaurantDetailActivity::class.java)
        i.putExtra("restaurant_id", restaurant.id)
        i.putExtra("restaurant_name", restaurant.name)
        i.putExtra("restaurant_image", restaurant.image)
        i.putExtra("restaurant_description", restaurant.description)
        i.putExtra("restaurant_address", restaurant.res_address + ", " + restaurant.res_neighborhood)
        i.putExtra("restaurant_neighborhood", restaurant.res_neighborhood)
        context.startActivity(i)
    }

    class RestaurantsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val textViewRestaurantName: TextView = view.findViewById(R.id.restaurant_name)
        val imageViewRestaurant: ImageView = view.findViewById(R.id.restaurant_image)
        val textViewRestaurantAddress: TextView = view.findViewById(R.id.restaurant_address)
    }
}