package com.example.kotlinapplicationdelivery.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.products.detail.ClientProductsDetailActivity
import com.example.kotlinapplicationdelivery.activities.client.restaurant.ClientRestaurantDetailActivity
import com.example.kotlinapplicationdelivery.models.DiscountedProduct
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductsDiscountedAdapter(val context: Activity, private val products: ArrayList<DiscountedProduct>): RecyclerView.Adapter<ProductsDiscountedAdapter.ProductsDiscountedViewHolder>() {

    var sharedPref = SharedPref(context)
    val gson = Gson()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsDiscountedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_product_today, parent, false)
        return ProductsDiscountedViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductsDiscountedViewHolder, position: Int) {

        val product = products[position]
        sharedPref = SharedPref(context)
        holder.textViewName.text = product.name
        holder.textViewOldPrice.text = "${product.originalPrice}đ"
        holder.textViewOldPrice.paintFlags = holder.textViewOldPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        holder.textViewNewPrice.text = "${product.discountedPrice}đ"
        Glide.with(context).load(product.imageUrl).into(holder.imageViewProduct)

        holder.itemView.setOnClickListener { goToRestaurantDetail(product) }
    }

    private fun goToRestaurantDetail(product: DiscountedProduct) {
        val i = Intent(context, ClientRestaurantDetailActivity::class.java)
        i.putExtra("restaurant_id", product.idRestaurant)
        context.startActivity(i)
    }

    class ProductsDiscountedViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.txtTitle)
        val textViewOldPrice: TextView = view.findViewById(R.id.txtOriginalPrice)
        val textViewNewPrice: TextView = view.findViewById(R.id.txtDiscountedPrice)
        val imageViewProduct: ImageView = view.findViewById(R.id.imgFood)
    }
}