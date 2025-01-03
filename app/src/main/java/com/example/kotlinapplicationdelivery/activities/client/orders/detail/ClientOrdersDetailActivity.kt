package com.example.kotlinapplicationdelivery.activities.client.orders.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.orders.map.ClientOrdersMapActivity
import com.example.kotlinapplicationdelivery.adapters.OrderProductsAdapter
import com.example.kotlinapplicationdelivery.models.Order
import com.google.gson.Gson

class ClientOrdersDetailActivity : AppCompatActivity() {
    val TAG = "ClientOrdersDetail"
    var order: Order? = null
    val gson = Gson()

    private var textViewClient: TextView? = null
    private var textViewAddress: TextView? = null
    private var textViewDate: TextView? = null
    private var textViewTotal: TextView? = null
    private var textViewStatus: TextView? = null
    private var textViewNote: TextView? = null
    private var recyclerViewProducts: RecyclerView? = null
    private var buttonGoToMap: Button? = null

    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null
    var adapter: OrderProductsAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_orders_detail)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)



        textViewClient = findViewById(R.id.textview_client)
        textViewAddress = findViewById(R.id.textview_address)
        textViewDate = findViewById(R.id.textview_date)
        textViewTotal = findViewById(R.id.textview_total)
        textViewStatus = findViewById(R.id.textview_status)
        buttonGoToMap = findViewById(R.id.btn_go_to_map)
        textViewNote = findViewById(R.id.note)
        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        buttonBack = findViewById(R.id.button_back)
        toolbar?.title = ""
        titleBar?.text = "Đơn hàng #${order?.id}"

        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        recyclerViewProducts?.layoutManager = LinearLayoutManager(this)

        adapter = OrderProductsAdapter(this, order?.products!!)
        recyclerViewProducts?.adapter = adapter

        textViewClient?.text = "${order?.client?.firstname} ${order?.client?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewDate?.text = "${order?.timestamp}"
        when (order?.status) {
            "PAID" -> {
                textViewStatus?.text = "Đã thanh toán"
            }
            "DISPATCHED" -> {
                textViewStatus?.text = "Đã gửi đi"
            }
            "ON THE WAY" -> {
                textViewStatus?.text = "Đang giao hàng"
            }
            else -> {
                textViewStatus?.text = "Đã giao hàng"
            }
        }
        textViewNote?.text = order?.note
        Log.d(TAG, "Order: ${order.toString()}")

        getTotal()

        if (order?.status == "ON THE WAY") {
            buttonGoToMap?.visibility = View.VISIBLE
        }

        buttonGoToMap?.setOnClickListener { goToMap() }

    }

    private fun goToMap() {
        val i = Intent(this, ClientOrdersMapActivity::class.java)
        i.putExtra("order", order?.toJson())
        startActivity(i)
    }

    @SuppressLint("SetTextI18n")
    private fun getTotal() {
        var total = 0

        for (p in order?.products!!) {
            total += (p.price * p.quantity!!)
        }
        textViewTotal?.text = "$total VND"

    }
}