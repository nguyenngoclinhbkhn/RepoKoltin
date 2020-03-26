package com.example.repokoltin.presenter

import com.example.repokoltin.model.RepoRealm
import com.example.repokoltin.view.detail.DetailView
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class PresenterDetail(val detailView: DetailView) {
    private val compositeDisposable =  CompositeDisposable();

    

    fun deleteRepo(id: Int){
        val config = RealmConfiguration.Builder()
            .name("repo.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();
        val realm = Realm.getInstance(config)
        realm.executeTransactionAsync {
            val realmResult: RealmResults<RepoRealm>? =
                it.where(RepoRealm::class.java).equalTo("id", id).findAll()
            realmResult?.deleteAllFromRealm()
            detailView.onDeleteRepoSuccess()
        }
    }

    fun showData(id: Int){
        getDataFromId(id).subscribe(displayData())
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
                        id = id,
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
                detailView.onShowDetail(t)
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)

            }

            override fun onError(e: Throwable) {
                detailView.showDetailFaild()
            }

        }
    }

    fun stop(){
        compositeDisposable.dispose()
    }


}