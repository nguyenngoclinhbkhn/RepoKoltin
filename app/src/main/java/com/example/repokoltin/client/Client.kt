package com.example.repokoltin.client
import com.example.repokoltin.Utils
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.internal.Util
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class Client {
    companion object {
        private var retrofit: Retrofit ?= null
        private val client: OkHttpClient = OkHttpClient.Builder().build()

        private fun getRetrofit(): Retrofit{
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Utils.baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
            return retrofit!!
        }


        fun createAPI() : API{
            return getRetrofit().create(API::class.java)
        }
    }
}