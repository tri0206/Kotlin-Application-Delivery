package com.example.kotlinapplicationdelivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serial

class User(
    @SerializedName("id") val id: String? = null,
    @SerializedName("firstname") var firstname: String,
    @SerializedName("lastname") var lastname: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") var phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("image") val image: String? = null,
    @SerializedName("session_token") val sessionToken: String? = null,
    @SerializedName("is_available") val isAvailable: Boolean? = null,
    @SerializedName("roles") val roles: ArrayList<Rol>? = null
    ) {
    override fun toString(): String {
        return "User(id='$id', firstname='$firstname', lastname='$lastname', email='$email', phone='$phone', password='$password', image='$image', sessionToken='$sessionToken', isAvailable=$isAvailable, roles=$roles)"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}