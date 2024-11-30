package com.example.rentmycar.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.rentmycar.BuildConfig

object RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // Use the value from BuildConfig
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

