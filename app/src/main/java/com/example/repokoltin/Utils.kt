package com.example.repokoltin

import android.util.Log
import com.example.repokoltin.model.Repo
import com.google.gson.JsonElement

class Utils {
    companion object {
        val baseUrl = "https://api.github.com/search/"
        val NAME = "LOGIN"
        val KEY_LOGIN = "KEY"
        val KEY = "KEY"


        fun convertJsonToList(json : JsonElement) : MutableList<Repo>{
            val list: MutableList<Repo> = ArrayList()
            val jsonArray = json.asJsonObject["items"].asJsonArray
            Log.e("TAG", "json $json" )
            for (i in 0 until jsonArray.size()) {
                var fullName = "Unknow"
                var des = "Unknow"
                var stars = "Unknow"
                var forks = "Unknow"
                var lang = "Unknow"

                try {
                    fullName = jsonArray[i].asJsonObject["name"].asString
                } catch (e: Exception) {
                    Log.e("TAG", "Exception $e")
                }
                try {
                    des = jsonArray[i].asJsonObject["description"].asString
                } catch (e: Exception) {
                    Log.e("TAG", "Exception $e")
                }
                try {
                    stars = jsonArray[i].asJsonObject["stargazers_count"].asString
                } catch (e: Exception) {
                    Log.e("TAG", "Exception $e")
                }
                try {
                    forks = jsonArray[i].asJsonObject["forks_count"].asString
                } catch (e: Exception) {
                    Log.e("TAG", "Exception $e")
                }
                var language: String? = "Unknown"
                try {
                    language = jsonArray[i].asJsonObject["language"].asString
                } catch (e: Exception) {
                    Log.e("TAG", "Exception $e")
                }
                val repo = Repo(fullName, des, stars, forks, language!!)
                list.add(repo)
                Log.e("TAG", "list size ${list.size}")

            }
            return list
        }
    }
}