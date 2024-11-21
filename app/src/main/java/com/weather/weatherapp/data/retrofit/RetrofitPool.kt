package com.weather.weatherapp.data.retrofit

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit


object RetrofitPool {

    fun getRetrofitInstance(baseUrl: String): ApiInterface {
        return Retrofit.Builder().apply {
            baseUrl(baseUrl)
            client(getOKHttpClient())
            addConverterFactory(GsonConverterFactory.create())
        }.build().create(ApiInterface::class.java)
    }


    private fun getOKHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor = makeLoggingInterceptor()): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                chain.proceed(request.build())
            }.connectTimeout(30, TimeUnit.SECONDS).readTimeout(50, TimeUnit.SECONDS).build()
    }

    private val httpLogger: HttpLoggingInterceptor.Logger by lazy {
        HttpLoggingInterceptor.Logger { message ->
            Timber.d(message)
        }
    }

    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor(httpLogger)
        loggingInterceptor.level =
            HttpLoggingInterceptor.Level.BODY //No need to check if debug, since using Timber
        return loggingInterceptor
    }

}