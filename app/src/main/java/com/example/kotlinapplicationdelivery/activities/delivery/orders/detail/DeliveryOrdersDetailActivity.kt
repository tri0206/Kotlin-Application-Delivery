package com.example.kotlinapplicationdelivery.activities.delivery.orders.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.delivery.home.DeliveryHomeActivity
import com.example.kotlinapplicationdelivery.activities.delivery.orders.map.DeliveryOrdersMapActivity
import com.example.kotlinapplicationdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.kotlinapplicationdelivery.adapters.OrderProductsAdapter
import com.example.kotlinapplicationdelivery.models.Order
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.OrdersProvider
import com.example.kotlinapplicationdelivery.providers.RestaurantsProvider
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersDetailActivity : AppCompatActivity() {
    val TAG = "DeliveryOrdersDetail"
    var order: Order? = null
    val gson = Gson()

    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null

    private var textViewClient: TextView? = null
    private var textViewAddress: TextView? = null
    private var textViewDate: TextView? = null
    private var textViewTotal: TextView? = null
    //private var textViewStatus: TextView? = null
    private var textViewRestaurantName: TextView? = null
    private var textViewRestaurantAddress: TextView? = null
    private var textViewPayment: TextView? = null
    private var recyclerViewProducts: RecyclerView? = null
    private var buttonUpdate: Button? = null
    private var buttonGoToMap: Button? = null
    private var restaurant: Restaurant? = null
    private var buttonOpenMap: Button? = null
    var adapter: OrderProductsAdapter? = null

    private var usersProvider: UsersProvider? = null
    private var ordersProvider: OrdersProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null

    var idDelivery = ""
    private var restaurantsProvider: RestaurantsProvider? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_orders_detail)

        sharedPref = SharedPref(this)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        getUserFromSession()
        restaurantsProvider = RestaurantsProvider(user?.sessionToken!!)
        usersProvider = UsersProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        getRestaurantIno()
        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        buttonBack = findViewById(R.id.button_back)
        toolbar?.title = ""
        titleBar?.text = "Đơn hàng #${order?.id}"

        textViewClient = findViewById(R.id.textview_client)
        textViewAddress = findViewById(R.id.textview_address)
        textViewDate = findViewById(R.id.textview_date)
        textViewTotal = findViewById(R.id.textview_total)
        textViewRestaurantName = findViewById(R.id.textview_restaurant_name)
        textViewRestaurantAddress = findViewById(R.id.textview_restaurant_address)
//        textViewStatus = findViewById(R.id.textview_status)
        textViewPayment = findViewById(R.id.textview_payment_method)
        buttonUpdate = findViewById(R.id.btn_update)
        buttonGoToMap = findViewById(R.id.btn_go_to_map)
        buttonOpenMap = findViewById(R.id.button_open_map)
        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        recyclerViewProducts?.layoutManager = LinearLayoutManager(this)

        adapter = OrderProductsAdapter(this, order?.products!!)
        recyclerViewProducts?.adapter = adapter

        textViewClient?.text = "${order?.client?.lastname} ${order?.client?.firstname}"
        textViewAddress?.text = order?.address?.address
        textViewDate?.text = "${order?.timestamp}"
        //textViewStatus?.text = order?.status
        textViewPayment?.text = order?.payment

        Log.d(TAG, "Order: ${order.toString()}")

        getTotal()

        if (order?.status == "DISPATCHED") {
            buttonUpdate?.visibility = View.VISIBLE
        }

        if (order?.status == "ON THE WAY") {
            buttonGoToMap?.visibility = View.VISIBLE
        }
        buttonUpdate?.setOnClickListener { updateOrder() }
        buttonGoToMap?.setOnClickListener { goToMap() }
        buttonOpenMap?.setOnClickListener { goToRestaurant() }
    }

    private fun updateOrder() {
        order?.idDelivery = user?.id
        Log.e(TAG, "updateOrder: $order")
        ordersProvider?.updateToOnTheWay(order!!)?.enqueue(object: Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {
                    if (response.body()?.isSuccess == true) {
                        Toast.makeText(this@DeliveryOrdersDetailActivity, "Giao hàng bắt đầu", Toast.LENGTH_LONG).show()
                        goToMap()
                    }
                    else {
                        Toast.makeText(this@DeliveryOrdersDetailActivity, "Người giao hàng không thể được chỉ định", Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    Toast.makeText(this@DeliveryOrdersDetailActivity, "There was no response from the server", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@DeliveryOrdersDetailActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun goToMap() {
        val i = Intent(this, DeliveryOrdersMapActivity::class.java)
        i.putExtra("order", order?.toJson())
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getTotal() {
        var total = 0
        for (p in order?.products!!) {
            total += (p.discountPrice!! * p.quantity!!)
        }
        textViewTotal?.text = "${total}đ"
    }

    private fun getRestaurantIno() {
        restaurantsProvider?.findById(order?.idRestaurant!!)?.enqueue(object:
            Callback<ResponseHttp> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseHttp>,
                response: Response<ResponseHttp>
            ) {

                if (response.body() != null) {
                    val gson = Gson()
                    restaurant = gson.fromJson(response.body()!!.data, Restaurant::class.java)
                    Log.e(TAG, "onResponse: $restaurant")
                    textViewRestaurantName?.text = restaurant?.name
                    textViewRestaurantAddress?.text = restaurant?.res_address + ", " + restaurant?.res_neighborhood
                    //txtDescription?.text = restaurant?.description
                    //Glide.with(this@ClientRestaurantDetailActivity).load(restaurant?.image).into(imgBackground!!)
                }
            }
            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun goToRestaurant() {
        val destinationLatitude = restaurant?.latitude
        val destinationLongitude = restaurant?.longitude
        val gmmIntentUri = Uri.parse("google.navigation:q=$destinationLatitude,$destinationLongitude&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(this, "Google Maps chưa được cài đặt", Toast.LENGTH_SHORT).show()
            val appPackageName = "com.google.android.apps.maps"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
    }
}