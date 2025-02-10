package com.example.kotlinapplicationdelivery.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.MainActivity
import com.example.kotlinapplicationdelivery.activities.SelectRolesActivity
import com.example.kotlinapplicationdelivery.activities.client.update.ClientUpdateActivity
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {
    private var myView: View? = null
    private var buttonSelectRol: ImageView? = null
    private var buttonUpdateProfile: ImageView? = null
    private var buttonUpdatePassword: ImageView? = null
    private var circleImageUser: CircleImageView? = null
    private var textViewName: TextView? = null
    private var textViewEmail: TextView? = null
    private var textViewPhone: TextView? = null
    private var btnLogout: Button? = null

    private var sharedPref: SharedPref? = null
    private var user: User? = null
    private var usersProvider: UsersProvider? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myView = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPref = SharedPref(requireActivity())

        buttonSelectRol = myView?.findViewById(R.id.btn_select_rol)
        buttonUpdateProfile = myView?.findViewById(R.id.btn_update_profile)
        textViewName = myView?.findViewById(R.id.textview_name)
        textViewEmail = myView?.findViewById(R.id.textview_email)
        textViewPhone = myView?.findViewById(R.id.textview_phone)
        circleImageUser = myView?.findViewById(R.id.circleimage_user)
        btnLogout = myView?.findViewById(R.id.btn_logout)
        buttonUpdatePassword = myView?.findViewById(R.id.btn_update_password)
        buttonSelectRol?.setOnClickListener { goToSelectRol() }
        buttonUpdateProfile?.setOnClickListener { goToUpdate() }
        btnLogout?.setOnClickListener { logout() }
        buttonUpdatePassword?.setOnClickListener { showChangePasswordDialog() }
        getUserFromSession()
        usersProvider = UsersProvider(user?.sessionToken)
        textViewName?.text = "${user?.firstname} ${user?.lastname}"
        textViewEmail?.text = user?.email
        textViewPhone?.text = user?.phone

        if (!user?.image.isNullOrBlank()) {
            Glide.with(requireContext()).load(user?.image).into(circleImageUser!!)
        }

        return myView
    }

    private fun logout() {
        while(!sharedPref?.getData("order").isNullOrBlank()) {
            sharedPref?.remove("order")
        }
        while(!sharedPref?.getData("user").isNullOrBlank()) {
            sharedPref?.remove("user")
        }
        val i = Intent(requireContext(), MainActivity::class.java)
        startActivity(i)
    }
    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }
    private fun goToSelectRol() {
        val i = Intent(requireContext(), SelectRolesActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
    private fun goToUpdate() {
        val i = Intent(requireContext(), ClientUpdateActivity::class.java)
        startActivity(i)
    }

    private fun showChangePasswordDialog() {
        // Inflate layout dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)

        // Tạo AlertDialog
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)


        val etOldPassword = dialogView.findViewById<EditText>(R.id.etOldPassword)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)
        val btnChangePassword = dialogView.findViewById<Button>(R.id.btnChangePassword)

        val dialog = builder.create()


        btnChangePassword.setOnClickListener {
            val oldPassword = etOldPassword.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            when {
                oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                }
                newPassword != confirmPassword -> {
                    Toast.makeText(context, "Mật khẩu mới không khớp!", Toast.LENGTH_SHORT).show()
                }
                newPassword == oldPassword -> {
                    Toast.makeText(context, "Mật khẩu mới của bạn trùng với mật khẩu cũ!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    changePassword(oldPassword, newPassword, user!!.email)
                    dialog.dismiss()
                }
            }
        }

        // Hiển thị dialog
        dialog.show()
    }

    private fun changePassword(oldPassword: String, newPassword: String, email: String) {
        usersProvider?.changePassword(oldPassword, newPassword, email)?.enqueue(object: Callback<ResponseHttp> {
            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Log.d("MainActivity", "There was an error ${t.message}")
                Toast.makeText(context, "${t.message}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseHttp>,
                response: Response<ResponseHttp>
            ) {

                Log.d("MainActivity", "Response: ${response.body()}")

                if (response.body()?.isSuccess == true) {
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

}