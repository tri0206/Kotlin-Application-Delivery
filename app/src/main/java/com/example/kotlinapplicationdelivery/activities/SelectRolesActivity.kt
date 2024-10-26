package com.example.kotlinapplicationdelivery.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.adapters.RolesAdapter
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson

class SelectRolesActivity : AppCompatActivity() {
    private var recyclerViewRoles: RecyclerView? = null
    private var user: User? = null
    private var adapter: RolesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_roles)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerViewRoles = findViewById(R.id.recyclerview_roles)
        recyclerViewRoles?.layoutManager = LinearLayoutManager(this)

        getUserFromSession()

        adapter = RolesAdapter(this, user?.roles!!)
        recyclerViewRoles?.adapter = adapter

    }
    private fun getUserFromSession() {

        val sharedPref = SharedPref(this)
        val gson = Gson()

        //IF USER EXISTS IN SESSION
        if (!sharedPref.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref.getData("user"), User::class.java)
        }

    }
}