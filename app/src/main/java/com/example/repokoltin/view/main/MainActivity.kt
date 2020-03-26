package com.example.repokoltin.view.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repokoltin.R
import com.example.repokoltin.Utils
import com.example.repokoltin.adapter.AdapterRepoFavo
import com.example.repokoltin.adapter.AdapterRepoSearch
import com.example.repokoltin.client.Client
import com.example.repokoltin.model.Repo
import com.example.repokoltin.model.RepoRealm
import com.example.repokoltin.presenter.PresenterMain
import com.example.repokoltin.view.detail.DetailActivity
import com.example.repokoltin.view.login.LoginActivity
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
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener,
    AdapterRepoSearch.OnRepoSearchListener, AdapterRepoFavo.OnRepoListener, MainView {
    var adapterRepoSearch: AdapterRepoSearch? = null
    var realm: Realm? = null
    var prefer: SharedPreferences? = null
    var adapterRepoFavo: AdapterRepoFavo? = null
    lateinit var presenterMain : PresenterMain
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        prefer = getSharedPreferences(Utils.NAME, Context.MODE_PRIVATE)
        Realm.init(this)
        presenterMain = PresenterMain(this, this)
        val config = RealmConfiguration.Builder()
            .name("repo.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();
        realm = Realm.getInstance(config)

        setupRepoFavo()
        setupRepoSearch()

        setListFavo()

        txtCancel.setOnClickListener(this)
        txtLogout.setOnClickListener(this)
        txtMostRecent.setOnClickListener(this)
        txtMostPopular.setOnClickListener(this)


        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(edSearch.text)) {
                    txtLogout.visibility = View.GONE
                    txtMostPopular.visibility = View.VISIBLE
                    txtMostRecent.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    recyclerViewFavo.visibility = View.GONE
                    presenterMain.showRepoSearch(edSearch.text.toString().trim(),
                        "", "")
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }
    private fun setListFavo(){
        progressBar.visibility = View.VISIBLE;
        Handler().postDelayed(Runnable {
            progressBar.visibility = View.GONE
            presenterMain.showRepoLocal()
        }, 1000)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.txtCancel -> {
//                edSearch.setText("")
                adapterRepoSearch?.setList(arrayListOf())
                adapterRepoFavo?.setList(arrayListOf())
                recyclerView.visibility = View.GONE
                txtMostPopular.visibility = View.GONE
                txtMostRecent.visibility = View.GONE
                txtLogout.visibility = View.VISIBLE
                recyclerViewFavo.visibility = View.VISIBLE
                setListFavo()
            }
            R.id.txtLogout -> {
                presenterMain.logout()
            }
            R.id.txtMostRecent -> {
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                presenterMain.showRepoSearch(edSearch.text.toString().trim(),
                    "updated", "desc")
            }
            R.id.txtMostPopular ->{
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                presenterMain.showRepoSearch(edSearch.text.toString().trim(),
                    "star", "desc")
            }
        }
    }

    private fun setupRepoFavo() {
        recyclerViewFavo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapterRepoFavo = AdapterRepoFavo(this);
        recyclerViewFavo.adapter = adapterRepoFavo
    }

    private fun setupRepoSearch() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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

    override fun onStop() {
        super.onStop()
        presenterMain.stop()
    }

    override fun OnRepoClicked(repo: RepoRealm?) {
        val intent = Intent(this, DetailActivity::class.java)
        adapterRepoFavo?.setList(arrayListOf())
        intent.putExtra(Utils.KEY, repo?.id.toString())
        Log.e("TAG", "id " + repo?.id.toString())
        startActivity(intent)
        finish()
    }

    override fun onShowRepoSearchSuccess(listRepo: List<Repo>) {
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        adapterRepoSearch?.setList(listRepo)
    }

    override fun onShowRepoSearchError() {
        Toast.makeText(this, "Please try again after one hour", Toast.LENGTH_SHORT).show()
    }

    override fun onShowRepoLocalSuccess(listRepo: ArrayList<RepoRealm>) {
        adapterRepoFavo?.setList(listRepo)
    }

    override fun onShowRepoLocalError() {
        Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show()
    }

    override fun logoutSuccess() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
