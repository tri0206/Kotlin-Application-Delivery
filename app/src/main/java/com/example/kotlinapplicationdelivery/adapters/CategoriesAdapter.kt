package com.example.kotlinapplicationdelivery.adapters

import android.R.attr.data
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
import com.example.kotlinapplicationdelivery.activities.client.restaurant.ClientRestaurantListActivity
import com.example.kotlinapplicationdelivery.models.Category
import com.example.kotlinapplicationdelivery.utils.SharedPref
import org.checkerframework.checker.nullness.qual.NonNull


class CategoriesAdapter(val context: Activity, private val categories: ArrayList<Category>): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    val sharedPref = SharedPref(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_cate_test, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val color = when (position % 6) {
            0 -> Color.rgb(254, 244, 229)
            1 -> Color.rgb(245, 229, 254)
            2 -> Color.rgb(229, 241, 254)
            3 -> Color.rgb(235, 254, 229)
            4 -> Color.rgb(249, 228, 228)
            else -> Color.WHITE
        }
        holder.cardView.setCardBackgroundColor(color)
        val category = categories[position]

        holder.textViewCategory.text = category.name
        Glide.with(context).load(category.image).into(holder.imageViewCategory)


        holder.itemView.setOnClickListener { goToRestaurants(category) }
    }
    private fun goToRestaurants(category: Category) {
        val i = Intent(context, ClientRestaurantListActivity::class.java)
        i.putExtra("idCategory", category.id)
        i.putExtra("nameCategory", category.name)
        context.startActivity(i)
    }

    class CategoriesViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cardView: CardView = itemView.findViewById(R.id.rootCate)
        val textViewCategory: TextView = view.findViewById(R.id.textview_category)
        val imageViewCategory: ImageView = view.findViewById(R.id.imageview_category)

    }

}