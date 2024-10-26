package com.example.kotlinapplicationdelivery.api

import com.example.kotlinapplicationdelivery.routes.UsersRoutes
import retrofit2.Retrofit

class ApiRoutes {
    private val API_URL = "http://192.168.1.101:3000/api/"
    private val retrofit = RetrofitClient()
    fun getUserRoutes(): UsersRoutes {
        return retrofit.getClient(API_URL).create(UsersRoutes::class.java)
    }
}