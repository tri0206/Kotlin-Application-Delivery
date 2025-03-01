package com.example.kotlinapplicationdelivery.fragments.delivery

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.adapters.OrdersDeliveryAdapter
import com.example.kotlinapplicationdelivery.models.Order
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.OrdersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersStatusFragment : Fragment() {

    private var myView: View? = null
    private var ordersProvider: OrdersProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null

    var recyclerViewOrders: RecyclerView? = null
    var adapter: OrdersDeliveryAdapter? = null

    var status = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_delivery_orders_status, container, false)
        sharedPref = SharedPref(requireActivity())
        status = arguments?.getString("status")!!
        getUserFromSession()
        ordersProvider = OrdersProvider(user?.sessionToken!!)
        recyclerViewOrders = myView?.findViewById(R.id.recyclerview_orders)
        recyclerViewOrders?.layoutManager = LinearLayoutManager(requireContext())
        getOrders()
        return myView
    }

    private fun getOrders() {
        if(status == "DISPATCHED") {
            ordersProvider?.getOrdersByStatus(status)?.enqueue(object:
                Callback<ArrayList<Order>> {
                override fun onResponse(call: Call<ArrayList<Order>>, response: Response<ArrayList<Order>>) {
                    if (response.body() != null) {
                        val orders = response.body()
                        adapter = OrdersDeliveryAdapter(requireActivity(), orders!!)
                        Log.e("manhtri", "onResponse: $orders", )
                        recyclerViewOrders?.adapter = adapter
                    }
                }
                override fun onFailure(call: Call<ArrayList<Order>>, t: Throwable) {
                    Toast.makeText(requireActivity(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        else {
            ordersProvider?.getOrdersByDeliveryAndStatus(user?.id!!, status)?.enqueue(object:
                Callback<ArrayList<Order>> {
                override fun onResponse(call: Call<ArrayList<Order>>, response: Response<ArrayList<Order>>) {
                    if (response.body() != null) {
                        val orders = response.body()
                        Log.e("manhtri", "onResponse: $orders")
                        adapter = OrdersDeliveryAdapter(requireActivity(), orders!!)
                        recyclerViewOrders?.adapter = adapter
                    }
                }
                override fun onFailure(call: Call<ArrayList<Order>>, t: Throwable) {
                    Toast.makeText(requireActivity(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

}