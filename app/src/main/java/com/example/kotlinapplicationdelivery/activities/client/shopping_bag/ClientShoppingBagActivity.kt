package com.example.kotlinapplicationdelivery.activities.client.shopping_bag

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.address.list.ClientAddressListActivity
import com.example.kotlinapplicationdelivery.adapters.ShoppingBagAdapter
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_shopping_bag)

        sharedPref = SharedPref(this)

        recyclerViewShoppingBag = findViewById(R.id.recyclerview_shopping_bag)
        textViewTotal = findViewById(R.id.textview_total)
        buttonNext = findViewById(R.id.btn_next)
        buttonNote = findViewById(R.id.btnNote)
        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        buttonBack = findViewById(R.id.button_back)
        toolbar?.title = ""
        titleBar?.text = "Đơn hàng của bạn"


        recyclerViewShoppingBag?.layoutManager = LinearLayoutManager(this)

        getProductsFromSharedPref()

        buttonNext?.setOnClickListener { goToAddressList() }
        buttonBack?.setOnClickListener {
            finish()
        }
        buttonNote?.setOnClickListener {showNoteDialog()}
    }

    private fun goToAddressList() {
        val i = Intent(this, ClientAddressListActivity::class.java)
        i.putExtra("total_price", textViewTotal?.text)
        i.putExtra("note", userNote)
        startActivity(i)
    }

    @SuppressLint("SetTextI18n")
    fun setTotal(total: Int) {
        textViewTotal?.text = "$total"
    }

    private fun getProductsFromSharedPref() {

        if (!sharedPref?.getData("order").isNullOrBlank()) { // THERE IS AN ORDER IN SHARED PREF
            val type = object: TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type)

            adapter = ShoppingBagAdapter(this, selectedProducts)
            recyclerViewShoppingBag?.adapter = adapter
        }

    }



    fun showNoteDialog() {

        val noteEditText = EditText(this)
        noteEditText.hint = "Nhập ghi chú của bạn"

        // Tạo AlertDialog
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

        // Hiển thị dialog
        builder.create().show()
    }

}