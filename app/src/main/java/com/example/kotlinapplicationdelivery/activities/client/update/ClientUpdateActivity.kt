package com.example.kotlinapplicationdelivery.activities.client.update

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import java.io.File

class ClientUpdateActivity : AppCompatActivity() {

    val TAG = "ClientUpdateActivity"
    private var circleImageUser: CircleImageView? = null
    private var editTextName: EditText? = null
    private var editTextLastname: EditText? = null
    private var editTextPhone: EditText? = null
    private var buttonUpdate: Button? = null
    private lateinit var dialog: AlertDialog
    var sharedPref: SharedPref? = null
    var user: User? = null
    private var imageFile: File? = null
    private var usersProvider: UsersProvider? = null

    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_update)
        sharedPref = SharedPref(this)

        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        buttonBack = findViewById(R.id.button_back)

        circleImageUser = findViewById(R.id.circleimage_user)
        editTextName = findViewById(R.id.edittext_name)
        editTextLastname = findViewById(R.id.edittext_lastname)
        editTextPhone = findViewById(R.id.edittext_phone)
        buttonUpdate = findViewById(R.id.btn_update)
        toolbar = findViewById(R.id.toolbar)
        titleBar?.text = "Chỉnh sửa hồ sơ"
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        getUserFromSession()

        usersProvider = UsersProvider(user?.sessionToken)
        editTextName?.setText(user?.firstname)
        editTextLastname?.setText(user?.lastname)
        editTextPhone?.setText(user?.phone)

        if (!user?.image.isNullOrBlank()) {
            Glide.with(this).load(user?.image).into(circleImageUser!!)
        }

        circleImageUser?.setOnClickListener { selectImage() }
        buttonUpdate?.setOnClickListener { updateData() }
        buttonBack?.setOnClickListener {
            finish()
        }
    }

    private fun updateData() {

        val name = editTextName?.text.toString()
        val lastname = editTextLastname?.text.toString()
        val phone = editTextPhone?.text.toString()

        user?.firstname = name
        user?.lastname = lastname
        user?.phone = phone
        showLoading()
        if (imageFile != null) {
            usersProvider?.update(imageFile!!, user!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: retrofit2.Response<ResponseHttp>) {

                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")

                    Toast.makeText(this@ClientUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                    if(response.body()?.isSuccess != false) {
                        saveUserInSession(response.body()?.data.toString())
                        hideLoading()
                    }

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
        else {
            usersProvider?.updateWithoutImage(user!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: retrofit2.Response<ResponseHttp>) {

                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")
                    Toast.makeText(this@ClientUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                    if(response.body()?.isSuccess != false) {
                        saveUserInSession(response.body()?.data.toString())
                        hideLoading()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }


    }

    private fun saveUserInSession(data: String) {
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref?.save("user", user)
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private val startImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data
                    imageFile =
                        fileUri?.path?.let { File(it) }
                    circleImageUser?.setImageURI(fileUri)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(this, "Task was cancelled", Toast.LENGTH_LONG).show()
                }
            }

        }

    private fun selectImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }
    }
    private fun showLoading() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_lottie_client_loading, null)

        builder.setView(dialogView)
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.window?.setGravity(Gravity.CENTER)


        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


    private fun hideLoading() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }
}