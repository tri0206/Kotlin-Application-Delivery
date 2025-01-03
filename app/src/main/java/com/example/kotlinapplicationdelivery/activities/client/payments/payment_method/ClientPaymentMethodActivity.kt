package com.example.kotlinapplicationdelivery.activities.client.payments.payment_method

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.ClientPaymentZaloFormActivity

class ClientPaymentMethodActivity : AppCompatActivity() {

    private var imageViewZalo: ImageView? = null
    private var imageViewCash : ImageView? = null
    private var toolbar: Toolbar? = null
    private var titleBar : TextView? = null
    private var buttonBack : ImageView?= null
    private var textViewCash: TextView? = null
    private var textViewZalo: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_payment_method)

        imageViewCash = findViewById(R.id.imageview_cash)
        imageViewZalo = findViewById(R.id.imageview_zalo)

        toolbar = findViewById(R.id.toolbar)
        titleBar = findViewById(R.id.custom_toolbar_title)
        buttonBack = findViewById(R.id.button_back)
        toolbar?.title = ""
        titleBar?.text = "Địa chỉ"
        textViewCash = findViewById(R.id.textview_cash)
        textViewZalo = findViewById(R.id.textview_zalo)
        imageViewZalo?.setOnClickListener { goToZaloForm() }
        textViewZalo?.setOnClickListener { goToZaloForm() }
        imageViewCash?.setOnClickListener {  }
        textViewCash?.setOnClickListener {  }
        buttonBack?.setOnClickListener {
            finish()
        }

    }

    private fun goToZaloForm(){
        val i = Intent(this, ClientPaymentZaloFormActivity::class.java)
        i.putExtra("total_price", intent.getStringExtra("total_price"))
        i.putExtra("note", intent.getStringExtra("note"))
        startActivity(i)
    }

}