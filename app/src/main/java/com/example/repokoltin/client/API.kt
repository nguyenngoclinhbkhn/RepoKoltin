package com.example.repokoltin.client

import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("users?")
    fun getUser(@Query("q") userName: String): Single<JsonElement>

    @GET("repositories?")
    fun getRepo(@Query("q") nameRepo: String): Single<JsonElement>

    @GET("repositories?")
    fun getRepoStar(@Query("q") nameRepo: String,
                    @Query("sort") star: String,
                    @Query("order") desc: String): Single<JsonElement>

    @GET("repositories?")
    fun getRepoUpdate(@Query("q") nameRepo: String,
                    @Query("sort") update: String,
                    @Query("order") desc: String): Single<JsonElement>

    @GET("repositories?")
    fun getRepoUpdateVer2(@Query("q") nameRepo: String,
                      @Query("sort") update: String,
                      @Query("order") desc: String): Call<JsonElement>
}