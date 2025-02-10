package com.example.kotlinapplicationdelivery.activities.client.restaurant

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.products.list.ClientProductsListActivity
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.adapters.ProductsAdapter
import com.example.kotlinapplicationdelivery.adapters.RestaurantsAdapter
import com.example.kotlinapplicationdelivery.adapters.ShoppingBagAdapter
import com.example.kotlinapplicationdelivery.fragments.client.ClientCategoriesFragment
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.ProductsProvider
import com.example.kotlinapplicationdelivery.providers.RestaurantsProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientRestaurantDetailActivity : AppCompatActivity() {
    private var imgBackground: ImageView? = null
    private var txtName: TextView? = null
    private var txtAddress: TextView? = null
    private var txtDescription: TextView? = null
    private var recyclerViewProducts: RecyclerView? = null
    private var idRestaurant: String? = null
    private var btnBack: ImageButton? = null
    private var btnSearch: ImageButton? = null
    private var searchEditText: EditText? = null
    private var totalPrice: TextView? = null
    var adapter: ProductsAdapter? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    private var cartOverlay: LinearLayout? = null
    private var cartIcon: ImageView? = null
    private var productsProvider: ProductsProvider? = null
    private var restaurantsProvider: RestaurantsProvider? = null
    var products: ArrayList<Product> = ArrayList()
    var restaurant: Restaurant? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_restaurant_detail)
        sharedPref = SharedPref(this)
        imgBackground = findViewById(R.id.restaurant_cover_image)
        txtName = findViewById(R.id.restaurant_name)
        txtAddress = findViewById(R.id.restaurant_address)
        txtDescription = findViewById(R.id.restaurant_description)
        btnBack = findViewById(R.id.btnBack)
        btnSearch = findViewById(R.id.btnSearch)
        searchEditText = findViewById(R.id.search_edit_text)
        totalPrice = findViewById(R.id.total_price)
        recyclerViewProducts = findViewById(R.id.restaurant_menu_recyclerview)
        cartOverlay = findViewById(R.id.cart_overlay)
        cartIcon = findViewById(R.id.cart_icon)
        recyclerViewProducts?.layoutManager = LinearLayoutManager(this)
        getUserFromSession()
        idRestaurant = intent.getStringExtra("restaurant_id")
        restaurantsProvider = RestaurantsProvider(user?.sessionToken!!)
        getRestaurantIno()
//        Glide.with(this).load(intent.getStringExtra("restaurant_image")).into(imgBackground!!)
        productsProvider = ProductsProvider(user?.sessionToken!!)
        updateCartOverlayVisibility()
        cartIcon?.setOnClickListener { goToShoppingBag() }
        btnBack?.setOnClickListener {
            if(!sharedPref?.getData("order").isNullOrBlank()) {
                sharedPref?.remove("order")
            }
            finish();
        }
//        updateCartOverlayVisibility()
        btnSearch?.setOnClickListener {toggleSearchBar()}
        getProducts()
        searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText?.text.toString().trim()
                if (query.isNotEmpty()) {
                    val intent = Intent(this, ClientProductsListActivity::class.java)
                    intent.putExtra("search_query", query)
                    intent.putExtra("id_restaurant", idRestaurant)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
        cartOverlay?.setOnClickListener {goToShoppingBag()}
    }

    override fun onResume() {
        super.onResume()
        updateCartOverlayVisibility()
    }
    private fun getProducts() {
        productsProvider?.findByRestaurant(idRestaurant!!)?.enqueue(object:
            Callback<ArrayList<Product>> {
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.body() != null) {
                    products = response.body()!!
                    Log.e("product", "onResponse: $products")
                    adapter = ProductsAdapter(this@ClientRestaurantDetailActivity, products)
                    recyclerViewProducts?.adapter = adapter
                }
            }
            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                Toast.makeText(this@ClientRestaurantDetailActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.d("tridoan", "Error: ${t.message}")
            }
        })
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }
    private fun toggleSearchBar() {
        if (searchEditText!!.visibility == View.GONE) {
            searchEditText!!.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(searchEditText, "alpha", 0f, 1f).apply {
                duration = 300
                start()
            }
            btnSearch?.setBackgroundResource(R.drawable.ic_close)
        } else {
            ObjectAnimator.ofFloat(searchEditText, "alpha", 1f, 0f).apply {
                duration = 300
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {

                    }

                    override fun onAnimationEnd(p0: Animator) {
                        searchEditText!!.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator) {

                    }

                    override fun onAnimationRepeat(p0: Animator) {

                    }
                })
                start()
            }
            btnSearch?.setBackgroundResource(R.drawable.ic_search)
        }
    }
    fun updateCartOverlayVisibility() {
        if (!sharedPref?.getData("order").isNullOrBlank()) {
            cartOverlay?.visibility = View.VISIBLE
        }
        else {
            cartOverlay?.visibility = View.GONE
        }
    }
    fun setTotal(total: Int) {
        totalPrice?.text = "$total"
    }
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
        if(!sharedPref?.getData("order").isNullOrBlank()) {
            sharedPref?.remove("order")
        }
    }
    private fun goToShoppingBag() {
        val i = Intent(this, ClientShoppingBagActivity::class.java)
        startActivity(i)
    }
    private fun getRestaurantIno() {
        Log.e("tridoan", "onResponse: 1")
        restaurantsProvider?.findById(idRestaurant!!)?.enqueue(object:
            Callback<ResponseHttp> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseHttp>,
                response: Response<ResponseHttp>
            ) {

                if (response.body() != null) {
                    val gson = Gson()
                    restaurant = gson.fromJson(response.body()!!.data, Restaurant::class.java)
                    Log.e("tridoan_restaurant", "onResponse: $restaurant")
                    txtName?.text = restaurant?.name
                    txtAddress?.text = restaurant?.res_address + ", " + restaurant?.res_neighborhood
                    txtDescription?.text = restaurant?.description
                    Glide.with(this@ClientRestaurantDetailActivity).load(restaurant?.image).into(imgBackground!!)
                }
            }
            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}