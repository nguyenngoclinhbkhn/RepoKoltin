package com.example.repokoltin.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repokoltin.R
import com.example.repokoltin.Utils
import com.example.repokoltin.adapter.AdapterRepoFavo
import com.example.repokoltin.adapter.AdapterRepoSearch
import com.example.repokoltin.client.Client
import com.example.repokoltin.model.Repo
import com.example.repokoltin.model.RepoRealm
import com.google.gson.JsonElement
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener,
    AdapterRepoSearch.OnRepoSearchListener, AdapterRepoFavo.OnRepoListener {
    var adapterRepoSearch: AdapterRepoSearch? = null
    var realm: Realm? = null
    var prefer: SharedPreferences? = null
    var adapterRepoFavo: AdapterRepoFavo? = null
    val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        prefer = getSharedPreferences(Utils.NAME, Context.MODE_PRIVATE)
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("repo.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();
        realm = Realm.getInstance(config)
//        getDataFromRealm2().observeOn(AndroidSchedulers.mainThread())
//            .subscribe(displayDataRealm())
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewFavo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setupRepoFavo()
        setupRepoSearch()

        txtCancel.setOnClickListener(this)
        txtLogout.setOnClickListener(this)
        txtMostRecent.setOnClickListener(this)
        txtMostPopular.setOnClickListener(this)


        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                txtLogout.visibility = View.GONE
                txtMostPopular.visibility = View.VISIBLE
                txtMostRecent.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                recyclerViewFavo.visibility = View.GONE
                val getRepo: Single<JsonElement> =
                    Client.createAPI().getRepo(edSearch.text.toString().trim { it <= ' ' })
                getRepo.map<List<Repo>> { jsonElement ->
                    convertJsonToList(jsonElement)
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getListRepo())
            }

            override fun afterTextChanged(s: Editable) {
                Log.e("TAG", "aflter")
            }
        })

    }
    private fun convertJsonToList(json : JsonElement) : MutableList<Repo>{
        val list: MutableList<Repo> = ArrayList()
        val jsonArray = json.asJsonObject["items"].asJsonArray
        for (i in 0 until jsonArray.size()) {
            val fullName = jsonArray[i].asJsonObject["name"].asString
            val description = jsonArray[i].asJsonObject["description"].asString
            val stars = jsonArray[i].asJsonObject["stargazers_count"].asString
            val forks = jsonArray[i].asJsonObject["forks_count"].asString
            var language: String? = "Unknown"
            try {
                language = jsonArray[i].asJsonObject["language"].asString
            } catch (e: Exception) {
                Log.e("TAG", "Exception $e")
            }
            val repo = Repo(fullName, description, stars, forks, language!!)
            list.add(repo)
        }
        return list
    }

    override fun onResume() {
        super.onResume()
        setListFavo()

    }


    private fun getListRepo(): SingleObserver<List<Repo>> {
        return object : SingleObserver<List<Repo>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onError(e: Throwable) {
                Log.e("TAG", "Error $e")
            }

            override fun onSuccess(t: List<Repo>) {
                recyclerView.setVisibility(View.VISIBLE)
                progressBar.setVisibility(View.GONE)
                adapterRepoSearch?.setList(t)
            }
        }
    }

    private fun getDataFromRealm2(): Single<List<RepoRealm>> {
        return object : Single<List<RepoRealm>>() {
            override fun subscribeActual(observer: SingleObserver<in List<RepoRealm>>) {
                val config = RealmConfiguration.Builder()
                    .name("repo.db")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
                val realm = Realm.getInstance(config)
                val result = realm?.where(RepoRealm::class.java)?.findAll();
                var list = arrayListOf<RepoRealm>()
                result?.forEach {
                    val repoRealm = RepoRealm(it.id,it.fullname, it.des, it.star, it.fork, it.lang);
                    list.add(repoRealm)
                }
                for (test in list ) {
                    Log.e("TAG", "id  ${test.id}  : name ${test.fullname}")
                }
                observer.onSuccess(list)
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun setListFavo(){
        val result = realm?.where(RepoRealm::class.java)?.findAll();
        var list = arrayListOf<RepoRealm>()
        result?.forEach {
            val repoRealm = RepoRealm(it.id,it.fullname, it.des, it.star, it.fork, it.lang);
            list.add(repoRealm)
            Log.e("TAG", "id ${it.id} : ${it.fullname}")
        }

        adapterRepoFavo?.setList(list)
    }

    private fun displayDataRealm(): SingleObserver<List<RepoRealm>> {
        return object : SingleObserver<List<RepoRealm>> {
            override fun onSuccess(t: List<RepoRealm>) {
                recyclerViewFavo.visibility = View.VISIBLE
                adapterRepoFavo?.setList(t)
                progressBar.visibility = View.GONE

            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onError(e: Throwable) {
                Log.e("TAG", "error $e")
            }

        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.txtCancel -> {
//                edSearch.setText("")
                adapterRepoSearch?.setList(arrayListOf())
                adapterRepoFavo?.setList(arrayListOf())
//                progressBar.visibility = View.VISIBLE;
                recyclerView.visibility = View.GONE
                txtMostPopular.visibility = View.GONE
                txtMostRecent.visibility = View.GONE
                txtLogout.visibility = View.VISIBLE
                recyclerViewFavo.visibility = View.VISIBLE
                setListFavo()
//                getDataFromRealm2().observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(displayDataRealm())
            }
            R.id.txtLogout -> {
                val config = RealmConfiguration.Builder()
                    .name("repo.db")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
                val realm = Realm.getInstance(config)
                realm.executeTransaction{
                    it.deleteAll()
                }
                prefer?.edit()?.putInt(Utils.KEY_LOGIN, 0)?.commit()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.txtMostRecent -> {
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                val getRepo: Single<JsonElement> =
                    Client.createAPI().getRepoUpdate(edSearch.text.toString().trim(), "updated", "desc")
                getRepo.map<List<Repo>> { jsonElement ->
                    convertJsonToList(jsonElement)
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getListRepo())
            }
            R.id.txtMostPopular ->{
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                val getRepo: Single<JsonElement> =
                    Client.createAPI().getRepoStar(edSearch.text.toString().trim(), "star", "desc")
                getRepo.map<List<Repo>> { jsonElement ->
                    convertJsonToList(jsonElement)
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getListRepo())
            }
        }
    }

    private fun setupRepoFavo() {
        adapterRepoFavo = AdapterRepoFavo(this);
        recyclerViewFavo.adapter = adapterRepoFavo
    }

    private fun setupRepoSearch() {
        adapterRepoSearch = AdapterRepoSearch(this)
        recyclerView.adapter = adapterRepoSearch
    }

    override fun onRepoSearchClicked(repo: Repo) {
        //todo save repo to realm
        realm?.executeTransaction {
            val realmRepo1 = it?.where(RepoRealm::class.java)?.findAll()
            if (realmRepo1?.size == 0){
                val repoRealm = it.createObject(RepoRealm::class.java, 1)
                repoRealm.fullname = repo.fullName
                repoRealm.des = repo.description
                repoRealm.star = repo.numberStart
                repoRealm.fork = repo.numberFork
                repoRealm.lang = repo.language
                Log.e("TAG", "insert ne")
            }else{
                Log.e("TAG", "insert 2 ne")
                val id = it?.where(RepoRealm::class.java)?.max("id")
                val nextId = id!!.toInt() + 1
                val repoRealm = it.createObject(RepoRealm::class.java, nextId)
                repoRealm.fullname = repo.fullName
                repoRealm.des = repo.description
                repoRealm.star = repo.numberStart
                repoRealm.fork = repo.numberFork
                repoRealm.lang = repo.language
            }


        }
    }


    override fun OnRepoClicked(repo: RepoRealm?) {
        val intent = Intent(this, DetailActivity::class.java)
        adapterRepoFavo?.setList(arrayListOf())
        intent.putExtra(Utils.KEY, repo?.id.toString())
        Log.e("TAG", "id " + repo?.id.toString())
        startActivity(intent)
        finish()
    }
}
