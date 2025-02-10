package com.example.kotlinapplicationdelivery.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.home.ClientHomeActivity
import com.example.kotlinapplicationdelivery.activities.delivery.home.DeliveryHomeActivity
import com.example.kotlinapplicationdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.RestaurantsProvider
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.OnSwipeTouchListener
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private var btnToGoRegisterActivity : Button? = null
    private var btnLogin : Button? = null
    private var editTextEmail : EditText? = null
    private var editTextPassword : EditText? = null
    private var forgotPassword : TextView? = null;
    private var usersProvider = UsersProvider()
    private val TAG = "Login Activity"
    var sharedPref: SharedPref? = null
    var imageView: ImageView? = null
    var textView: TextView? = null
    var count: Int = 0

    var user: User? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)
        btnToGoRegisterActivity = findViewById(R.id.register_activity)
        sharedPref = SharedPref(this)
        btnLogin = findViewById(R.id.login)
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        btnLogin?.setOnClickListener {login()}
        forgotPassword = findViewById(R.id.forgot_password_textview)
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        forgotPassword!!.setOnClickListener {
            showForgotPasswordDialog()
        }
        imageView!!.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeTop() {

            }

            override fun onSwipeRight() {
                if (count == 0) {
                    imageView!!.setImageResource(R.drawable.good_night_img)
                    textView!!.text = "Night"
                    count = 1
                } else {
                    imageView!!.setImageResource(R.drawable.good_morning_img)
                    textView!!.text = "Morning"
                    count = 0
                }
            }

            override fun onSwipeLeft() {
                if (count == 0) {
                    imageView!!.setImageResource(R.drawable.good_night_img)
                    textView!!.text = "Night"
                    count = 1
                } else {
                    imageView!!.setImageResource(R.drawable.good_morning_img)
                    textView!!.text = "Morning"
                    count = 0
                }
            }

            override fun onSwipeBottom() {

            }
        })

        btnToGoRegisterActivity?.setOnClickListener { goToRegister() }
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
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref?.save("user", user)

        if(user.roles?.size!! > 1) {
            goToSelectRol()
        }
        else {
            goToClientHome()
        }
    }
    private fun getUserFromSession() {
        val gson = Gson()

        if(!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
            Log.d(TAG, "getUserFromSession: $user")
            if(sharedPref?.getData("rol").isNullOrBlank()) {
                val rol = sharedPref?.getData("rol")?.replace("\"", "")

                when (rol) {
                    "RESTAURANT" -> {
                        goToRestaurantHome()
                    }
                    "CLIENT" -> {
                        goToClientHome()
                    }
                    "REPARTITION" -> {
                        goToDeliveryHome()
                    }
                }
            }
            else {
                goToClientHome()
            }
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
                        Toast.makeText(this@MainActivity, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_LONG).show()
                    }
                }

            })
            //Toast.makeText(this, "Email or password is valid!", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "Email hoặc mật khẩu không hợp lệ!", Toast.LENGTH_LONG).show()
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
    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_forgot_password, null)

        val emailInput = dialogLayout.findViewById<EditText>(R.id.email)
        val sendButton = dialogLayout.findViewById<Button>(R.id.change_password)
        val backButton = dialogLayout.findViewById<Button>(R.id.back)
        val dialog = builder.setView(dialogLayout)
            .setCancelable(true)
            .create()
        dialog.show()
        backButton.setOnClickListener {
            dialog.dismiss()
        }
        sendButton.setOnClickListener {
            val email = emailInput.text.toString()
            if (email.isNotEmpty()) {
                showConfirmationDialog(email, dialog)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showConfirmationDialog(email: String, dialog: AlertDialog) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận mật khẩu đặt lại")
            .setMessage("Bạn có chắc chắn muốn đặt lại mật khẩu cho $email?")
            .setPositiveButton("Yes") { _, _ ->
                sendForgotPasswordRequest(email, dialog)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun sendForgotPasswordRequest(email: String, dialog: AlertDialog) {
        usersProvider.resetPassword(email)?.enqueue(object: Callback<ResponseHttp> {
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
                    Toast.makeText(this@MainActivity, "Mật khẩu mới đã được gửi tới $email", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                else {
                    Toast.makeText(this@MainActivity, "Không thể đặt lại mật khẩu", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}