package com.example.kotlinapplicationdelivery.routes

import com.example.kotlinapplicationdelivery.models.ResponseHttp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RestaurantsRoutes {

    @Multipart
    @POST("restaurants/create")
    fun create(
        @Part image: MultipartBody.Part,
        @Part("restaurant") restaurant: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>
}