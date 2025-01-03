package com.example.kotlinapplicationdelivery.providers

import android.util.Log
import com.example.kotlinapplicationdelivery.api.ApiRoutes
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.Restaurant
import com.example.kotlinapplicationdelivery.routes.ProductsRoutes
import com.example.kotlinapplicationdelivery.routes.RestaurantsRoutes
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class RestaurantsProvider(private val token: String) {
    private var restaurantsRoutes: RestaurantsRoutes? = null

    init {
        val api = ApiRoutes()
        restaurantsRoutes = api.getRestaurantRoutes(token)
    }

    fun create(restaurant: Restaurant, file: File): Call<ResponseHttp>? {
        Log.e("tridoan", "createRestaurantInfo: 2", )
        val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("image", file.name, reqFile)

        // Tạo request body cho product
        val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), restaurant.toJson())

        // Gửi request với file và thông tin sản phẩm
        return restaurantsRoutes?.create(imagePart, requestBody, token)
    }
}