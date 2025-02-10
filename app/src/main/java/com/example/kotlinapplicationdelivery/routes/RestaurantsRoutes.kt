package com.example.kotlinapplicationdelivery.routes

import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.Restaurant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RestaurantsRoutes {

    @Multipart
    @POST("restaurants/create")
    fun create(
        @Part image: MultipartBody.Part,
        @Part("restaurant") restaurant: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @FormUrlEncoded
    @POST("restaurants/updateStatus")
    fun updateStatus(@Field("id") id: String, @Field("status") status: String): Call<ResponseHttp>

    @FormUrlEncoded
    @POST("restaurants/findByUser")
    fun findByUser(@Field("idUser") idUser: String): Call<ResponseHttp>

    @GET("restaurants/findByCategory/{idCategory}")
    fun findByCategory(
        @Path("idCategory") idCategory: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Restaurant>>

    @GET("restaurants/findByQuery/{query}")
    fun findByQuery(
        @Path("query") query: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Restaurant>>

    @FormUrlEncoded
    @POST("restaurants/findById")
    fun findById(@Field("idRestaurant") idRestaurant: String): Call<ResponseHttp>
}