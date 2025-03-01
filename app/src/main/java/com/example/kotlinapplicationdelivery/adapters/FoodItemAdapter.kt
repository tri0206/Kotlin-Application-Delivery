package com.example.kotlinapplicationdelivery.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.restaurant.orders.detail.RestaurantOrdersDetailActivity
import com.example.kotlinapplicationdelivery.activities.restaurant.product.RestaurantProductDiscountActivity
import com.example.kotlinapplicationdelivery.activities.restaurant.product.RestaurantProductEditActivity
import com.example.kotlinapplicationdelivery.adapters.ProductsAdapter.ProductsViewHolder
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson

class FoodItemAdapter(val context: Activity, val products: ArrayList<Product>): RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {

    var sharedPref = SharedPref(context)
    val gson = Gson()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item_food, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(
        holder: FoodItemAdapter.FoodItemViewHolder,
        position: Int
    ) {
        val product = products[position]
        Log.e("TAG", "onBindViewHolder: $product", )
        sharedPref = SharedPref(context)
        holder.textViewName.text = product.name
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)
        if(product.price != product.discountPrice) {
            holder.textViewDiscountedStatus.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            showProductInformationDialog(product)
        }
        holder.btnViewFood.setOnClickListener {
            showProductInformationDialog(product)
        }
        holder.btnEditFood.setOnClickListener {
            goToProductEditActivity(product)
        }

        holder.btnDiscount.setOnClickListener {
            goToProductDiscount(product)
        }
    }
    class FoodItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textview_name)
        val textViewDiscountedStatus: TextView = view.findViewById(R.id.txtDiscountStatus)
        val imageViewProduct: ImageView = view.findViewById(R.id.imageview_image)
        val btnViewFood: ImageView = view.findViewById(R.id.btnViewFood)
        val btnEditFood: ImageView = view.findViewById(R.id.btnEditFood)
        val btnDiscount: ImageView = view.findViewById(R.id.btnDiscountFood)
    }
    private fun showProductInformationDialog(product: Product) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_product_infomation, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val name = dialogView.findViewById<EditText>(R.id.edittext_name)
        val description = dialogView.findViewById<EditText>(R.id.edittext_description)
        val price = dialogView.findViewById<EditText>(R.id.edittext_price)
        val cate = dialogView.findViewById<EditText>(R.id.edittext_cate)
        val image1 = dialogView.findViewById<ImageView>(R.id.imageview_image1)
        val image2 = dialogView.findViewById<ImageView>(R.id.imageview_image2)
        val image3 = dialogView.findViewById<ImageView>(R.id.imageview_image3)
        val btnExit = dialogView.findViewById<Button>(R.id.btn_exit)

        name.setText(product.name)
        price.setText(product.price.toString())
        cate.setText(product.nameCategory)
        description.setText(product.description)
        Glide.with(context).load(product.image1).into(image1)
        Glide.with(context).load(product.image2).into(image2)
        Glide.with(context).load(product.image3).into(image3)
//        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
//        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

        val dialog = builder.create()
        btnExit?.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
//    private fun showProductInformationEditDialog(product: Product) {
//
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_product_information_edit, null)
//        val builder = AlertDialog.Builder(context)
//        builder.setView(dialogView)
//        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
//        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)
//        val name = dialogView.findViewById<EditText>(R.id.edittext_name)
//        val description = dialogView.findViewById<EditText>(R.id.edittext_description)
//        val price = dialogView.findViewById<EditText>(R.id.edittext_price)
//        val image1 = dialogView.findViewById<ImageView>(R.id.imageview_image1)
//        val image2 = dialogView.findViewById<ImageView>(R.id.imageview_image2)
//        val image3 = dialogView.findViewById<ImageView>(R.id.imageview_image3)
//
//        name.setText(product.name)
//        price.setText(product.price.toString())
//        description.setText(product.description)
//        Glide.with(context).load(product.image1).into(image1)
//        Glide.with(context).load(product.image2).into(image2)
//        Glide.with(context).load(product.image3).into(image3)
//        val dialog = builder.create()
//        dialog.show()
//        btnCancel?.setOnClickListener { dialog.dismiss() }
//        dialog.window?.setLayout(
//            (context.resources.displayMetrics.widthPixels).toInt(),
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//    }

    private fun goToProductEditActivity(product: Product) {
        val i = Intent(context, RestaurantProductEditActivity::class.java)
        i.putExtra("product", product.toJson())
        context.startActivity(i)
    }

    private fun goToProductDiscount(product: Product) {
        val i = Intent(context, RestaurantProductDiscountActivity::class.java)
        i.putExtra("product", product.toJson())
        context.startActivity(i)
    }

}