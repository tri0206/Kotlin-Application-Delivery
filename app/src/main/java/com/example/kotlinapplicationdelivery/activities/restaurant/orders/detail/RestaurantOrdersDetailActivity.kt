package com.example.kotlinapplicationdelivery.activities.restaurant.orders.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
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
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.kotlinapplicationdelivery.adapters.OrderProductsAdapter
import com.example.kotlinapplicationdelivery.models.Order
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.OrdersProvider
import com.example.kotlinapplicationdelivery.providers.UsersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantOrdersDetailActivity : AppCompatActivity() {
    val TAG = "ClientOrdersDetail"
    var order: Order? = null
    val gson = Gson()

    private var textViewClient: TextView? = null
    private var textViewAddress: TextView? = null
    private var textViewDate: TextView? = null
    private var textViewTotal: TextView? = null
    private var textViewStatus: TextView? = null
    private var textViewDelivery: TextView? = null
    private var textViewDeliveryAssigned: TextView? = null
    private var recyclerViewProducts: RecyclerView? = null
    private var buttonUpdate: Button? = null
    private var textviewPaymentMethod: TextView? = null
    var adapter: OrderProductsAdapter? = null

    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null

    private var usersProvider: UsersProvider? = null
    private var ordersProvider: OrdersProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null

    //var spinnerDeliveryMen: Spinner? = null
    private var idDelivery = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_orders_detail)

        sharedPref = SharedPref(this)
        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)
        getUserFromSession()

        usersProvider = UsersProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        buttonBack = findViewById(R.id.button_back)
        toolbar?.title = ""
        titleBar?.text = "Đơn hàng #${order?.id}"

        textViewClient = findViewById(R.id.textview_client)
        textViewAddress = findViewById(R.id.textview_address)
        textViewDate = findViewById(R.id.textview_date)
        textViewTotal = findViewById(R.id.textview_total)
        textViewStatus = findViewById(R.id.textview_status)
        textviewPaymentMethod = findViewById(R.id.textview_payment_method)
//        textViewDelivery = findViewById(R.id.textview_delivery)
//        textViewDeliveryAssigned = findViewById(R.id.textview_delivery_assigned)

        textViewDelivery = findViewById(R.id.textview_delivery)
        //spinnerDeliveryMen = findViewById(R.id.spinner_delivery_men)
        buttonUpdate = findViewById(R.id.btn_update)

        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        recyclerViewProducts?.layoutManager = LinearLayoutManager(this)

        adapter = OrderProductsAdapter(this, order?.products!!)
        recyclerViewProducts?.adapter = adapter

        textViewClient?.text = "${order?.client?.firstname} ${order?.client?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewDate?.text = "${order?.timestamp}"
        textViewStatus?.text = order?.note
        if(order?.delivery?.firstname == null) {
            textViewDelivery?.text = null
        }
        else {
            textViewDelivery?.text = "${order?.delivery?.lastname} ${order?.delivery?.firstname}"
        }
        textviewPaymentMethod?.text = order?.payment
        Log.d(TAG, "Order: ${order.toString()}")

        getTotal()


        if (order?.status == "PAID") {
            buttonUpdate?.visibility = View.VISIBLE
//            textViewDeliveryAvailable?.visibility = View.VISIBLE
//            spinnerDeliveryMen?.visibility = View.VISIBLE
        }

        if (order?.status != "PAID") {
            textViewDeliveryAssigned?.visibility = View.VISIBLE
            textViewDelivery?.visibility = View.VISIBLE
        }

        buttonUpdate?.setOnClickListener { updateOrder() }
        buttonBack?.setOnClickListener { finish() }
    }

    private fun updateOrder() {
        //order?.idDelivery = idDelivery
        Log.e(TAG, "updateOrder: $order")
        ordersProvider?.updateToDispatched(order!!)?.enqueue(object: Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {
                    if (response.body()?.isSuccess == true) {
                        Toast.makeText(this@RestaurantOrdersDetailActivity, "Đơn hàng đã được xác nhận!", Toast.LENGTH_SHORT).show()
                        goToOrders()
                    }
                    else {
                        Toast.makeText(this@RestaurantOrdersDetailActivity, "Đơn hàng không được xác nhận! Đã có lỗi xảy ra.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this@RestaurantOrdersDetailActivity, "Không có phản hồi từ máy chủ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@RestaurantOrdersDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun goToOrders() {
        val i = Intent(this, RestaurantHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

//    private fun getDeliveryMen() {
//        usersProvider?.getDeliveryMen()?.enqueue(object: Callback<ArrayList<User>> {
//            override fun onResponse(call: Call<ArrayList<User>>, response: Response<ArrayList<User>>) {
//                if (response.body() != null) {
//                    val deliveryMen = response.body()
//                    val arrayAdapter = ArrayAdapter<User>(this@RestaurantOrdersDetailActivity, android.R.layout.simple_dropdown_item_1line, deliveryMen!!)
//                    spinnerDeliveryMen?.adapter = arrayAdapter
//                    spinnerDeliveryMen?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, l: Long) {
//                            idDelivery = deliveryMen[position].id!! // SELECTING THE DELIVERY ID FROM THE SPINNER
//                            Log.d(TAG, "Id Delivery: $idDelivery")
//                        }
//
//                        override fun onNothingSelected(p0: AdapterView<*>?) {
//
//                        }
//                    }
//
//                }
//
//            }
//
//            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
//                Toast.makeText(this@RestaurantOrdersDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }

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
}