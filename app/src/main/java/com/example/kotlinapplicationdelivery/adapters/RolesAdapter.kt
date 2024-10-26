package com.example.kotlinapplicationdelivery.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.client.home.ClientHomeActivity
import com.example.kotlinapplicationdelivery.activities.delivery.home.DeliveryHomeActivity
import com.example.kotlinapplicationdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.kotlinapplicationdelivery.models.Rol
import com.example.kotlinapplicationdelivery.utils.SharedPref


class RolesAdapter(private val context: Activity, private val roles: ArrayList<Rol>): RecyclerView.Adapter<RolesAdapter.RolesViewHolder>() {

    private val sharedPref = SharedPref(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_roles, parent, false)
        return RolesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return roles.size
    }

    override fun onBindViewHolder(holder: RolesViewHolder, position: Int) {

        val rol = roles[position]

        holder.textViewRol.text = rol.name
        Glide.with(context).load(rol.image).into(holder.imageViewRol)

        holder.itemView.setOnClickListener { goToRol(rol) }
    }

    private fun goToRol(rol: Rol) {
        when (rol.name) {
            "RESTAURANTE" -> {

                sharedPref.save("rol", "RESTAURANTE")

                val i = Intent(context, RestaurantHomeActivity::class.java)
                context.startActivity(i)
            }
            "CLIENTE" -> {
                sharedPref.save("rol", "CLIENTE")

                val i = Intent(context, ClientHomeActivity::class.java)
                context.startActivity(i)
            }
            "REPARTIDOR" -> {

                sharedPref.save("rol", "REPARTIDOR")

                val i = Intent(context, DeliveryHomeActivity::class.java)
                context.startActivity(i)
            }
        }
    }

    class RolesViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val textViewRol: TextView = view.findViewById(R.id.textview_rol)
        val imageViewRol: ImageView = view.findViewById(R.id.imageview_rol)

    }

}