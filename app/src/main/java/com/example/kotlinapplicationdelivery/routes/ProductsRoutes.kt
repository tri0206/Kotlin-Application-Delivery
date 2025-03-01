package com.example.kotlinapplicationdelivery.routes

import com.example.kotlinapplicationdelivery.models.Category
import com.example.kotlinapplicationdelivery.models.DiscountedProduct
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProductsRoutes {

    @GET("products/findByCategory/{id_category}")
    fun findByCategory(
        @Path("id_category") idCategory: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Product>>

    @GET("products/findByRestaurant/{id_restaurant}")
    fun findByRestaurant(
        @Path("id_restaurant") idRestaurant: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Product>>

    @GET("products/findByCategoryOrName/{keyword}")
    fun findByCategoryOrName(
        @Path("keyword") keyword: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Product>>

    @GET("products/findByQuery/{query}/{idRestaurant}")
    fun findByQuery(
        @Path("query") query: String,
        @Path("idRestaurant") idRestaurant: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Product>>

    @GET("products/getDiscountedProducts")
    fun getDiscountedProducts(
        @Header("Authorization") token: String
    ): Call<ArrayList<DiscountedProduct>>

    @Multipart
    @POST("products/create")
    fun create(
        @Part images: Array<MultipartBody.Part?>,
        @Part("product") product: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @Multipart
    @POST("products/update")
    fun update(
        @Part images: Array<MultipartBody.Part?>,
        @Part("product") product: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>
}