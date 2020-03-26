package com.example.repokoltin.presenter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.repokoltin.Utils
import com.example.repokoltin.client.Client
import com.example.repokoltin.model.Repo
import com.example.repokoltin.model.RepoRealm
import com.example.repokoltin.view.main.MainView
import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.concurrent.TimeUnit

class PresenterMain(val mainView: MainView, val context: Context) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(Utils.NAME, Context.MODE_PRIVATE)
    }

    fun showRepoLocal() {
        val config = RealmConfiguration.Builder()
            .name("repo.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();
        val realm = Realm.getInstance(config)
        val result = realm?.where(RepoRealm::class.java)?.findAll();
        var list = arrayListOf<RepoRealm>()
        result?.forEach {
            val repoRealm = RepoRealm(it.id, it.fullname, it.des, it.star, it.fork, it.lang);
            list.add(repoRealm)
        }
        mainView.onShowRepoLocalSuccess(list)
    }

    fun showRepoSearch(nameRepo: String, sort: String, desc: String) {
//        var getRepo: Single<JsonElement> ?= null
        when (sort) {
            "star" ->
                Client.createAPI().getRepoStar(nameRepo.trim(), "star", "desc")
            "updated" ->
                Client.createAPI().getRepoUpdate("rx", "updated", "desc")
            else -> Client.createAPI().getRepo(nameRepo.trim())
        }.delay(1000, TimeUnit.MILLISECONDS)
            .map<List<Repo>> { jsonElement ->
                convertJsonToList(jsonElement)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getListRepoObservable())
    }

    fun logout() {
        val config = RealmConfiguration.Builder()
            .name("repo.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();
        val realm = Realm.getInstance(config)
        realm.executeTransaction{
            it.deleteAll()
        }
        sharedPreferences.edit().putInt(Utils.KEY_LOGIN, 0).commit()
        mainView.logoutSuccess()
    }

    fun stop() {
        compositeDisposable.dispose()
    }

    private fun convertJsonToList(json: JsonElement): MutableList<Repo> {
        val list: MutableList<Repo> = ArrayList()
        val jsonArray = json.asJsonObject["items"].asJsonArray
        Log.e("TAG", "json $json")
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

    private fun getListRepoObservable(): SingleObserver<List<Repo>> {
        return object : SingleObserver<List<Repo>> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onError(e: Throwable) {
                mainView.onShowRepoSearchError()
            }

            override fun onSuccess(t: List<Repo>) {
                mainView.onShowRepoSearchSuccess(t)
            }

        }
    }


}