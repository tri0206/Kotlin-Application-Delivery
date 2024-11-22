package com.example.kotlinapplicationdelivery.api

import com.example.kotlinapplicationdelivery.routes.AddressRoutes
import com.example.kotlinapplicationdelivery.routes.CategoriesRoutes
import com.example.kotlinapplicationdelivery.routes.OrdersRoutes
import com.example.kotlinapplicationdelivery.routes.ProductsRoutes
import com.example.kotlinapplicationdelivery.routes.UsersRoutes
import retrofit2.Retrofit

class ApiRoutes {
    private val API_URL = "http://192.168.1.101:3000/api/"
    private val retrofit = RetrofitClient()
    fun getUsersRoutes(): UsersRoutes {
        return retrofit.getClient(API_URL).create(UsersRoutes::class.java)
    }
    fun getUsersRoutesWithToken(token: String): UsersRoutes {
        return retrofit.getClientWithToken(API_URL, token).create(UsersRoutes::class.java)
    }
    fun getCategoriesRoutes(token: String): CategoriesRoutes {
        return retrofit.getClientWithToken(API_URL, token).create(CategoriesRoutes::class.java)
    }
    fun getProductsRoutes(token: String): ProductsRoutes {
        return retrofit.getClientWithToken(API_URL, token).create(ProductsRoutes::class.java)
    }
    fun getAddressRoutes(token: String): AddressRoutes {
        return retrofit.getClientWithToken(API_URL, token).create(AddressRoutes::class.java)
    }
    fun getOrdersRoutes(token: String): OrdersRoutes {
        return retrofit.getClientWithToken(API_URL, token).create(OrdersRoutes::class.java)
    }

}