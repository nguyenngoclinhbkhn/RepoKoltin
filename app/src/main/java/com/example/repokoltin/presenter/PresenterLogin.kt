package com.example.repokoltin.presenter

import android.content.Context
import android.content.SharedPreferences
import com.example.repokoltin.Utils
import com.example.repokoltin.client.Client
import com.example.repokoltin.model.User
import com.example.repokoltin.view.login.LoginView
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PresenterLogin(val loginView: LoginView, val context: Context) {
    private val compositeDisposable = CompositeDisposable()
    private var sharedPreferences: SharedPreferences?= null
    init {
        start()
    }
    fun login(userName: String, pass: String){
        if (checkPassValid(pass)){
            getUserSingleObservable(userName, pass).subscribe(getUserSingleObserver())
        }else{
            loginView.onPasswordNotValid()
        }
    }

    fun getUserSingleObservable(userName: String, pass: String): Single<User> {
        return Client.createAPI().getUser(userName)
            .map { json ->
                User(json.asJsonObject.get("items").asJsonArray.get(0).asJsonObject.get("login").asString, pass) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUserSingleObserver() : SingleObserver<User>{
        return object : SingleObserver<User> {
            override fun onSuccess(t: User) {
                t.let {
                    loginView.onLoginSuccess()
                    sharedPreferences?.edit()?.putInt(Utils.KEY_LOGIN, 1)?.commit()
                }
            }
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }
            override fun onError(e: Throwable) {
                loginView.onLoginFail()
            }
        }
    }
    fun start(){
        sharedPreferences = context.getSharedPreferences(Utils.NAME, Context.MODE_PRIVATE)
        if (sharedPreferences!!.getInt(Utils.KEY_LOGIN, 0) == 1){
            loginView.autoLogin()
        }
    }
    fun onStop(){
        compositeDisposable.dispose()
    }
    fun checkPassValid(pass: String) : Boolean{
        if (pass.length == 6) {
            val arrayPass = pass.toCharArray()
            val char1 = arrayPass[0].toInt()
            for (i in 1 until arrayPass.size) {
                if (arrayPass[i].toInt() != char1) {
                    return true
                }
            }
            return false
        }
        return false
    }
}