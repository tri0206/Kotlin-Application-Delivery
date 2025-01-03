package com.example.kotlinapplicationdelivery.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.restaurant.create.RestaurantCreateActivity
import com.example.kotlinapplicationdelivery.adapters.RolesAdapter
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback


class SelectRolesActivity : AppCompatActivity() {
    private var recyclerViewRoles: RecyclerView? = null
    private var user: User? = null
    private var adapter: RolesAdapter? = null
    private var imgRegister: ImageView? = null
    private var usersProvider: UsersProvider? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_roles)
        recyclerViewRoles = findViewById(R.id.recyclerview_roles)
        recyclerViewRoles?.layoutManager = LinearLayoutManager(this)
        imgRegister = findViewById(R.id.img_register)
        getUserFromSession()
        adapter = RolesAdapter(this, user?.roles!!)
        recyclerViewRoles?.adapter = adapter
        usersProvider = UsersProvider(user?.sessionToken)
        checkItemCount()
        imgRegister?.setOnClickListener {
            showRegisterRolesDialog(this)
        }
    }
    private fun checkItemCount() {
        if (adapter!!.itemCount >= 3) {
            imgRegister!!.visibility = View.GONE
        } else {
            imgRegister!!.visibility = View.VISIBLE
        }
    }
    private fun getUserFromSession() {

        val sharedPref = SharedPref(this)
        val gson = Gson()

        if (!sharedPref.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref.getData("user"), User::class.java)
        }
    }

    private fun showRegisterRolesDialog(activity: AppCompatActivity) {
        val inflater = LayoutInflater.from(activity)
        val popupView = inflater.inflate(R.layout.dialog_register_roles, null)


        val alertDialog = AlertDialog.Builder(activity)
            .setView(popupView)
            .setCancelable(true)
            .create()


        val radioGroup = popupView.findViewById<RadioGroup>(R.id.rg_roles)
        val registerButton = popupView.findViewById<Button>(R.id.btn_register)


        registerButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedOption = popupView.findViewById<RadioButton>(selectedId).text
                if(selectedOption == "Giao hàng") {
                    Toast.makeText(activity, "Bạn đã chọn: $selectedOption", Toast.LENGTH_SHORT).show()
                    registerRoles(user?.id.toString(), 3)
                }
                else {
                    Toast.makeText(activity, "Bạn đã chọn: $selectedOption", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SelectRolesActivity, RestaurantCreateActivity::class.java)
                    startActivity(intent)
                    //registerRoles(user?.id.toString(), 2)
                }
                alertDialog.dismiss()
            } else {
                Toast.makeText(activity, "Vui lòng chọn vai trò", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }
    private fun registerRoles(id: String, idRole: Int) {
        usersProvider?.registerRoles(id, idRole)?.enqueue(object: Callback<ResponseHttp> {

            override fun onResponse(call: Call<ResponseHttp>, response: retrofit2.Response<ResponseHttp>) {
                val message = response.body()?.message ?: "Không có phản hồi từ server."
                val isSuccess = response.body()?.isSuccess ?: false
                if(isSuccess) {
                    Toast.makeText(this@SelectRolesActivity, "Đăng ký thành công!", Toast.LENGTH_LONG).show()
                    saveUserInSession(response.body()?.data.toString())
                    val intent = Intent(this@SelectRolesActivity, SelectRolesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    finish()
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                else {
                    Toast.makeText(this@SelectRolesActivity, "Thất bại: $message!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@SelectRolesActivity, "Lỗi HTTP: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun saveUserInSession(data: String) {
        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }
}