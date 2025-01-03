package com.example.kotlinapplicationdelivery.activities.client.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.MainActivity
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.ClientPaymentZaloFormActivity
import com.example.kotlinapplicationdelivery.fragments.ProfileFragment
import com.example.kotlinapplicationdelivery.fragments.client.ClientCategoriesFragment
import com.example.kotlinapplicationdelivery.fragments.client.ClientOrdersFragment
import com.example.kotlinapplicationdelivery.fragments.client.ClientProfileFragment
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class ClientHomeActivity : AppCompatActivity() {
    private val TAG = "ClientHomeActivity"
    private var btnLogOut : Button? = null
    private var sharedPref: SharedPref?= null
    private var bottomNavigation: BottomNavigationView? = null
    var user: User ?= null
    private var usersProvider: UsersProvider? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(this)
        setContentView(R.layout.activity_client_home)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        openFragment(ClientCategoriesFragment())

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.item_home -> {
                    openFragment(ClientCategoriesFragment())
                    true
                }

                R.id.item_orders -> {
                    openFragment(ClientOrdersFragment())
                    true
                }

                R.id.item_profile -> {
                    openFragment(ProfileFragment())
                    true
                }

                else -> false

            }

        }
        getUserFromSession()

        usersProvider = UsersProvider(token = user?.sessionToken!!)
        createToken()
    }

    private fun createToken() {
        usersProvider?.createToken(user!!, this)
    }


    private fun getUserFromSession() {

        val gson = Gson()

        if(!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
            Log.d(TAG, "getUserFromSession: $user")
        }
    }
    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)

        if (currentFragment is ClientCategoriesFragment) {
            AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có chắc chắn muốn thoát?")
                .setPositiveButton("Có", fun(_: DialogInterface, _: Int) {
                    super.onBackPressed()
                    finish()
                })
                .setNegativeButton("Không", null)
                .show()
        } else {
            openFragment(ClientCategoriesFragment())
            bottomNavigation?.selectedItemId = R.id.item_home
        }
    }

}