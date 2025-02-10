package com.example.kotlinapplicationdelivery.fragments.restaurant

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.adapters.RestaurantTabsPagerAdapter
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.RestaurantsProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RestaurantOrdersFragment : Fragment() {
    private var myView: View? = null

    private var viewpager: ViewPager2? = null
    private var tabLayout: TabLayout? = null
    private var switchStatus: SwitchCompat? = null
    var restaurant: Restaurant? = null
    private var restaurantProvider: RestaurantsProvider? = null
    var sharedPref: SharedPref? = null
    var user: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_restaurant_orders, container, false)
        sharedPref = SharedPref(requireActivity())
        getUserFromSession()
        Log.e("tridoan", "getRestaurantFromSession: $restaurant")
        restaurantProvider = RestaurantsProvider(user?.sessionToken!!)
        viewpager = myView?.findViewById(R.id.viewpager)
        tabLayout = myView?.findViewById(R.id.tab_layout)
        switchStatus = myView?.findViewById(R.id.switch_status)
        tabLayout?.setSelectedTabIndicatorColor(Color.BLACK)
        tabLayout?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        tabLayout?.tabTextColors = ContextCompat.getColorStateList(requireContext(), R.color.black)
        tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout?.isInlineLabel = true
        getRestaurant(user?.id!!)
        getRestaurantFromSession()
        val numberOfTabs = 4

        val adapter = RestaurantTabsPagerAdapter(requireActivity().supportFragmentManager, lifecycle, numberOfTabs)
        viewpager?.adapter = adapter
        viewpager?.isUserInputEnabled = true

        TabLayoutMediator(tabLayout!!, viewpager!!) { tab, position ->

            when(position) {
                0 -> {
                    tab.text = "Đơn chờ"
                }
                1 -> {
                    tab.text = "Đã xác nhận"
                }
                2 -> {
                    tab.text = "Đang trên đường"
                }
                3 -> {
                    tab.text = "Đã giao hàng"
                }
            }

        }.attach()
        if(restaurant?.status == "active") {
            switchStatus?.isChecked = true;
            switchStatus!!.text = "Đang mở cửa"
        }
        else {
            switchStatus?.isChecked = false;
            switchStatus!!.text = "Đóng cửa"
        }
        switchStatus?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchStatus!!.text = "Đang mở cửa"
                changeStatus(restaurant?.id!!, "active")
            } else {
                switchStatus!!.text = "Đóng cửa"
                changeStatus(restaurant?.id!!, "inactive")
            }
        }
        return myView
    }
    private fun getRestaurantFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("restaurant").isNullOrBlank()) {
            restaurant = gson.fromJson(sharedPref?.getData("restaurant"), Restaurant::class.java)
        }
    }
    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }
    private fun changeStatus(id: String, status: String) {
        restaurantProvider?.updateStatus(id, status)?.enqueue(object:
            Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {
                    saveRestaurantInSession(response.body()?.data.toString())
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Đã xảy ra lỗi trong yêu cầu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(requireContext(), "Lỗi: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun saveRestaurantInSession(data: String) {

        val sharedPref = SharedPref(requireActivity())
        val gson = Gson()
        val restaurant = gson.fromJson(data, Restaurant::class.java)
        sharedPref.save("restaurant", restaurant)
    }
    private fun getRestaurant(idUser: String) {
        restaurantProvider?.findByUser(idUser)?.enqueue(object:
            Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {
                    saveRestaurantInSession(response.body()?.data.toString())
                } else {
                    Toast.makeText(requireContext(), "Đã xảy ra lỗi trong yêu cầu", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(requireContext(), "Lỗi: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}