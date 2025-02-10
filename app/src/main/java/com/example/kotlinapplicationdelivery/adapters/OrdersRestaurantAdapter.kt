package com.example.kotlinapplicationdelivery.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.activities.restaurant.orders.detail.RestaurantOrdersDetailActivity
import com.example.kotlinapplicationdelivery.models.Order

class OrdersRestaurantAdapter(val context: Activity, val orders: ArrayList<Order>): RecyclerView.Adapter<OrdersRestaurantAdapter.OrdersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_orders_restaurant, parent, false)
        return OrdersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = orders[position]
        holder.textViewOrderId.text = "Đơn hàng #${order.id}"
        holder.textViewDate.text = "${order.timestamp}"
        holder.textViewAddress.text = order.address?.address + ", " + order.address?.neighborhood
        holder.textViewClient.text = order.client?.firstname + " " + order.client?.lastname
        holder.itemView.setOnClickListener { goToOrderDetail(order) }
    }

    private fun goToOrderDetail(order: Order) {
        val i = Intent(context, RestaurantOrdersDetailActivity::class.java)
        i.putExtra("order", order.toJson())
        context.startActivity(i)
    }

    class OrdersViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewOrderId: TextView = view.findViewById(R.id.textview_order_id)
        val textViewDate: TextView = view.findViewById(R.id.textview_date)
        val textViewAddress: TextView = view.findViewById(R.id.textview_address)
        val textViewClient: TextView = view.findViewById(R.id.textview_client)

    }
}