package com.example.kotlinapplicationdelivery.fragments.restaurant

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.models.Category
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.CategoriesProvider
import com.example.kotlinapplicationdelivery.providers.ProductsProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RestaurantProductFragment : Fragment() {
    val TAG = "ProductFragment"
    private var myView: View? = null
    private var editTextName: EditText? = null
    private var editTextDescription: EditText? = null
    private var editTextPrice: EditText? = null
    private var imageViewProduct1: ImageView? = null
    private var imageViewProduct2: ImageView? = null
    private var imageViewProduct3: ImageView? = null
    private var buttonCreate: Button? = null
    var spinnerCategories: Spinner? = null

    private var imageFile1: File? = null
    private var imageFile2: File? = null
    private var imageFile3: File? = null

    private var categoriesProvider: CategoriesProvider? = null
    private var productsProvider: ProductsProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var categories = ArrayList<Category>()
    var idCategory = ""
    private lateinit var dialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_restaurant_product, container, false)

        editTextName = myView?.findViewById(R.id.edittext_name)
        editTextDescription = myView?.findViewById(R.id.edittext_description)
        editTextPrice = myView?.findViewById(R.id.edittext_price)
        imageViewProduct1 = myView?.findViewById(R.id.imageview_image1)
        imageViewProduct2 = myView?.findViewById(R.id.imageview_image2)
        imageViewProduct3 = myView?.findViewById(R.id.imageview_image3)
        buttonCreate = myView?.findViewById(R.id.btn_create)
        spinnerCategories = myView?.findViewById(R.id.spinner_categories)

        buttonCreate?.setOnClickListener { createProduct() }
        imageViewProduct1?.setOnClickListener { selectImage(101) }
        imageViewProduct2?.setOnClickListener { selectImage(102) }
        imageViewProduct3?.setOnClickListener { selectImage(103) }

        sharedPref = SharedPref(requireActivity())

        getUserFromSession()
//        showLoading()
        categoriesProvider = CategoriesProvider(user?.sessionToken!!)
        productsProvider = ProductsProvider(user?.sessionToken!!)
        getCategories()

        return myView
    }

    private fun getCategories() {
        categoriesProvider?.getAll()?.enqueue(object: Callback<ArrayList<Category>> {
            override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>
            ) {

                if (response.body() != null) {

                    categories = response.body()!!

                    val arrayAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, categories)
                    spinnerCategories?.adapter = arrayAdapter
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
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {

            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }

    private fun createProduct() {
        val name = editTextName?.text.toString()
        val description = editTextDescription?.text.toString()
        val priceText = editTextPrice?.text.toString()
        val files = ArrayList<File>()

        if (isValidForm(name, description, priceText)) {
            val product = Product(
                name = name,
                description = description,
                price = priceText.toInt(),
                idCategory = idCategory
            )

            files.add(imageFile1!!)
            files.add(imageFile2!!)
            files.add(imageFile3!!)

            showLoading()

            productsProvider?.create(files, product)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    hideLoading()
                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Body: ${response.body()}")
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                    if(response.body()?.isSuccess == true) {
                        resetForm()
                    }
                }
                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    hideLoading()
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
    }
    private fun showLoading() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
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
            Toast.makeText(requireContext(), "Enter the name of the product", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isBlank()) {
            Toast.makeText(requireContext(), "Enter the product description", Toast.LENGTH_SHORT).show()
            return false
        }
        if (price.isBlank()) {
            Toast.makeText(requireContext(), "Enter the price of the product", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile1 == null) {
            Toast.makeText(requireContext(), "Select image 1", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile2 == null) {
            Toast.makeText(requireContext(), "Select image 2", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile3 == null) {
            Toast.makeText(requireContext(), "Select image 3", Toast.LENGTH_SHORT).show()
            return false
        }
        if (idCategory.isBlank()) {
            Toast.makeText(requireContext(), "Select the product category", Toast.LENGTH_SHORT).show()
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
                            fileUri?.path?.let { File(it) } // THE FILE THAT WE ARE GOING TO SAVE AS AN IMAGE ON THE SERVER
                        imageViewProduct1?.setImageURI(fileUri)
                    }
                    102 -> {
                        imageFile2 =
                            fileUri?.path?.let { File(it) } // THE FILE THAT WE ARE GOING TO SAVE AS AN IMAGE ON THE SERVER
                        imageViewProduct2?.setImageURI(fileUri)
                    }
                    103 -> {
                        imageFile3 =
                            fileUri?.path?.let { File(it) } // THE FILE THAT WE ARE GOING TO SAVE AS AN IMAGE ON THE SERVER
                        imageViewProduct3?.setImageURI(fileUri)
                    }
                }

            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetForm() {
        editTextName?.setText("")
        editTextDescription?.setText("")
        editTextPrice?.setText("")
        imageFile1 = null
        imageFile2 = null
        imageFile3 = null
        imageViewProduct1?.setImageResource(R.drawable.ic_image)
        imageViewProduct2?.setImageResource(R.drawable.ic_image)
        imageViewProduct3?.setImageResource(R.drawable.ic_image)
    }


    private fun selectImage(requestCode: Int) {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(requestCode)
    }
}