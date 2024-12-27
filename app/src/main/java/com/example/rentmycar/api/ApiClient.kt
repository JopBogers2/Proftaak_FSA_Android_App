package com.example.rentmycar.api

import com.example.rentmycar.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun createOkHttpClient(tokenProvider: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(JwtInterceptor(tokenProvider))
            .build()
    }

    fun createApiService(tokenProvider: String?): ApiService {
        val client = createOkHttpClient(tokenProvider)
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}