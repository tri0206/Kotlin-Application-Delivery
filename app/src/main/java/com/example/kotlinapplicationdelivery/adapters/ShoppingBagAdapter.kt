package com.example.kotlinapplicationdelivery.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.products.detail.ClientProductsDetailActivity
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.utils.SharedPref

class ShoppingBagAdapter(val context: Activity, val products: ArrayList<Product>): RecyclerView.Adapter<ShoppingBagAdapter.ShoppingBagViewHolder>() {

    val sharedPref = SharedPref(context)


    init {
        (context as ClientShoppingBagActivity).setTotal(getTotal())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingBagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_shopping_bag, parent, false)
        return ShoppingBagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShoppingBagViewHolder, position: Int) {

        val product = products[position]

        holder.textViewName.text = product.name
        holder.textviewQuantity.text = "${product.quantity}"

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
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)

        holder.btnPlus.setOnClickListener { addItem(product, holder) }
        holder.btnMinus.setOnClickListener { removeItem(product, holder) }
        holder.imageViewDelete.setOnClickListener { deleteItem(position) }
//        holder.itemView.setOnClickListener { goToDetail(product) }
    }

    private fun getTotal(): Int {
        var total = 0
        for (p in products) {
            if (p.quantity != null) {
                total += (p.quantity!! * p.discountPrice!!)
            }
        }
        return total
    }

    private fun getIndexOf(idProduct: String): Int {
        for ((pos, p) in products.withIndex()) {
            if (p.id == idProduct) {
                return pos
            }
        }
        return -1
    }

    private fun deleteItem(position: Int) {
        products.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, products.size)
        if(products.size == 0) {
            sharedPref.remove("order")
        }
        else {
            sharedPref.save("order", products)
        }
        (context as ClientShoppingBagActivity).setTotal(getTotal())
    }

    @SuppressLint("SetTextI18n")
    private fun addItem(product: Product, holder: ShoppingBagViewHolder) {
        val index = getIndexOf(product.id!!)
        product.quantity = product.quantity!! + 1
        products[index].quantity = product.quantity

        holder.textviewQuantity.text = "${product.quantity}"
        if(product.price != product.discountPrice) {
            holder.textViewDiscountedPrice.text = "${product.quantity!! * product.price}đ"
            holder.textViewPrice.text = "${product.quantity!! * product.discountPrice!!}đ"
        }
        else {
            holder.textViewPrice.text = "${product.quantity!! * product.price}đ"
        }
        sharedPref.save("order", products)
        (context as ClientShoppingBagActivity).setTotal(getTotal())
    }

    @SuppressLint("SetTextI18n")
    private fun removeItem(product: Product, holder: ShoppingBagViewHolder) {
        if (product.quantity!! > 1) {
            val index = getIndexOf(product.id!!)
            product.quantity = product.quantity!! - 1
            products[index].quantity = product.quantity
            holder.textviewQuantity.text = "${product.quantity}"
            if(product.price != product.discountPrice) {
                holder.textViewDiscountedPrice.text = "${product.quantity!! * product.price}đ"
                holder.textViewPrice.text = "${product.quantity!! * product.discountPrice!!}đ"
            }
            else {
                holder.textViewPrice.text = "${product.quantity!! * product.price}đ"
            }
            sharedPref.save("order", products)
            (context as ClientShoppingBagActivity).setTotal(getTotal())
        }
    }

    class ShoppingBagViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textview_name)
        val textViewPrice: TextView = view.findViewById(R.id.textview_price)
        val textViewDiscountedPrice: TextView = view.findViewById(R.id.textview_discounted_price)
        val textviewQuantity: TextView = view.findViewById(R.id.textview_quantity)
        val imageViewProduct: ImageView = view.findViewById(R.id.imageview_product)
        val btnPlus: TextView = view.findViewById(R.id.button_plus)
        val btnMinus: TextView = view.findViewById(R.id.button_minus)
        val imageViewDelete: ImageView = view.findViewById(R.id.imageview_delete)
    }
}