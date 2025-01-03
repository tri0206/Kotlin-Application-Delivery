package com.example.kotlinapplicationdelivery.activities

import android.content.Intent
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
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private var btnBackToLogin : ImageView? = null
    private var btnRegister : Button?= null
    private var editTextFirstName : EditText?= null
    private var editTextLastName : EditText?= null
    private var editTextEmail : EditText?= null
    private var editTextPhoneNumber : EditText?= null
    private var editTextPassword : EditText?= null
    private var editTextConfirmPassword : EditText?= null

    private var usersProvider = UsersProvider()
    private val TAG = "Login Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        btnBackToLogin = findViewById(R.id.back_to_login)
        btnRegister = findViewById(R.id.register)
        editTextFirstName = findViewById(R.id.first_name)
        editTextLastName = findViewById(R.id.last_name)
        editTextEmail = findViewById(R.id.email)
        editTextPhoneNumber = findViewById(R.id.phone_number)
        editTextPassword = findViewById(R.id.password)
        editTextConfirmPassword = findViewById(R.id.confirm_password)


        btnBackToLogin?.setOnClickListener { backToLogin() }
        btnRegister?.setOnClickListener { register() }
    }
    private fun saveUserInSession(data: String) {

        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }
    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun isValidForm(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        if (firstName.isBlank()) {
            Toast.makeText(this, "Bạn phải nhập tên", Toast.LENGTH_SHORT).show()
            return false
        }

        if (lastName.isBlank()) {
            Toast.makeText(this, "Bạn phải nhập họ và tên đệm", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isBlank()) {
            Toast.makeText(this, "Bạn phải nhập email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phoneNumber.isBlank()) {
            Toast.makeText(this, "Bạn phải nhập số điện thoại", Toast.LENGTH_SHORT).show()
            return false
        }


        if (password.isBlank()) {
            Toast.makeText(this, "Bạn phải nhập mật khẩu", Toast.LENGTH_SHORT).show()
            return false
        }

        if (confirmPassword.isBlank()) {
            Toast.makeText(this, "Bạn phải nhập mật khẩu xác nhận", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!email.isEmailValid()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun register() {
        val firstName = editTextFirstName?.text.toString();
        val lastName = editTextLastName?.text.toString();
        val email = editTextEmail?.text.toString();
        val phoneNumber = editTextPhoneNumber?.text.toString();
        val password = editTextPassword?.text.toString();
        val confirmPassword = editTextConfirmPassword?.text.toString();

        if (isValidForm(firstName = firstName, phoneNumber = phoneNumber, lastName = lastName, email = email, password = password, confirmPassword = confirmPassword)) {

            val user = User(
                firstname = firstName,
                lastname = lastName,
                email = email,
                phone = phoneNumber,
                password = password
            )
            usersProvider.register(user)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                     if(response.body()?.isSuccess == true) {
                         saveUserInSession(response.body()?.data.toString())
                         goToClientHome()
                         Toast.makeText(this@RegisterActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                     }
                    //response.message()
                    Log.d(TAG, "onResponse: $response" + response.body())
                }
                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "onFailure: error ${t.message}")
                    Toast.makeText(this@RegisterActivity, "${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        Log.d(TAG, "register: $firstName, $lastName, $email, $phoneNumber, $password, $confirmPassword")

    }
    private fun goToClientHome() {
        val i = Intent(this, SaveImageActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Delete screen history
        startActivity(i)
    }
    private fun backToLogin() {
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent)
    }
}