package com.example.kotlinapplicationdelivery.activities.restaurant.product

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.adapters.AddressAdapter
import com.example.kotlinapplicationdelivery.adapters.DiscountAdapter
import com.example.kotlinapplicationdelivery.models.Address
import com.example.kotlinapplicationdelivery.models.Discount
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.providers.DiscountProvider
import com.example.kotlinapplicationdelivery.providers.ProductsProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RestaurantProductDiscountActivity : AppCompatActivity() {

    private var productsProvider: ProductsProvider? = null
    private var discountsProvider: DiscountProvider? = null
    private var btnAdd: FloatingActionButton? = null
    var adapter: DiscountAdapter? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var product: Product? = null
    val gson = Gson()
    var discounts = ArrayList<Discount>()
    private var recyclerViewDiscounts: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_product_discount)
        btnAdd = findViewById(R.id.btnAddDiscount)
        sharedPref = SharedPref(this)
        product = gson.fromJson(intent.getStringExtra("product"), Product::class.java)
        getUserFromSession()
        productsProvider = ProductsProvider(user?.sessionToken!!)
        discountsProvider = DiscountProvider(user?.sessionToken!!)
        recyclerViewDiscounts = findViewById(R.id.recyclerViewDiscounts)

        getDiscount()
        btnAdd?.setOnClickListener {
            if(adapter?.itemCount!! >= 1) {
                Toast.makeText(this, "Đã có giảm giá cho sản phẩm này!", Toast.LENGTH_SHORT).show()
            }
            else {
                showAddDiscountDialog()
            }
        }
    }

    private fun getUserFromSession() {
        val gson = Gson()
        if (!sharedPref?.getData("user").isNullOrBlank()) {
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun createDiscount(discount: Discount) {
            discountsProvider?.createDiscount(discount)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    if (response.body() != null) {
                        Toast.makeText(this@RestaurantProductDiscountActivity, "Thêm giảm giá thành công!", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this@RestaurantProductDiscountActivity, "An error occurred in the request", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(this@RestaurantProductDiscountActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun getDiscount() {
        discountsProvider?.getDiscount(product?.id!!)?.enqueue(object: Callback<ArrayList<Discount>> {
            override fun onResponse(call: Call<ArrayList<Discount>>, response: Response<ArrayList<Discount>>) {
                if (response.body() != null) {
                    discounts = response.body()!!
                    Log.e("TAG", "onResponse: $discounts")
                    adapter = DiscountAdapter(this@RestaurantProductDiscountActivity, discounts,
                        discountsProvider!!
                    )
                    recyclerViewDiscounts?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Discount>>, t: Throwable) {
                Toast.makeText(this@RestaurantProductDiscountActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showAddDiscountDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_discount, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val spinnerDiscountType = dialogView.findViewById<Spinner>(R.id.discountType)
        val paymentMethods = listOf("Giá trị", "Phần trăm")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDiscountType?.adapter = adapter
        val etDiscountValue = dialogView.findViewById<EditText>(R.id.etDiscountValue)
        val tvStartDateTime = dialogView.findViewById<TextView>(R.id.tvStartDateTime)
        val tvEndDateTime = dialogView.findViewById<TextView>(R.id.tvEndDateTime)
        val btnSaveDiscount = dialogView.findViewById<Button>(R.id.btnSaveDiscount)
        var selectedDiscountType: String = "Giá trị"
        tvStartDateTime.setOnClickListener {
            showDateTimePicker(tvStartDateTime)
        }

        tvEndDateTime.setOnClickListener {
            showDateTimePicker(tvEndDateTime)
        }

        spinnerDiscountType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDiscountType = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val dialog = builder.create()

        btnSaveDiscount.setOnClickListener {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            val startDate = sdf.parse(tvStartDateTime.text.toString())
            val endDate = sdf.parse(tvEndDateTime.text.toString())
            var discountType: String? = null
            discountType = if(selectedDiscountType == "Phần trăm") {
                "percentage"
            } else {
                "fixed"
            }
            if (startDate != null && endDate != null) {
                val discount = Discount(
                    idProduct = product?.id!!,
                    discountType = discountType,
                    value = etDiscountValue.text.toString(),
                    startDate = startDate.toString(),
                    endDate = endDate.toString()
                )
                Log.e("manh.tri", "showAddDiscountDialog: $discount", )
                if (endDate.before(startDate)) {
                    Toast.makeText(this, "Ngày kết thúc phải sau ngày bắt đầu!", Toast.LENGTH_SHORT).show()
                } else {
                    if(!isValidForm(etDiscountValue.toString(), tvStartDateTime.text.toString(),
                            tvEndDateTime.text.toString()
                        )) {
                        Toast.makeText(this, "Hãy điền đủ thông tin!", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        createDiscount(discount)
                        dialog.dismiss()
                        onResume()
                    }
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn ngày giờ hợp lệ!", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun isValidForm(value : String, startDate : String, endDate : String) : Boolean {
        if(value.isBlank()) {
            return false
        }
        if(startDate.isBlank()) {
            return false;
        }
        if(endDate.isBlank()) {
            return false
        }
        return true;
    }
    private fun showDateTimePicker(textView: TextView) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                textView.text = sdf.format(calendar.time)

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    override fun onResume() {
        super.onResume()
        getDiscount()
    }
}