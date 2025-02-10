package com.example.kotlinapplicationdelivery.activities.client.restaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.adapters.ProductsAdapter
import com.example.kotlinapplicationdelivery.adapters.RestaurantsAdapter
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

class ClientRestaurantListActivity : AppCompatActivity() {
    var recyclerViewRestaurants: RecyclerView? = null
    var adapter: RestaurantsAdapter? = null

    var user: User? = null
    var sharedPref: SharedPref? = null

    private var restaurantsProvider: RestaurantsProvider? = null
    var restaurants: ArrayList<Restaurant> = ArrayList()

    private var idCategory: String? = null
    private var nameCategory: String? = null
    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_restaurant_list)
        sharedPref = SharedPref(this)
        idCategory = intent.getStringExtra("idCategory")
        nameCategory = intent.getStringExtra("nameCategory")

        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        buttonBack = findViewById(R.id.button_back)
        toolbar?.title = ""
        titleBar?.text = "$nameCategory"
        getUserFromSession()
        restaurantsProvider = RestaurantsProvider(user?.sessionToken!!)

        recyclerViewRestaurants = findViewById(R.id.recyclerview_restaurants)
        recyclerViewRestaurants?.layoutManager = LinearLayoutManager(this)
        buttonBack?.setOnClickListener {
            finish()
        }
        getRestaurants()
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun getRestaurants() {
        restaurantsProvider?.findByCategory(idCategory!!)?.enqueue(object:
            Callback<ArrayList<Restaurant>> {
            override fun onResponse(
                call: Call<ArrayList<Restaurant>>,
                response: Response<ArrayList<Restaurant>>
            ) {

                if (response.body() != null) {
                    restaurants = response.body()!!
                    Log.e("tridoan", "onResponse: $restaurants")
                    restaurants = restaurants.filter { it.status == "active" } as ArrayList<Restaurant>
                    adapter = RestaurantsAdapter(this@ClientRestaurantListActivity, restaurants)
                    recyclerViewRestaurants?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Restaurant>>, t: Throwable) {
                Log.e("tridoan", "onFailure: $t.message")
                Toast.makeText(this@ClientRestaurantListActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}