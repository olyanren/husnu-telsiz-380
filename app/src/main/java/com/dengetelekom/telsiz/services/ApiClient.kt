package com.dengetelekom.telsiz.services

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.dengetelekom.telsiz.retrofitcoroutines.remote.NetworkResponseAdapterFactory

import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {
    fun getClient(baseUrl: String?): Retrofit {
        return getClient(baseUrl, null, null)
    }

    fun getClientBasic(baseUrl: String?, accessToken: String?): Retrofit = getClient(
        baseUrl,
        accessToken,
        "Basic"
    )

    fun getClientBearer(baseUrl: String?, accessToken: String?): Retrofit = getClient(
        baseUrl,
        accessToken,
        "Bearer"
    )

    private fun getClient(
        baseUrl: String?,
        accessToken: String?,
        authenticationType: String?
    ): Retrofit {
        val accessTokenParam = if (authenticationType == null || authenticationType == "Bearer")
            "Bearer $accessToken"
        else "Basic $accessToken"
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val ongoing = chain.request().newBuilder()
                ongoing.addHeader("Accept", "application/json;versions=1")
                if (authenticationType != null) {
                    ongoing.addHeader("Authorization", accessTokenParam)
                }
                chain.proceed(ongoing.build())
            }

            .build()
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()
        return Retrofit.Builder()
            .baseUrl(baseUrl!!)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }


}