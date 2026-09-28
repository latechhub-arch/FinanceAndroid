package com.latechhub.finance.data.remote

import android.content.Context
import com.latechhub.finance.data.local.TokenStorage
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {

    private const val BASE_URL = "http://127.0.0.1:5000/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit: Retrofit

    private val refreshRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .build()

    val refreshApi: RefreshApi =
        refreshRetrofit.create(RefreshApi::class.java)

    fun initialize(context: Context) {
        val tokenStorage = TokenStorage(context.applicationContext)

        okHttpClient = OkHttpClient.Builder()
            .authenticator(
                AuthAuthenticator(tokenStorage)
            )
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
    }

    val authApi: AuthApi
        get() = retrofit.create(AuthApi::class.java)
}
