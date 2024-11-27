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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.adapters.CategoriesAdapter
import com.example.kotlinapplicationdelivery.models.Category
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.CategoriesProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class ClientCategoriesFragment : Fragment() {

    val TAG = "CategoriesFragment"
    private var myView: View? = null
    var recyclerViewCategories: RecyclerView? = null

    var adapter: CategoriesAdapter? = null
    private var categoriesProvider: CategoriesProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var categories = ArrayList<Category>()
    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_client_categories, container, false)
        titleBar = myView?.findViewById(R.id.custom_toolbar_title)
        setHasOptionsMenu(true)

        toolbar = myView?.findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
        toolbar?.title = ""
        titleBar?.text = "FoodPicking"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        recyclerViewCategories = myView?.findViewById(R.id.recyclerview_categories)
        recyclerViewCategories?.layoutManager = LinearLayoutManager(requireContext())

        sharedPref = SharedPref(requireActivity())

        getUserFromSession()

        categoriesProvider = CategoriesProvider(user?.sessionToken!!)

        getCategories()

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

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {

            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }

}