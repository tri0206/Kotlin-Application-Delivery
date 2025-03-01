package com.example.kotlinapplicationdelivery.fragments.restaurant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.adapters.FoodItemAdapter
import com.example.kotlinapplicationdelivery.adapters.ProductsAdapter
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.ProductsProvider
import com.example.kotlinapplicationdelivery.providers.RestaurantsProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RestaurantFoodManagement : Fragment() {

    private var myView: View? = null
    var adapter: FoodItemAdapter? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var restaurant: Restaurant? = null
    private var productsProvider: ProductsProvider? = null
    var products: ArrayList<Product> = ArrayList()
    private var recyclerViewFoods: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_restaurant_food_management, container, false)
        sharedPref = SharedPref(requireActivity())
        getUserFromSession()
        getRestaurantFromSession()
        productsProvider = ProductsProvider(user?.sessionToken!!)
        recyclerViewFoods = myView?.findViewById(R.id.recyclerViewFood)
        getProducts()
        return myView
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun getProducts() {
        productsProvider?.findByRestaurant(restaurant?.id!!)?.enqueue(object:
            Callback<ArrayList<Product>> {
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.body() != null) {
                    products = response.body()!!
                    Log.e("product", "onResponse: $products")
                    adapter = FoodItemAdapter(requireActivity(), products)
                    recyclerViewFoods?.adapter = adapter
                }
            }
            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                Log.d("tridoan", "Error: ${t.message}")
            }
        })
    }

    private fun getRestaurantFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("restaurant").isNullOrBlank()) {
            restaurant = gson.fromJson(sharedPref?.getData("restaurant"), Restaurant::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        getProducts()
    }
}