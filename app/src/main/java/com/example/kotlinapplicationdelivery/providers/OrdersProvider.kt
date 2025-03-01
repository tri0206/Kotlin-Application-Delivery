package com.example.kotlinapplicationdelivery.providers

import com.example.kotlinapplicationdelivery.api.ApiRoutes
import com.example.kotlinapplicationdelivery.models.Order
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.routes.OrdersRoutes
import retrofit2.Call

class OrdersProvider(val token: String) {

    private var ordersRoutes: OrdersRoutes? = null

    init {
        val api = ApiRoutes()
        ordersRoutes = api.getOrdersRoutes(token)
    }

    fun getOrdersByStatus(status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByStatus(status, token)
    }

    fun getOrdersByClientAndStatus(idClient: String, status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByClientAndStatus(idClient, status, token)
    }

    fun getOrdersByRestaurantAndStatus(idRestaurant: String, status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByRestaurantAndStatus(idRestaurant, status, token)
    }

    fun getOrdersByDeliveryAndStatus(idDelivery:String, status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByDeliveryAndStatus(idDelivery, status, token)
    }

    fun create(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.create(order, token)
    }

    fun updateToDispatched(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateToDispatched(order, token)
    }

    fun updateToOnTheWay(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateToOnTheWay(order, token)
    }

    fun updateToDelivered(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateToDelivered(order, token)
    }

    fun updateLatLng(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateLatLng(order, token)
    }
}