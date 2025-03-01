package com.example.kotlinapplicationdelivery.providers

import com.example.kotlinapplicationdelivery.api.ApiRoutes
import com.example.kotlinapplicationdelivery.models.Address
import com.example.kotlinapplicationdelivery.models.Discount
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.routes.AddressRoutes
import com.example.kotlinapplicationdelivery.routes.DiscountRoutes
import retrofit2.Call

class DiscountProvider(val token: String) {
    private var discountRoutes: DiscountRoutes? = null

    init {
        val api = ApiRoutes()
        discountRoutes = api.getDiscountRoutes(token)
    }

    fun getDiscount(idProduct: String): Call<ArrayList<Discount>>? {
        return discountRoutes?.getDiscount(idProduct, token)
    }

    fun createDiscount(discount: Discount): Call<ResponseHttp>? {
        return discountRoutes?.create(discount, token)
    }

    fun deleteDiscount(id: String): Call<ResponseHttp>? {
        return discountRoutes?.delete(id, token)
    }
}