package com.example.kotlinapplicationdelivery.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinapplicationdelivery.R
import com.example.kotlinapplicationdelivery.adapters.FoodItemAdapter.FoodItemViewHolder
import com.example.kotlinapplicationdelivery.models.Discount
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.providers.DiscountProvider
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscountAdapter(val context: Activity, val discounts: ArrayList<Discount>, val discountsProvider: DiscountProvider): RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder>() {
    var sharedPref = SharedPref(context)
    val gson = Gson()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_discount, parent, false)
        return DiscountViewHolder(view)
    }
    override fun getItemCount(): Int {
        return discounts.size
    }

    override fun onBindViewHolder(
        holder: DiscountAdapter.DiscountViewHolder,
        position: Int
    ) {
        val discount = discounts[position]
        Log.e("manh.tri", "onBindViewHolder: $discount", )
        sharedPref = SharedPref(context)

        if(discount.discountType == "fixed") {
            holder.textViewType.text = "Giảm theo giá trị"
            holder.textViewValue.text = "-${discount.value}đ"
        }
        else {
            holder.textViewType.text = "phần trăm"
            holder.textViewValue.text = "-${discount.value}%"
        }
        holder.textViewStartDate.text = discount.startDate
        holder.textViewEndDate.text = discount.endDate

        holder.btnDelete.setOnClickListener {
            deleteDiscountFromDatabase(discount.id!!, position)
        }
    }
    class DiscountViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewType: TextView = view.findViewById(R.id.tvDiscountType)
        val textViewValue: TextView = view.findViewById(R.id.tvDiscountValue)
        val textViewStartDate: TextView = view.findViewById(R.id.tvStartDate)
        val textViewEndDate: TextView = view.findViewById(R.id.tvEndDate)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteDiscount)
    }

    private fun deleteDiscountFromDatabase(discountId: String, position: Int) {
        discountsProvider.deleteDiscount(discountId)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.isSuccessful) {
                    discounts.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, discounts.size)
                    Toast.makeText(context, "Xóa giảm giá thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lỗi khi xóa giảm giá!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}