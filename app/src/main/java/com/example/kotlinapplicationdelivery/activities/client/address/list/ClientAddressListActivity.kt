package com.example.kotlinapplicationdelivery.activities.client.address.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
    private var toolbar: Toolbar? = null

    private var recyclerViewAddress: RecyclerView? = null

    private var buttonNext: Button? = null
    var adapter: AddressAdapter? = null
    private var addressProvider: AddressProvider? = null
    private var ordersProvider: OrdersProvider? = null
    var sharedPref: SharedPref? = null
    var user: User? = null

    var address = ArrayList<Address>()

    val gson = Gson()

    private var selectedProducts = ArrayList<Product>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_address_list)

        sharedPref = SharedPref(this)

        getProductsFromSharedPref()

        fabCreateAddress = findViewById(R.id.fab_address_create)

        toolbar = findViewById(R.id.toolbar)
         buttonNext = findViewById(R.id.btn_next)
        recyclerViewAddress = findViewById(R.id.recyclerview_address)

        recyclerViewAddress?.layoutManager = LinearLayoutManager(this)

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "My addresses"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserFromSession()

        addressProvider = AddressProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        fabCreateAddress?.setOnClickListener { goToAddressCreate() }

        getAddress()

        buttonNext?.setOnClickListener { getAddressFromSession() }
    }

    private fun getProductsFromSharedPref() {

        if (!sharedPref?.getData("order").isNullOrBlank()) {
            val type = object: TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type)

        }

    }

    private fun createOrder(idAddress: String) {
        goToPaymentsForm()

        val order = Order(
            products = selectedProducts,
            idClient = user?.id!!,
            idAddress = idAddress
        )

        ordersProvider?.create(order)?.enqueue(object: Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                if (response.body() != null) {
                    Toast.makeText(this@ClientAddressListActivity, "${response.body()?.message}", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this@ClientAddressListActivity, "An error occurred in the request", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@ClientAddressListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getAddressFromSession() {

        if (!sharedPref?.getData("address").isNullOrBlank()) {
            val a = gson.fromJson(sharedPref?.getData("address"), Address::class.java) // IF IT EXISTS
            createOrder(a.id!!)
            goToPaymentsForm()
        }
        else {
            Toast.makeText(this, "Select an address to continue", Toast.LENGTH_LONG).show()
        }

    }

    private fun goToPaymentsForm() {
        val i = Intent(this, ClientPaymentMethodActivity::class.java)
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