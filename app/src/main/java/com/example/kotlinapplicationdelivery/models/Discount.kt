package com.example.kotlinapplicationdelivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Discount(
    @SerializedName("id") val id: String? = null,
    @SerializedName("product_id") val idProduct: String? = null,
    @SerializedName("name_product") val nameProduct: String?= null,
    @SerializedName("discount_type") val discountType: String,
    @SerializedName("value") val value: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
) {
    override fun toString(): String {
        return "Discount(id=$id, idProduct='$idProduct', discountType='$discountType', value='$value', nameProduct='$nameProduct', value=$value, startDate=$startDate, endDate=$endDate)"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}