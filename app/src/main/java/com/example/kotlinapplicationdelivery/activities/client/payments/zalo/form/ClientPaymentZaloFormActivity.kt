package com.example.kotlinapplicationdelivery.activities.client.payments.zalo.form

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinapplicationdelivery.R

class ClientPaymentZaloFormActivity : AppCompatActivity() {

    private var btnZaloPay : Button? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_payment_zalo_form)
    }

    fun payZalo() {

    }
}