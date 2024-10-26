package com.example.kotlinapplicationdelivery.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.home.ClientHomeActivity
import com.example.kotlinapplicationdelivery.activities.delivery.home.DeliveryHomeActivity
import com.example.kotlinapplicationdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var btnToGoRegisterActivity : ImageView? = null
    private var btnLogin : Button? = null
    private var editTextEmail : EditText? = null
    private var editTextPassword : EditText? = null
    private var usersProvider = UsersProvider()
    private val TAG = "Login Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        btnToGoRegisterActivity = findViewById(R.id.register_activity)
        btnLogin = findViewById(R.id.login)
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        btnLogin?.setOnClickListener {login()}
        btnToGoRegisterActivity?.setOnClickListener { goToRegister() }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getUserFromSession()
    }

    private fun goToClientHome() {
        val i = Intent(this, ClientHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
    private fun goToRestaurantHome() {
        val i = Intent(this, RestaurantHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
    private fun goToDeliveryHome() {
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun saveUserInSession(data: String) {

        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)

        if(user.roles?.size!! > 1) {
            goToSelectRol()
        }
        else {
            goToClientHome()
        }
    }
    private fun getUserFromSession() {

        val sharedPref = SharedPref(this)
        val gson = Gson()

        if(!sharedPref.getData("user").isNullOrBlank()) {
            val user = gson.fromJson(sharedPref.getData("user"), User::class.java)
            Log.d(TAG, "getUserFromSession: $user")
            if(sharedPref.getData("rol").isNullOrBlank()) {
                val rol = sharedPref.getData("rol")?.replace("\"", "")

                when (rol) {
                    "RESTAURANTE" -> {
                        goToRestaurantHome()
                    }
                    "CLIENTE" -> {
                        goToClientHome()
                    }
                    "REPARTIDOR" -> {
                        goToDeliveryHome()
                    }
                }
            }
        }
        else {
            goToClientHome()
        }
    }
    private fun String.isEmailValid() : Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun isValidForm(email : String, password : String) : Boolean {
        if(email.isBlank()) {
            return false
        }
        if(password.isBlank()) {
            return false;
        }
        if(!email.isEmailValid()) {
            return false
        }
        return true;
    }
    private fun login() {
        val email = editTextEmail?.text.toString();
        val password = editTextPassword?.text.toString();

        if(isValidForm(email, password)) {

            usersProvider.login(email, password)?.enqueue(object: Callback<ResponseHttp> {
                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d("MainActivity", "There was an error ${t.message}")
                    Toast.makeText(this@MainActivity, "There was an error ${t.message}", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {

                    Log.d("MainActivity", "Response: ${response.body()}")

                    if (response.body()?.isSuccess == true) {
                        Toast.makeText(this@MainActivity, response.body()?.message, Toast.LENGTH_LONG).show()

                        saveUserInSession(response.body()?.data.toString())
                    }
                    else {
                        Toast.makeText(this@MainActivity, "The data is not correct", Toast.LENGTH_LONG).show()
                    }
                }

            })
            //Toast.makeText(this, "Email or password is valid!", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "Email or password is invalid!", Toast.LENGTH_LONG).show()
        }
        Log.d(TAG, "login: $email, $password")

    }
    private fun goToSelectRol() {
        val i = Intent(this, SelectRolesActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
    private fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}