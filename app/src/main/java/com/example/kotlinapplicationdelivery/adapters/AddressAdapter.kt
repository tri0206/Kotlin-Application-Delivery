package com.example.kotlinapplicationdelivery.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.address.list.ClientAddressListActivity
import com.example.kotlinapplicationdelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.kotlinapplicationdelivery.models.Address
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson

class AddressAdapter(val context: Activity, val address: ArrayList<Address>): RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    val sharedPref = SharedPref(context)
    val gson = Gson()
    private var prev = 0
    private var positionAddressSession = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun getItemCount(): Int {
        return address.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val a = address[position] // EACH OF THE DIRECTIONS
        if (!sharedPref.getData("address").isNullOrBlank()) { // IF THE USER CHOSE AN ADDRESS FROM THE LIST
            val adr = gson.fromJson(sharedPref.getData("address"), Address::class.java)

            if (adr.id == a.id) {
                positionAddressSession = position
                holder.imageViewCheck.visibility = View.VISIBLE
            }
        }

        holder.textViewAddress.text = a.address
        holder.textViewNeighborhood.text = a.neighborhood

        holder.itemView.setOnClickListener {
            (context as ClientAddressListActivity).resetValue(prev)
            context.resetValue(positionAddressSession)
            prev = position // 1

            holder.imageViewCheck.visibility = View.VISIBLE
            saveAddress(a.toJson())
        }
    }

    private fun saveAddress(data: String) {
        val ad = gson.fromJson(data, Address::class.java)
        sharedPref.save("address", ad)
    }

    class AddressViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewAddress: TextView = view.findViewById(R.id.textview_address)
        val textViewNeighborhood: TextView = view.findViewById(R.id.textview_neighborhood)
        val imageViewCheck: ImageView = view.findViewById(R.id.imageview_check)
    }
}