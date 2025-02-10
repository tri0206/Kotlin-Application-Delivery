package com.example.kotlinapplicationdelivery.fragments.restaurant

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.kotlinapplicationdelivery.R

class ProductDiscountedFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showChangePasswordDialog()
        return inflater.inflate(R.layout.fragment_product_discounted, container, false)
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_discount, null)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val dialog = builder.create()

        // Hiển thị dialog
        dialog.show()
    }
}