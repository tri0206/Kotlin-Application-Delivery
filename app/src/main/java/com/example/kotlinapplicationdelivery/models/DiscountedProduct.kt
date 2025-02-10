package com.example.kotlinapplicationdelivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class DiscountedProduct(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("original_price") val originalPrice: String? = null,
    @SerializedName("discounted_price") val discountedPrice: String? = null,
    @SerializedName("id_restaurant") val idRestaurant: String? = null,
)
