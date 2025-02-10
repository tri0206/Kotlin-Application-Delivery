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


        val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), restaurant.toJson())

        return restaurantsRoutes?.create(imagePart, requestBody, token)
    }

    fun updateStatus(id: String, status: String): Call<ResponseHttp>? {
        return restaurantsRoutes?.updateStatus(id, status)
    }

    fun findByUser(idUser: String): Call<ResponseHttp>? {
        return restaurantsRoutes?.findByUser(idUser)
    }

    fun findByCategory(idCategory: String): Call<ArrayList<Restaurant>>? {
        return restaurantsRoutes?.findByCategory(idCategory, token)
    }
    fun findByQuery(query: String): Call<ArrayList<Restaurant>>? {
        return restaurantsRoutes?.findByQuery(query, token)
    }

    fun findById(idRestaurant: String): Call<ResponseHttp>? {
        Log.e("tridoan", "findById: 2", )
        return restaurantsRoutes?.findById(idRestaurant)
    }
}