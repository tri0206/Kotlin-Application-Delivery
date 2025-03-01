package com.example.kotlinapplicationdelivery.activities.restaurant.product

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.models.Category
import com.example.kotlinapplicationdelivery.models.Order
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.CategoriesProvider
import com.example.kotlinapplicationdelivery.providers.ProductsProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RestaurantProductEditActivity : AppCompatActivity() {
    val TAG = "ProductFragment"
    private var editTextName: EditText? = null
    private var editTextDescription: EditText? = null
    private var editTextPrice: EditText? = null
    private var imageViewProduct1: ImageView? = null
    private var imageViewProduct2: ImageView? = null
    private var imageViewProduct3: ImageView? = null
    private var buttonUpdate: Button? = null
    private var buttonExit: Button? = null
    var spinnerCategories: Spinner? = null
    val gson = Gson()

    private var imageFile1: File? = null
    private var imageFile2: File? = null
    private var imageFile3: File? = null

    private var categoriesProvider: CategoriesProvider? = null
    private var productsProvider: ProductsProvider? = null
    private var product: Product? = null
    var restaurant: Restaurant? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var categories = ArrayList<Category>()
    var idCategory = ""
    private lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_product_edit)

        product = gson.fromJson(intent.getStringExtra("product"), Product::class.java)
        editTextName = findViewById(R.id.edittext_name)
        editTextDescription = findViewById(R.id.edittext_description)
        editTextPrice = findViewById(R.id.edittext_price)

        editTextName?.setText(product?.name)
        editTextDescription?.setText(product?.description)
        editTextPrice?.setText(product?.price.toString())

        imageViewProduct1 = findViewById(R.id.imageview_image1)
        imageViewProduct2 = findViewById(R.id.imageview_image2)
        imageViewProduct3 = findViewById(R.id.imageview_image3)
        Glide.with(this).load(product?.image1).into(imageViewProduct1!!)
        Glide.with(this).load(product?.image2).into(imageViewProduct2!!)
        Glide.with(this).load(product?.image3).into(imageViewProduct3!!)
        buttonUpdate = findViewById(R.id.btn_update)
        buttonExit = findViewById(R.id.btn_exit)
        spinnerCategories = findViewById(R.id.spinner_categories)

        buttonUpdate?.setOnClickListener { updateProduct() }
        buttonExit?.setOnClickListener { finish() }
        imageViewProduct1?.setOnClickListener { selectImage(101) }
        imageViewProduct2?.setOnClickListener { selectImage(102) }
        imageViewProduct3?.setOnClickListener { selectImage(103) }

        sharedPref = SharedPref(this)

        getUserFromSession()
        getRestaurantFromSession()
        Log.e("tridoan", "onCreateView: " + restaurant?.id )
        categoriesProvider = CategoriesProvider(user?.sessionToken!!)
        productsProvider = ProductsProvider(user?.sessionToken!!)
        getCategories()
    }

    private fun getCategories() {
        categoriesProvider?.getAll()?.enqueue(object: Callback<ArrayList<Category>> {
            override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>
            ) {

                if (response.body() != null) {

                    categories = response.body()!!

                    val arrayAdapter = ArrayAdapter(this@RestaurantProductEditActivity, android.R.layout.simple_dropdown_item_1line, categories)
                    spinnerCategories?.adapter = arrayAdapter

                    val selectedIndex = categories.indexOfFirst { it.id == product?.idCategory }
                    if (selectedIndex != -1) {
                        spinnerCategories?.setSelection(selectedIndex)
                        idCategory = categories[selectedIndex].id!!
                    }
                    spinnerCategories?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, l: Long) {
                            idCategory = categories[position].id!!
                            Log.d(TAG, "Id category: $idCategory")
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                Log.d(TAG, "Error: ${t.message}")
                Toast.makeText(this@RestaurantProductEditActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun updateProduct() {
        val name = editTextName?.text.toString()
        val description = editTextDescription?.text.toString()
        val priceText = editTextPrice?.text.toString()
        val files = ArrayList<File>()

        if (isValidForm(name, description, priceText)) {
            val product = Product(
                id = product?.id,
                name = name,
                description = description,
                price = priceText.toInt(),
                idCategory = idCategory,
                idRestaurant = restaurant?.id
            )
            Log.e("newProduct", "updateProduct: $product", )
//            files.add(imageFile1!!)
//            files.add(imageFile2!!)
//            files.add(imageFile3!!)
            if (imageFile1 != null) files.add(imageFile1!!)
            if (imageFile2 != null) files.add(imageFile2!!)
            if (imageFile3 != null) files.add(imageFile3!!)

            showLoading()

            productsProvider?.update(files, product)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    hideLoading()
                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Body: ${response.body()}")
                    Toast.makeText(this@RestaurantProductEditActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                    if(response.body()?.isSuccess == true) {
                        Toast.makeText(this@RestaurantProductEditActivity, "Cập nhật sản phẩm thành công!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    hideLoading()
                    Toast.makeText(this@RestaurantProductEditActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
    }

    private fun showLoading() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_lottie_loading, null)

        builder.setView(dialogView)
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.window?.setGravity(Gravity.CENTER)


        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun hideLoading() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }
    private fun isValidForm(name: String, description: String, price: String): Boolean {

        if (name.isBlank()) {
            Toast.makeText(this, "Enter the name of the product", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isBlank()) {
            Toast.makeText(this, "Enter the product description", Toast.LENGTH_SHORT).show()
            return false
        }
        if (price.isBlank()) {
            Toast.makeText(this, "Enter the price of the product", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile1 == null) {
            Toast.makeText(this, "Select image 1", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile2 == null) {
            Toast.makeText(this, "Select image 2", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile3 == null) {
            Toast.makeText(this, "Select image 3", Toast.LENGTH_SHORT).show()
            return false
        }
        if (idCategory.isBlank()) {
            Toast.makeText(this, "Select the product category", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {

                val fileUri = data?.data

                when (requestCode) {
                    101 -> {
                        imageFile1 =
                            fileUri?.path?.let { File(it) }
                        imageViewProduct1?.setImageURI(fileUri)
                    }
                    102 -> {
                        imageFile2 =
                            fileUri?.path?.let { File(it) }
                        imageViewProduct2?.setImageURI(fileUri)
                    }
                    103 -> {
                        imageFile3 =
                            fileUri?.path?.let { File(it) }
                        imageViewProduct3?.setImageURI(fileUri)
                    }
                }

            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImage(requestCode: Int) {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(requestCode)
    }

    private fun getRestaurantFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("restaurant").isNullOrBlank()) {
            restaurant = gson.fromJson(sharedPref?.getData("restaurant"), Restaurant::class.java)
            Log.e("tridoan", "getRestaurantFromSession: $restaurant")
        }
    }
}