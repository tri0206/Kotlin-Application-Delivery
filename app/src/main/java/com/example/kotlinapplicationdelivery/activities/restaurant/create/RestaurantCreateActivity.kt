package com.example.kotlinapplicationdelivery.activities.restaurant.create

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.SelectRolesActivity
import com.example.kotlinapplicationdelivery.activities.client.address.map.ClientAddressMapActivity
import com.example.kotlinapplicationdelivery.models.Address
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.AddressProvider
import com.example.kotlinapplicationdelivery.providers.ProductsProvider
import com.example.kotlinapplicationdelivery.providers.RestaurantsProvider
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RestaurantCreateActivity : AppCompatActivity() {
    private var btnSubmit: Button? = null
    private var editTextName: EditText?= null
    private var editTextPhone: EditText?= null
    private var editTextDescription: EditText?= null
    private var editTextRefPoint: EditText? = null
    private var editTextAddress: EditText? = null
    private var editTextNeighborhood: EditText? = null
    private var imgAvatar: ImageView?= null
    private var addressLat = 0.0
    private var addressLng = 0.0
    var sharedPref: SharedPref? = null
    var user: User? = null
    var restaurant: Restaurant? = null
    private var imageFile: File? = null
    private var addressProvider: AddressProvider? = null
    private var restaurantProvider: RestaurantsProvider? = null
    private var usersProvider: UsersProvider? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_create)

        sharedPref = SharedPref(this)

        getUserFromSession()
        restaurantProvider = RestaurantsProvider(user?.sessionToken!!)
        addressProvider = AddressProvider(user?.sessionToken!!)
        usersProvider = UsersProvider(user?.sessionToken)
        btnSubmit = findViewById(R.id.btn_submit)
        editTextRefPoint = findViewById(R.id.edittext_ref_point)
        editTextAddress = findViewById(R.id.edittext_address)
        editTextNeighborhood = findViewById(R.id.edittext_neighborhood)
        editTextName = findViewById(R.id.et_restaurant_name)
        editTextPhone = findViewById(R.id.et_restaurant_phone)
        editTextDescription = findViewById(R.id.et_restaurant_description)
        imgAvatar = findViewById(R.id.imageview_image)
        editTextRefPoint?.setOnClickListener { goToAddressMap() }
        btnSubmit?.setOnClickListener {
            createRestaurantInfo { success ->
                if (success) {
                    getRestaurantFromSession()
                    Log.e("tridoan", "onCreate: $restaurant")
                    createAddress()
                    registerRoles(user?.id.toString(), 2)
                } else {
                    Toast.makeText(this, "Không thể tạo nhà hàng. Vui lòng thử lại!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        imgAvatar?.setOnClickListener { selectImage(101) }
    }
    @SuppressLint("SetTextI18n")
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val city = data?.getStringExtra("city")
            val address = data?.getStringExtra("address")
            val country = data?.getStringExtra("country")
            addressLat = data?.getDoubleExtra("lat", 0.0)!!
            addressLng = data.getDoubleExtra("lng", 0.0)

            editTextRefPoint?.setText("$address $city")

        }

    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun createAddress() {
        val address = editTextAddress?.text.toString()
        val neighborhood = editTextNeighborhood?.text.toString()

        if (isValidAddressForm(address, neighborhood)) {

            val addressModel = Address(
                address = address,
                neighborhood = neighborhood,
                idUser = user?.id!!,
                lat = addressLat,
                lng = addressLng,
                idRestaurant = restaurant?.id!!
            )

            addressProvider?.create(addressModel)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    if (response.body() != null) {
                        Toast.makeText(this@RestaurantCreateActivity, "Tạo địa chỉ nhà hàng thành công!", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this@RestaurantCreateActivity, "Tạo địa chỉ nhà hàng thất bại!", Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(this@RestaurantCreateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })

        }
    }
    private fun createRestaurantInfo(callback: (Boolean) -> Unit) {
        val name = editTextName?.text.toString()
        val phone = editTextPhone?.text.toString()
        val description = editTextDescription?.text.toString()
        showLoading()
        if (isValidForm(name, phone, description)) {
            val restaurantModel = Restaurant(
                name = name,
                description = description,
                phone = phone,
                idUser = user?.id!!,
                status = "active"
            )

            restaurantProvider?.create(restaurantModel, imageFile!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    if (response.body() != null) {
                        saveRestaurantInSession(response.body()?.data.toString())
                        Toast.makeText(this@RestaurantCreateActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        callback(true) // Thông báo thành công
                        hideLoading()
                    } else {
                        Toast.makeText(this@RestaurantCreateActivity, "Đã xảy ra lỗi trong yêu cầu", Toast.LENGTH_SHORT).show()
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(this@RestaurantCreateActivity, "Lỗi: ${t.message}", Toast.LENGTH_LONG).show()
                    callback(false)
                }
            })
        } else {
            callback(false)
        }
    }

    private fun isValidAddressForm(address: String, neighborhood: String): Boolean {

        if (address.isBlank()) {
            Toast.makeText(this, "Nhập địa chỉ", Toast.LENGTH_SHORT).show()
            return false
        }
        if (neighborhood.isBlank()) {
            Toast.makeText(this, "Nhập khu phố hoặc nơi cư trú", Toast.LENGTH_SHORT).show()
            return false
        }
        if (addressLat == 0.0) {
            Toast.makeText(this, "Chọn điểm tham chiếu", Toast.LENGTH_SHORT).show()
            return false
        }
        if (addressLng == 0.0) {
            Toast.makeText(this, "Chọn điểm tham chiếu", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun goToAddressMap() {
        val i = Intent(this, ClientAddressMapActivity::class.java)
        resultLauncher.launch(i)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {

                val fileUri = data?.data

                when (requestCode) {
                    101 -> {
                        imageFile =
                            fileUri?.path?.let { File(it) }
                        imgAvatar?.setImageURI(fileUri)
                    }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Nhiệm vụ đã bị hủy", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun selectImage(requestCode: Int) {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(requestCode)
    }
    private fun isValidForm(name: String, description: String, phoneNumber: String): Boolean {

        if (name.isBlank()) {
            Toast.makeText(this, "Nhập tên nhà hàng", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isBlank()) {
            Toast.makeText(this, "Nhập giới thiệu về nhà hàng", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phoneNumber.isBlank()) {
            Toast.makeText(this, "Nhập số điện thoại", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile == null) {
            Toast.makeText(this, "Chọn hình ảnh", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun saveRestaurantInSession(data: String) {

        val sharedPref = SharedPref(this)
        val gson = Gson()
        val restaurant = gson.fromJson(data, Restaurant::class.java)
        sharedPref.save("restaurant", restaurant)
    }
    private fun getRestaurantFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("restaurant").isNullOrBlank()) {
            restaurant = gson.fromJson(sharedPref?.getData("restaurant"), Restaurant::class.java)
        }
    }
    private fun registerRoles(id: String, idRole: Int) {
        usersProvider?.registerRoles(id, idRole)?.enqueue(object: Callback<ResponseHttp> {

            override fun onResponse(call: Call<ResponseHttp>, response: retrofit2.Response<ResponseHttp>) {
                val message = response.body()?.message ?: "Không có phản hồi từ server."
                val isSuccess = response.body()?.isSuccess ?: false
                if(isSuccess) {
                    Toast.makeText(this@RestaurantCreateActivity, "Đăng ký quyền nhà hàng thành công!", Toast.LENGTH_LONG).show()
                    saveUserInSession(response.body()?.data.toString())
                    val intent = Intent(this@RestaurantCreateActivity, SelectRolesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    finish()
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                else {
                    Toast.makeText(this@RestaurantCreateActivity, "Thất bại: $message!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@RestaurantCreateActivity, "Lỗi HTTP: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun saveUserInSession(data: String) {
        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
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