package com.example.kotlinapplicationdelivery.activities.client.products.list

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
import com.example.kotlinapplicationdelivery.activities.client.home.ClientHomeActivity
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.adapters.ProductsAdapter
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.ProductsProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientProductsListActivity : AppCompatActivity() {

    val TAG = "ClientProducts"
    var recyclerViewProducts: RecyclerView? = null
    var adapter: ProductsAdapter? = null

    var user: User? = null
    var sharedPref: SharedPref? = null

    private var productsProvider: ProductsProvider? = null
    var products: ArrayList<Product> = ArrayList()

    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null
    private var bag : ImageView? = null
    private var query: String? = null
    private var idRestaurant: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_products_list)

        sharedPref = SharedPref(this)

        query = intent.getStringExtra("search_query")
        idRestaurant = intent.getStringExtra("id_restaurant")
        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        bag = findViewById(R.id.shopping_bag)
        buttonBack = findViewById(R.id.button_back)
        toolbar?.title = ""
        titleBar?.text = "$query"
        getUserFromSession()
        productsProvider = ProductsProvider(user?.sessionToken!!)

        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        recyclerViewProducts?.layoutManager = LinearLayoutManager(this)

        buttonBack?.setOnClickListener {
            finish()
        }
        bag?.setOnClickListener { goToShoppingBag() }
        getProducts()
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun getProducts() {
        productsProvider?.findByQuery(query!!, idRestaurant!!)?.enqueue(object:
            Callback<ArrayList<Product>> {
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.body() != null) {
                    products = response.body()!!
                    Log.e("search product", "onResponse: $products")
                    adapter = ProductsAdapter(this@ClientProductsListActivity, products)
                    recyclerViewProducts?.adapter = adapter
                }

            }
            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                Toast.makeText(this@ClientProductsListActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Error: ${t.message}")
            }

        })
    }
    private fun goToShoppingBag() {
        val i = Intent(this, ClientShoppingBagActivity::class.java)
        startActivity(i)
    }
}