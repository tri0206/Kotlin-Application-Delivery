package com.example.kotlinapplicationdelivery.fragments.client

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.products.list.ClientProductSearchingListActivity
import com.example.kotlinapplicationdelivery.activities.client.restaurant.ClientRestaurantSearchingListActivity
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.adapters.CategoriesAdapter
import com.example.kotlinapplicationdelivery.adapters.ProductsDiscountedAdapter
import com.example.kotlinapplicationdelivery.models.Category
import com.example.kotlinapplicationdelivery.models.DiscountedProduct
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.CategoriesProvider
import com.example.kotlinapplicationdelivery.providers.ProductsProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class ClientCategoriesFragment : Fragment() {

    val TAG = "CategoriesFragment"
    private var myView: View? = null
    var recyclerViewCategories: RecyclerView? = null
    var recyclerViewDiscountedProducts: RecyclerView? = null
    var adapter: CategoriesAdapter? = null
    var adapterProduct: ProductsDiscountedAdapter? = null
    private var categoriesProvider: CategoriesProvider? = null
    private var productsProvider: ProductsProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var categories = ArrayList<Category>()
    var discountedProducts = ArrayList<DiscountedProduct>()
    private var titleBar : TextView? = null
    private  var greeting : TextView? = null;
    private var shoppingBag : ImageView? = null
    private var searchProduct : EditText? = null
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_client_categories, container, false)
        titleBar = myView?.findViewById(R.id.custom_toolbar_title)
        greeting = myView?.findViewById(R.id.greeting)
        shoppingBag = myView?.findViewById(R.id.shopping_bag)
        searchProduct = myView?.findViewById(R.id.findProduct)
        setHasOptionsMenu(true)

        recyclerViewCategories = myView?.findViewById(R.id.recyclerview_categories)
        recyclerViewDiscountedProducts = myView?.findViewById(R.id.rvPopular)
        recyclerViewCategories?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDiscountedProducts?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        sharedPref = SharedPref(requireActivity())

        getUserFromSession()

        categoriesProvider = CategoriesProvider(user?.sessionToken!!)
        productsProvider = ProductsProvider(user?.sessionToken!!)
        greeting?.text = "Hi, ${user!!.firstname}"
        getCategories()
        getDiscountedProducts()
        shoppingBag?.setOnClickListener {goToShoppingBag()}
        searchProduct?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchProduct?.text.toString().trim()
                if (query.isNotEmpty()) {
                    val intent = Intent(requireContext(), ClientRestaurantSearchingListActivity::class.java)
                    intent.putExtra("search_query", query)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
        return myView
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shopping_bag, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_shopping_bag) {
            goToShoppingBag()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToShoppingBag() {
        val i = Intent(requireContext(), ClientShoppingBagActivity::class.java)
        startActivity(i)
    }

    private fun getCategories() {
        categoriesProvider?.getAll()?.enqueue(object: Callback<ArrayList<Category>> {
            override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>
            ) {
                if (response.body() != null) {
                    categories = response.body()!!
                    adapter = CategoriesAdapter(requireActivity(), categories)
                    recyclerViewCategories?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                Log.d(TAG, "Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun getDiscountedProducts() {
        productsProvider?.getDiscountedProducts()?.enqueue(object: Callback<ArrayList<DiscountedProduct>> {
            override fun onResponse(call: Call<ArrayList<DiscountedProduct>>, response: Response<ArrayList<DiscountedProduct>>
            ) {
                if (response.body() != null) {
                    discountedProducts = response.body()!!
                    Log.e("tridoan", "onResponse: $discountedProducts", )
                    adapterProduct = ProductsDiscountedAdapter(requireActivity(), discountedProducts)
                    recyclerViewDiscountedProducts?.adapter = adapterProduct
                }
            }

            override fun onFailure(call: Call<ArrayList<DiscountedProduct>>, t: Throwable) {
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
}