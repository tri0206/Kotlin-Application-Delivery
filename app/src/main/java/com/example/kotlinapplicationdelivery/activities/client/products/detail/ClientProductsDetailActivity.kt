package com.example.kotlinapplicationdelivery.activities.client.products.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ClientProductsDetailActivity : AppCompatActivity() {
    val TAG = "ProductsDetail"
    private var product: Product? = null
    val gson = Gson()

    private var imageSlider: ImageSlider? = null
    private var textViewName: TextView? = null
    private var textViewDescription: TextView? = null
    private var textViewPrice: TextView? = null
    private var textViewCounter: TextView? = null
    private var imageViewAdd: ImageView? = null
    private var imageViewRemove: ImageView? = null
    private var buttonAdd: Button? = null
    private var bag: ImageView? = null
    private var counter = 1
    private var productPrice = 0

    var sharedPref: SharedPref? = null
    private var selectedProducts = ArrayList<Product>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_products_detail)

        product = gson.fromJson(intent.getStringExtra("product"), Product::class.java)
        sharedPref = SharedPref(this)

        imageSlider = findViewById(R.id.imageslider)
        textViewName = findViewById(R.id.textview_name)
        textViewDescription = findViewById(R.id.textview_description)
        textViewPrice = findViewById(R.id.textview_price)
        textViewCounter = findViewById(R.id.textview_counter)
        imageViewAdd = findViewById(R.id.imageview_add)
        imageViewRemove = findViewById(R.id.imageview_remove)
        buttonAdd = findViewById(R.id.btn_add_product)
        bag = findViewById(R.id.shopping_bag)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(product?.image1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(product?.image2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(product?.image3, ScaleTypes.CENTER_CROP))

        imageSlider?.setImageList(imageList)

        textViewName?.text = product?.name
        textViewDescription?.text = product?.description
        textViewPrice?.text = "${product?.price} VND"

        imageViewAdd?.setOnClickListener { addItem() }
        imageViewRemove?.setOnClickListener { removeItem() }
        buttonAdd?.setOnClickListener { addToBag() }
        bag?.setOnClickListener { goToShoppingBag() }
        getProductsFromSharedPref()
    }

    private fun addToBag() {
        val index = getIndexOf(product?.id!!)

        if (index == -1) {
            if (product?.quantity == null) {
                product?.quantity = 1
            }
            selectedProducts.add(product!!)
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_LONG).show()
        }
        else {
            selectedProducts[index].quantity = counter
            Toast.makeText(this, "Đã chỉnh sửa món ăn", Toast.LENGTH_LONG).show()
        }

        sharedPref?.save("order", selectedProducts)

    }

    @SuppressLint("SetTextI18n")
    private fun getProductsFromSharedPref() {

        if (!sharedPref?.getData("order").isNullOrBlank()) {
            val type = object: TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type)
            val index = getIndexOf(product?.id!!)

            if (index != -1) {
                product?.quantity = selectedProducts[index].quantity
                textViewCounter?.text = "${product?.quantity}"
                productPrice = product?.price!! * product?.quantity!!
                textViewPrice?.text = "$productPrice VND"
                buttonAdd?.backgroundTintList = ColorStateList.valueOf(Color.RED)
            }
            for (p in selectedProducts) {
                Log.d(TAG, "Shared pref: $p")
            }
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

    @SuppressLint("SetTextI18n")
    private fun addItem() {
        counter++
        productPrice = product?.price!! * counter
        product?.quantity = counter
        textViewCounter?.text = "${product?.quantity}"
        textViewPrice?.text = "$productPrice VND"
    }

    @SuppressLint("SetTextI18n")
    private fun removeItem() {
        if (counter > 1) {
            counter--
            productPrice = product?.price!! * counter
            product?.quantity = counter
            textViewCounter?.text = "${product?.quantity}"
            textViewPrice?.text = "$productPrice VND"
        }
    }
    private fun goToShoppingBag() {
        val i = Intent(this, ClientShoppingBagActivity::class.java)
        startActivity(i)
    }
}