package com.example.kotlinapplicationdelivery.routes

import android.telecom.Call
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UsersRoutes {
    @POST("users/create")
    fun register(@Body user: User): retrofit2.Call<ResponseHttp>

    @FormUrlEncoded
    @POST("users/login")
    fun login(@Field("email") email: String, @Field("password") password: String): retrofit2.Call<ResponseHttp>


    @Multipart
    @PUT("users/update")
    fun update(
        @Part image: MultipartBody.Part,
        @Part("user") user: RequestBody
    ): retrofit2.Call<ResponseHttp>

    @PUT("users/updateWithoutImage")
    fun updateWithoutImage(
        @Body user: User
    ): retrofit2.Call<ResponseHttp>
}