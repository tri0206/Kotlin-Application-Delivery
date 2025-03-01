package com.example.kotlinapplicationdelivery.routes

import com.example.kotlinapplicationdelivery.models.Address
import com.example.kotlinapplicationdelivery.models.Discount
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface DiscountRoutes {
    @GET("discount/findByProduct/{product_id}")
    fun getDiscount(
        @Path("product_id") idProduct: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Discount>>

    @POST("discount/create")
    fun create(
        @Body discount: Discount,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @DELETE("discount/deleteDiscount/{id}")
    fun delete(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>
}