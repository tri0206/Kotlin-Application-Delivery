package com.example.kotlinapplicationdelivery.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.products.detail.ClientProductsDetailActivity
import com.example.kotlinapplicationdelivery.activities.client.restaurant.ClientRestaurantDetailActivity
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.adapters.ShoppingBagAdapter.ShoppingBagViewHolder
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
        if(product.price != product.discountPrice) {
            holder.textViewDiscountedPrice.apply {
                text = "${product.price}đ"
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                visibility = View.VISIBLE
            }
            holder.textViewPrice.text = "${product.discountPrice}đ"
        }
        else {
            holder.textViewPrice.text = "${product.price}đ"
        }
        holder.textViewDescription.text = product.description
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)
        holder.btnAdd.setOnClickListener {
            it.isClickable = true
            holder.btnAdd.visibility = View.GONE
            holder.quantityLayout.visibility = View.VISIBLE
            addToBag(product)
        }
        //getProductsFromSharedPref()
        holder.btnPlus.setOnClickListener {addItem(product, holder)}
        holder.btnMinus.setOnClickListener { removeItem(product, holder) }
        holder.itemView.setOnClickListener { goToDetail(product) }
    }

    private fun goToDetail(product: Product) {
        val i = Intent(context, ClientProductsDetailActivity::class.java)
        i.putExtra("product", product.toJson())
        i.putExtra("id_restaurant", product.idRestaurant)
        context.startActivity(i)
    }

    class ProductsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val btnAdd: TextView = view.findViewById(R.id.button_add)
        val textViewName: TextView = view.findViewById(R.id.textview_name)
        val textViewPrice: TextView = view.findViewById(R.id.textview_price)
        val textViewDiscountedPrice: TextView = view.findViewById(R.id.textview_discounted_price)
        val imageViewProduct: ImageView = view.findViewById(R.id.imageview_product)
        val textViewDescription: TextView = view.findViewById(R.id.textview_description)
        val quantityLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.quantityLayout)
        val btnPlus: TextView = view.findViewById(R.id.button_plus)
        val btnMinus: TextView = view.findViewById(R.id.button_minus)
        val txtQuantity: TextView = view.findViewById(R.id.textview_quantity)
    }
    private fun addToBag(product: Product) {
        val index = getIndexOf(product.id!!)
        if (index == -1) {
            if (product.quantity == null) {
                product.quantity = 1
            }
            selectedProducts.add(product)
        }
        sharedPref.save("order", selectedProducts)
        (context as ClientRestaurantDetailActivity).setTotal(getTotal())
        context.updateCartOverlayVisibility()
    }
    @SuppressLint("SetTextI18n")
    private fun addItem(product: Product, holder: ProductsViewHolder) {
        val index = getIndexOf(product.id!!)
        product.quantity = product.quantity!! + 1
        selectedProducts[index].quantity = product.quantity

        holder.txtQuantity.text = "${product.quantity}"
        //holder.textViewPrice.text = "${product.quantity!! * product.price} VND"

        sharedPref.save("order", selectedProducts)
        (context as ClientRestaurantDetailActivity).setTotal(getTotal())
        context.updateCartOverlayVisibility()
    }

    @SuppressLint("SetTextI18n")
    private fun removeItem(product: Product, holder: ProductsViewHolder) {
        val index = getIndexOf(product.id!!)
        if (product.quantity!! > 1) {
            product.quantity = product.quantity!! - 1
            selectedProducts[index].quantity = product.quantity
            holder.txtQuantity.text = "${product.quantity}"
            //holder.textViewPrice.text = "${product.quantity!! * product.price}VND"
            sharedPref.save("order", selectedProducts)
            (context as ClientRestaurantDetailActivity).setTotal(getTotal())
            context.updateCartOverlayVisibility()
        }
        else {
            selectedProducts.removeAt(index)
            holder.quantityLayout.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            sharedPref.save("order", selectedProducts)
            if(selectedProducts.size == 0) {
                sharedPref.remove("order")
            }
            (context as ClientRestaurantDetailActivity).setTotal(getTotal())
            context.updateCartOverlayVisibility()
        }
    }
//    @SuppressLint("SetTextI18n")
//    private fun getProductsFromSharedPref() {
//
//        if (!sharedPref.getData("order").isNullOrBlank()) {
//            val type = object: TypeToken<ArrayList<Product>>() {}.type
//            selectedProducts = gson.fromJson(sharedPref.getData("order"), type)
////            val index = getIndexOf(product?.id!!)
////            if (index != -1) {
////                product?.quantity = selectedProducts[index].quantity
////            }
//        }
//    }
    private fun getIndexOf(idProduct: String): Int {
        for ((pos, p) in selectedProducts.withIndex()) {
            if (p.id == idProduct) {
                return pos
            }
        }
        return -1
    }
    private fun getTotal(): Int {
        var total = 0
        for (p in selectedProducts) {
            if (p.quantity != null) {
                total += (p.quantity!! * p.discountPrice!!)
            }
        }
        return total
    }
}