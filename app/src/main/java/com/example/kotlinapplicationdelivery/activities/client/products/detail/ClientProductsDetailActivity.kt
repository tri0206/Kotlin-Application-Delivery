package com.example.kotlinapplicationdelivery.activities.client.products.detail

import android.annotation.SuppressLint
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

    private var counter = 1
    private var productPrice = 0.0

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

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(product?.image1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(product?.image2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(product?.image3, ScaleTypes.CENTER_CROP))

        imageSlider?.setImageList(imageList)

        textViewName?.text = product?.name
        textViewDescription?.text = product?.description
        textViewPrice?.text = "${product?.price}$"

        imageViewAdd?.setOnClickListener { addItem() }
        imageViewRemove?.setOnClickListener { removeItem() }
        buttonAdd?.setOnClickListener { addToBag() }

        getProductsFromSharedPref()
    }

    private fun addToBag() {
        val index = getIndexOf(product?.id!!) // PRODUCT INDEX IF IT EXISTS IN SHARED PREF

        if (index == -1) { // THIS PRODUCT DOES NOT YET EXIST IN SHARED PREF
            if (product?.quantity == null) {
                product?.quantity = 1
            }
            selectedProducts.add(product!!)
        }
        else { // THE PRODUCT ALREADY EXISTS IN SHARED PREF - WE MUST EDIT THE QUANTITY
            selectedProducts[index].quantity = counter
        }

        sharedPref?.save("order", selectedProducts)
        Toast.makeText(this, "Productivity", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("SetTextI18n")
    private fun getProductsFromSharedPref() {

        if (!sharedPref?.getData("order").isNullOrBlank()) { // THERE IS AN ORDER IN SHARED PREF
            val type = object: TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type)
            val index = getIndexOf(product?.id!!)

            if (index != -1) {
                product?.quantity = selectedProducts[index].quantity
                textViewCounter?.text = "${product?.quantity}"
                productPrice = product?.price!! * product?.quantity!!
                textViewPrice?.text = "${productPrice}$"
                buttonAdd?.text = "Edit product"
                buttonAdd?.backgroundTintList = ColorStateList.valueOf(Color.RED)
            }

            for (p in selectedProducts) {
                Log.d(TAG, "Shared pref: $p")
            }
        }

    }

    // IT IS TO COMPARE IF A PRODUCT ALREADY EXISTS IN SHARED PREF AND THUS BE ABLE TO EDIT THE QUANTITY OF THE SELECTED PRODUCT
    private fun getIndexOf(idProduct: String): Int {

        for ((pos, p) in selectedProducts.withIndex()) {
            if (p.id == idProduct) {
                return pos
            }
        }

        return -1
    }

    private fun addItem() {
        counter++
        productPrice = product?.price!! * counter
        product?.quantity = counter
        textViewCounter?.text = "${product?.quantity}"
        textViewPrice?.text = "${productPrice}$"
    }

    private fun removeItem() {
        if (counter > 1) {
            counter--
            productPrice = product?.price!! * counter
            product?.quantity = counter
            textViewCounter?.text = "${product?.quantity}"
            textViewPrice?.text = "${productPrice}$"
        }
    }
}