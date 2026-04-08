package com.example.f_booking.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 là localhost của máy tính nhìn từ máy ảo Android
    // Chú ý: Nhớ đổi IP này khi deploy server thật!
    private const val BASE_URL = "http://10.0.2.2:5000/api/"

    // Biến lưu Token (Sẽ được cập nhật sau khi login thành công)
    var authToken: String? = null

    private val client = OkHttpClient.Builder().apply {
        // Log API để dễ debug
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        addInterceptor(logging)

        // Tự động gắn Token vào Header nếu có
        addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            authToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(requestBuilder.build())
        }
    }.build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}