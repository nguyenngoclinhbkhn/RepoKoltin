package com.example.repokoltin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.repokoltin.R
import com.example.repokoltin.Utils
import com.example.repokoltin.model.Repo
import com.example.repokoltin.model.RepoRealm
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("repo.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();
        val realm = Realm.getInstance(config)
        val id = intent.getStringExtra(Utils.KEY).toInt()
        getDataFromId(id).observeOn(AndroidSchedulers.mainThread()).subscribe(displayData())

        txtDelete.setOnClickListener {
            realm.executeTransaction {
                val realmResult: RealmResults<RepoRealm>? =
                    it.where(RepoRealm::class.java).equalTo("id", id).findAll()
                realmResult?.deleteAllFromRealm()
                startActivity(Intent(this@DetailActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun getDataFromId(id: Int): Single<RepoRealm> {
        return object : Single<RepoRealm>() {
            override fun subscribeActual(observer: SingleObserver<in RepoRealm>) {
                val config = RealmConfiguration.Builder()
                    .name("repo.db")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
                val realm = Realm.getInstance(config)
                val result = realm?.where(RepoRealm::class.java)?.equalTo("id", id)!!.findFirst();
                observer.onSuccess(
                    RepoRealm(
                        id = result.id,
                        fullname = result.fullname,
                        des = result.des,
                        star = result.star,
                        fork = result.fork,
                        lang = result.lang
                    )
                )
            }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun displayData(): SingleObserver<RepoRealm> {
        return object : SingleObserver<RepoRealm> {
            override fun onSuccess(t: RepoRealm) {
                txtFullName.setText(t.fullname)
                txtDes.text = t.des
                txtStar.text = t.star
                txtForks.text = t.fork
                txtLanguage.text = t.lang
                txtTitleToolbar.text = t.fullname
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)

            }

            override fun onError(e: Throwable) {
            }

        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@DetailActivity, MainActivity::class.java))
        finish()
    }
}
