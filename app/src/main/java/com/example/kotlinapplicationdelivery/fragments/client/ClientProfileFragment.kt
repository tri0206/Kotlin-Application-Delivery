package com.example.kotlinapplicationdelivery.fragments.client

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.MainActivity
import com.example.kotlinapplicationdelivery.activities.SelectRolesActivity
import com.example.kotlinapplicationdelivery.activities.client.home.ClientHomeActivity
import com.example.kotlinapplicationdelivery.activities.client.update.ClientUpdateActivity
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView

class ClientProfileFragment : Fragment() {

    private var myView: View? = null
    private var buttonSelectRol: Button? = null
    private var buttonUpdateProfile: Button? = null
    private var circleImageUser: CircleImageView? = null
    private var textViewName: TextView? = null
    private var textViewEmail: TextView? = null
    private var textViewPhone: TextView? = null
    private var imageViewLogout: ImageView? = null

    private var sharedPref: SharedPref? = null
    private var user: User? = null
    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_client_profile, container, false)
        sharedPref = SharedPref(requireActivity())

        buttonSelectRol = myView?.findViewById(R.id.btn_select_rol)
        buttonUpdateProfile = myView?.findViewById(R.id.btn_update_profile)
        textViewName = myView?.findViewById(R.id.textview_name)
        textViewEmail = myView?.findViewById(R.id.textview_email)
        textViewPhone = myView?.findViewById(R.id.textview_phone)
        circleImageUser = myView?.findViewById(R.id.circleimage_user)
        imageViewLogout = myView?.findViewById(R.id.imageview_logout)
        titleBar = myView?.findViewById(R.id.custom_toolbar_title)
        buttonSelectRol?.setOnClickListener { goToSelectRol() }
        buttonUpdateProfile?.setOnClickListener { goToUpdate() }
        imageViewLogout?.setOnClickListener { logout() }
        buttonBack = myView?.findViewById(R.id.button_back)
        getUserFromSession()
        toolbar = myView?.findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        toolbar?.title = ""
        titleBar?.text = "Hồ sơ"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        textViewName?.text = "${user?.firstname} ${user?.lastname}"
        textViewEmail?.text = user?.email
        textViewPhone?.text = user?.phone

        if (!user?.image.isNullOrBlank()) {
            Glide.with(requireContext()).load(user?.image).into(circleImageUser!!)
        }

        buttonBack?.setOnClickListener {
            buttonBack?.post {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        return myView
    }



    private fun logout() {
        sharedPref?.remove("user")
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
}