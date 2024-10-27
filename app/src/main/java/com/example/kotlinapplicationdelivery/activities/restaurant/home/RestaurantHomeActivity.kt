package com.example.kotlinapplicationdelivery.activities.restaurant.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.MainActivity
import com.example.kotlinapplicationdelivery.fragments.client.ClientCategoriesFragment
import com.example.kotlinapplicationdelivery.fragments.client.ClientOrdersFragment
import com.example.kotlinapplicationdelivery.fragments.client.ClientProfileFragment
import com.example.kotlinapplicationdelivery.fragments.restaurant.RestaurantCategoryFragment
import com.example.kotlinapplicationdelivery.fragments.restaurant.RestaurantOrdersFragment
import com.example.kotlinapplicationdelivery.fragments.restaurant.RestaurantProductFragment
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class RestaurantHomeActivity: AppCompatActivity() {
    private val TAG = "RestaurantHomeActivity"
    private var btnLogOut : Button? = null
    private var sharedPref: SharedPref?= null
    private var bottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPref = SharedPref(this)
        setContentView(R.layout.activity_restaurant_home)
        //btnLogOut = findViewById(R.id.log_out)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        btnLogOut?.setOnClickListener {
            logOut()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        openFragment(RestaurantOrdersFragment())

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.item_home -> {
                    openFragment(RestaurantOrdersFragment())
                    true
                }

                R.id.item_category -> {
                    openFragment(RestaurantCategoryFragment())
                    true
                }

                R.id.item_profile -> {
                    openFragment(ClientProfileFragment())
                    true
                }
                R.id.item_product -> {
                    openFragment(RestaurantProductFragment())
                    true
                }
                else -> false

            }

        }
        getUserFromSession()
    }

    private fun getUserFromSession() {

        val gson = Gson()

        if(!sharedPref?.getData("user").isNullOrBlank()) {
            val user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
            Log.d(TAG, "getUserFromSession: $user")
        }
    }
    private fun logOut() {
        sharedPref?.remove("user")
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}