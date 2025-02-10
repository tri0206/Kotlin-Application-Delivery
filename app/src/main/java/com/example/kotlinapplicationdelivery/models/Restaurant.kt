package com.example.kotlinapplicationdelivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Restaurant(
    @SerializedName("id_restaurant") val id: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("phone")  val phone: String? = null,
    @SerializedName("image")  val image: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("id_user") val idUser: String,
    @SerializedName("res_address") val res_address: String? = null,
    @SerializedName("res_neighborhood") val res_neighborhood: String? = null,
    @SerializedName("lat") val latitude: Double? = null,
    @SerializedName("lng") val longitude: Double? = null
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun toString(): String {
        return "Restaurant(id='$id', name='$name', description='$description', phone='$phone', image='$image', status='$status', idUser='$idUser', res_address='$res_address', res_neighborhood='$res_neighborhood', lat='$latitude', lng='$longitude')"
    }
}