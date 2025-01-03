package com.example.kotlinapplicationdelivery.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
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
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductsAdapter(val context: Activity, private val products: ArrayList<Product>): RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    var sharedPref = SharedPref(context)
    val gson = Gson()
    private var selectedProducts = ArrayList<Product>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_product, parent, false)
        return ProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {

        val product = products[position]
        sharedPref = SharedPref(context)
        holder.textViewName.text = product.name
        holder.textViewPrice.text = "${product.price} VND"
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)
        holder.btnAdd.setOnClickListener {
            it.isClickable = true
            addToBag(product)
        }
        getProductsFromSharedPref()

        holder.itemView.setOnClickListener { goToDetail(product) }
    }

    private fun goToDetail(product: Product) {
        val i = Intent(context, ClientProductsDetailActivity::class.java)
        i.putExtra("product", product.toJson())
        context.startActivity(i)
    }

    class ProductsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val btnAdd: ImageView = view.findViewById(R.id.button_add)
        val textViewName: TextView = view.findViewById(R.id.textview_name)
        val textViewPrice: TextView = view.findViewById(R.id.textview_price)
        val imageViewProduct: ImageView = view.findViewById(R.id.imageview_product)

    }
    private fun addToBag(product: Product) {
        val index = getIndexOf(product.id!!) // PRODUCT INDEX IF IT EXISTS IN SHARED PREF

        if (index == -1) { // THIS PRODUCT DOES NOT YET EXIST IN SHARED PREF
            if (product.quantity == null) {
                product.quantity = 1
            }
            selectedProducts.add(product)
            Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(context, "Món ăn đã có trong thực đơn", Toast.LENGTH_LONG).show()
        }

        sharedPref.save("order", selectedProducts)
    }
    @SuppressLint("SetTextI18n")
    private fun getProductsFromSharedPref() {

        if (!sharedPref.getData("order").isNullOrBlank()) { // THERE IS AN ORDER IN SHARED PREF
            val type = object: TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref.getData("order"), type)
//            val index = getIndexOf(product?.id!!)
//            if (index != -1) {
//                product?.quantity = selectedProducts[index].quantity
//            }
        }
    }
    private fun getIndexOf(idProduct: String): Int {
        for ((pos, p) in selectedProducts.withIndex()) {
            if (p.id == idProduct) {
                return pos
            }
        }
        return -1
    }
}