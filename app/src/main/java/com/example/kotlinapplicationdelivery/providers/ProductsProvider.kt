package com.example.kotlinapplicationdelivery.providers

import com.example.kotlinapplicationdelivery.api.ApiRoutes
import com.example.kotlinapplicationdelivery.models.Category
import com.example.kotlinapplicationdelivery.models.DiscountedProduct
import com.example.kotlinapplicationdelivery.models.Product
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.routes.ProductsRoutes
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class ProductsProvider(private val token: String) {
    private var productsRoutes: ProductsRoutes? = null

    init {
        val api = ApiRoutes()
        productsRoutes = api.getProductsRoutes(token)
    }

    fun findByCategory(idCategory: String): Call<ArrayList<Product>>? {
        return productsRoutes?.findByCategory(idCategory, token)
    }
    fun findByRestaurant(idRestaurant: String): Call<ArrayList<Product>>? {
        return productsRoutes?.findByRestaurant(idRestaurant, token)
    }
    fun findByCategoryOrName(keyword: String): Call<ArrayList<Product>>? {
        return productsRoutes?.findByCategoryOrName(keyword, token)
    }
    fun getDiscountedProducts(): Call<ArrayList<DiscountedProduct>>? {
        return productsRoutes?.getDiscountedProducts(token)
    }
    fun create(files: List<File>, product: Product): Call<ResponseHttp>? {
        val images = arrayOfNulls<MultipartBody.Part>(files.size)
        for (i in files.indices) {
            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), files[i])
            images[i] = MultipartBody.Part.createFormData("image", files[i].name, reqFile)
        }
        val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.toJson())
        return productsRoutes?.create(images, requestBody, token)
    }

    fun findByQuery(query: String, idRestaurant: String): Call<ArrayList<Product>>? {
        return productsRoutes?.findByQuery(query, idRestaurant, token)
    }
}