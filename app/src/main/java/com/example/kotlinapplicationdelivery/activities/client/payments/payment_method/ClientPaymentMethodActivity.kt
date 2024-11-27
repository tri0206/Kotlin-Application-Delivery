package com.example.kotlinapplicationdelivery.activities.client.payments.payment_method

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.ClientPaymentMomoFormActivity
import com.example.kotlinapplicationdelivery.activities.client.payments.paypal.form.ClientPaymentsPaypalFormActivity

class ClientPaymentMethodActivity : AppCompatActivity() {

    private var imageViewMomo: ImageView? = null
    private var imageViewPaypal: ImageView? = null
    private var imageViewCash : ImageView? = null
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_payment_method)

        imageViewMomo = findViewById(R.id.imageview_momo)
        imageViewPaypal = findViewById(R.id.imageview_paypal)
        toolbar = findViewById(R.id.toolbar)

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Payment methods"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageViewMomo?.setOnClickListener { goToMomo() }
        imageViewPaypal?.setOnClickListener { goToPaypal() }
    }

    private fun goToMomo(){
        val i = Intent(this, ClientPaymentMomoFormActivity::class.java)
        startActivity(i)
    }

    private fun goToPaypal(){
        val i = Intent(this, ClientPaymentsPaypalFormActivity::class.java)
        startActivity(i)
    }
}