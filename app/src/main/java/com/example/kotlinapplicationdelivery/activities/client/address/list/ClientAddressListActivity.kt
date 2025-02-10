package com.example.kotlinapplicationdelivery.activities.client.address.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.address.create.ClientAddressCreateActivity
import com.example.kotlinapplicationdelivery.activities.client.payments.payment_method.ClientPaymentMethodActivity
import com.example.kotlinapplicationdelivery.adapters.AddressAdapter
import com.example.kotlinapplicationdelivery.models.Address
import com.example.kotlinapplicationdelivery.models.Order
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.AddressProvider
import com.example.kotlinapplicationdelivery.providers.OrdersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAddressListActivity : AppCompatActivity() {
    private var fabCreateAddress: FloatingActionButton? = null

    private var recyclerViewAddress: RecyclerView? = null

    //private var buttonNext: Button? = null
    var adapter: AddressAdapter? = null
    private var addressProvider: AddressProvider? = null
    private var ordersProvider: OrdersProvider? = null
    var sharedPref: SharedPref? = null
    var user: User? = null

    var address = ArrayList<Address>()

    val gson = Gson()

    private var selectedProducts = ArrayList<Product>()

    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_address_list)

        sharedPref = SharedPref(this)

        getProductsFromSharedPref()

        fabCreateAddress = findViewById(R.id.fab_address_create)
        //buttonNext = findViewById(R.id.btn_next)
        recyclerViewAddress = findViewById(R.id.recyclerview_address)

        recyclerViewAddress?.layoutManager = LinearLayoutManager(this)

        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        buttonBack = findViewById(R.id.button_back)
        toolbar?.title = ""
        titleBar?.text = "Địa chỉ"

        getUserFromSession()

        addressProvider = AddressProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        fabCreateAddress?.setOnClickListener { goToAddressCreate() }

        getAddress()

        //buttonNext?.setOnClickListener { goToPaymentsForm() }
        buttonBack?.setOnClickListener {
            finish()
        }
    }

    private fun getProductsFromSharedPref() {
        if (!sharedPref?.getData("order").isNullOrBlank()) {
            val type = object: TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type)
        }
    }


    private fun getAddressFromSession() {
        if (!sharedPref?.getData("address").isNullOrBlank()) {
            val a = gson.fromJson(sharedPref?.getData("address"), Address::class.java) // IF IT EXISTS
            //createOrder(a.id!!)
        }
        else {
            Toast.makeText(this, "Select an address to continue", Toast.LENGTH_LONG).show()
        }
    }

    private fun goToPaymentsForm() {
        val i = Intent(this, ClientPaymentMethodActivity::class.java)
        i.putExtra("total_price", intent.getStringExtra("total_price"))
        i.putExtra("note", intent.getStringExtra("note"))
        i.putExtra("id_restaurant", intent.getStringExtra("id_restaurant"))
        startActivity(i)
    }

    fun resetValue(position: Int) {
        val viewHolder = recyclerViewAddress?.findViewHolderForAdapterPosition(position) // ONE DIRECTION
        val view = viewHolder?.itemView
        val imageViewCheck = view?.findViewById<ImageView>(R.id.imageview_check)
        imageViewCheck?.visibility = View.GONE
    }

    private fun getAddress() {
        addressProvider?.getAddress(user?.id!!)?.enqueue(object: Callback<ArrayList<Address>> {
            override fun onResponse(call: Call<ArrayList<Address>>, response: Response<ArrayList<Address>>) {
                if (response.body() != null) {
                    address = response.body()!!
                    adapter = AddressAdapter(this@ClientAddressListActivity, address)
                    recyclerViewAddress?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Address>>, t: Throwable) {
                Toast.makeText(this@ClientAddressListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun goToAddressCreate() {
        val i = Intent(this, ClientAddressCreateActivity::class.java)
        startActivity(i)
    }
}