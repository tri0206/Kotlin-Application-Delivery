package com.example.kotlinapplicationdelivery.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.utils.SharedPref

class OrderProductsAdapter(val context: Activity, val products: ArrayList<Product>): RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {

    val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_order_products, parent, false)
        return OrderProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {

        val product = products[position]

        holder.textViewName.text = product.name

        if (product.quantity != null) {
            holder.textViewQuantity.text = "${product.quantity!!}"
        }
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)
    }

    class OrderProductsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val imageViewProduct: ImageView = view.findViewById(R.id.imageview_product)
        val textViewName: TextView = view.findViewById(R.id.textview_name)
        val textViewQuantity: TextView = view.findViewById(R.id.textview_quantity)
    }
}