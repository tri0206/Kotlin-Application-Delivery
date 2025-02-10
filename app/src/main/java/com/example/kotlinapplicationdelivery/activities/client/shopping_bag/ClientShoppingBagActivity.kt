package com.example.kotlinapplicationdelivery.activities.client.shopping_bag

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.address.list.ClientAddressListActivity
import com.example.kotlinapplicationdelivery.activities.client.home.ClientHomeActivity
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.ClientPaymentZaloFormActivity
import com.example.kotlinapplicationdelivery.adapters.ShoppingBagAdapter
import com.example.kotlinapplicationdelivery.models.Address
import com.example.kotlinapplicationdelivery.models.Order
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.OrdersProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ClientShoppingBagActivity : AppCompatActivity() {
    private var recyclerViewShoppingBag: RecyclerView? = null
    private var textViewTotal: TextView? = null
    private var buttonNext: Button? = null
    private var buttonNote: Button? = null
    private var adapter: ShoppingBagAdapter? = null
    var sharedPref: SharedPref? = null
    var gson = Gson()
    private var selectedProducts = ArrayList<Product>()

    private var toolbar: Toolbar? = null
    private var titleBar: TextView? = null
    private var buttonBack: ImageView? = null
    private var userNote: String? = null
    private var addressLayout: LinearLayout? = null
    private var textviewAddress: TextView? = null
    private var textviewNeighborhood: TextView? = null
    private var ad: Address? = null
    private var spinnerPaymentMethod: Spinner? = null
    private var selectedPaymentMethod: String = "Tiền mặt"

    private var ordersProvider: OrdersProvider? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_shopping_bag)

        sharedPref = SharedPref(this)
        val gson = Gson()
        getUserFromSession()
        ordersProvider = OrdersProvider(user?.sessionToken!!)
        recyclerViewShoppingBag = findViewById(R.id.recyclerview_shopping_bag)
        textViewTotal = findViewById(R.id.textview_total)
        buttonNext = findViewById(R.id.btn_next)
        buttonNote = findViewById(R.id.btnNote)
        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        addressLayout = findViewById(R.id.layout_address)
        buttonBack = findViewById(R.id.button_back)
        textviewAddress = findViewById(R.id.textview_address)
        textviewNeighborhood = findViewById(R.id.textview_neighborhood)
        toolbar?.title = ""
        titleBar?.text = "Đơn hàng của bạn"
        spinnerPaymentMethod = findViewById(R.id.spinner_payment_method)
        setAddress()
        setPaymentMethod()
        recyclerViewShoppingBag?.layoutManager = LinearLayoutManager(this)
        spinnerPaymentMethod?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPaymentMethod = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        getProductsFromSharedPref()
        buttonNext?.setOnClickListener { showConfirmationDialog() }
        buttonBack?.setOnClickListener {
            finish()
        }
        buttonNote?.setOnClickListener {showNoteDialog()}
        addressLayout?.setOnClickListener {goToAddressList()}
    }

    private fun goToAddressList() {
        val i = Intent(this, ClientAddressListActivity::class.java)
        startActivity(i)
    }

    @SuppressLint("SetTextI18n")
    fun setTotal(total: Int) {
        textViewTotal?.text = "$total"
    }

    private fun getProductsFromSharedPref() {
        if (!sharedPref?.getData("order").isNullOrBlank()) {
            val type = object: TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type)
            adapter = ShoppingBagAdapter(this, selectedProducts)
            recyclerViewShoppingBag?.adapter = adapter
        }
    }

    private fun showNoteDialog() {
        val noteEditText = EditText(this)
        noteEditText.hint = "Nhập ghi chú của bạn"

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Nhập ghi chú")
            .setMessage("Vui lòng nhập ghi chú cho nhà hàng.")
            .setView(noteEditText)
            .setPositiveButton("Lưu", DialogInterface.OnClickListener { _, _ ->

                val note = noteEditText.text.toString()
                if (note.isNotEmpty()) {
                    userNote = note
                    Toast.makeText(applicationContext, "Ghi chú đã lưu: $note", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(applicationContext, "Vui lòng nhập ghi chú!", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .setNegativeButton(
                "Hủy",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })

        builder.create().show()
    }
    private fun setAddress() {
        if(!sharedPref?.getData("address").isNullOrBlank()) {
            ad = gson.fromJson(sharedPref?.getData("address"), Address::class.java)
        }
        textviewAddress?.text = ad!!.address
        textviewNeighborhood?.text = ad!!.neighborhood
    }

    override fun onResume() {
        super.onResume()
        setAddress()
    }
    private fun setPaymentMethod() {
        val paymentMethods = listOf("Tiền mặt", "ZaloPay")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod?.adapter = adapter
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận đặt hàng")
        builder.setMessage("Bạn có chắc muốn đặt hàng không?")

        builder.setPositiveButton("Có") { _, _ ->
            navigateToPayment()
        }

        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun navigateToPayment() {
        when (selectedPaymentMethod) {
            "Tiền mặt" -> {
                createOrder()
                Toast.makeText(this, "Đã chọn thanh toán Tiền mặt", Toast.LENGTH_SHORT).show()
            }
            "ZaloPay" -> {
                goToZaloPayForm()
            }
            else -> {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createOrder() {
        val order = Order(
            products = selectedProducts,
            idClient = user?.id!!,
            idAddress = ad?.id!!,
            idRestaurant = selectedProducts[0].idRestaurant!!,
            payment = "Thanh toán khi nhận hàng",
            note = userNote,
        )

        Log.e("tridoan", "createOrder: $order" )
        ordersProvider?.create(order)?.enqueue(object: Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {
                    showPaymentSuccessDialog()
                    sharedPref?.remove("order")
                    Toast.makeText(this@ClientShoppingBagActivity, "${response.body()?.message}", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this@ClientShoppingBagActivity, "An error occurred in the request", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@ClientShoppingBagActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun goToHomePage() {
        val intent = Intent(this, ClientHomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun goToZaloPayForm() {
        val intent = Intent(this, ClientPaymentZaloFormActivity::class.java)
        intent.putExtra("total_price", textViewTotal?.text)
        intent.putExtra("note", userNote)
        intent.putExtra("id_restaurant", selectedProducts[0].idRestaurant)
        startActivity(intent)
        finish()
    }

    private fun showPaymentSuccessDialog() {
        val dialog = Dialog(this).apply {
            setContentView(R.layout.dialog_payment_zalo_status)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        val buttonHome: Button = dialog.findViewById(R.id.buttonHome)
        buttonHome.setOnClickListener {
            dialog.dismiss()
            goToHomePage()
        }

        dialog.show()
    }

}