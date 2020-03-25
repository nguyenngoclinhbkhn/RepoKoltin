package com.example.repokoltin.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.repokoltin.R
import com.example.repokoltin.Utils
import com.example.repokoltin.client.Client
import com.example.repokoltin.model.User
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.internal.Util

class LoginActivity : AppCompatActivity() {
    private var compositeDisposable: CompositeDisposable? = null
    private var preference : SharedPreferences ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()
        preference = getSharedPreferences(Utils.NAME, Context.MODE_PRIVATE)
        if (preference!!.getInt(Utils.KEY_LOGIN, 0) == 1){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        txtLogin.setOnClickListener(View.OnClickListener {
            checkUserNamePass(edUserName.getText().toString().trim { it <= ' ' },
                    edPass.text.toString().trim { it <= ' ' })
        })

    }

    private fun checkUserNamePass(userName: String, pass: String) {
        if (checkPass(pass)) {
            txtLogin.visibility = View.GONE
            progressLogin.visibility = View.VISIBLE
            val getUser: Single<JsonElement> = Client.createAPI().getUser(userName!!)
            getUser.map { it ->
                convertJson(it, pass)
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(checkUser())
        } else {
            Toast.makeText(this, "Password not valid", Toast.LENGTH_SHORT).show()
        }
    }
    private fun convertJson(json: JsonElement, pass: String) : User{
        val jsonObject: JsonArray = json.asJsonObject.get("items").asJsonArray
        return User(jsonObject.get(0).asJsonObject.get("login").asString, pass)
    }

    private fun checkUser(): SingleObserver<User> {
        return object : SingleObserver<User> {
            override fun onSuccess(t: User) {
                t.let {
                    preference?.edit()?.putInt(Utils.KEY_LOGIN, 1)?.commit()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@LoginActivity, "Success", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable?.add(d)
            }

            override fun onError(e: Throwable) {
                txtLogin.visibility = View.VISIBLE
                progressLogin.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Login error", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onBackPressed() {
        txtLogin.visibility = View.VISIBLE
        progressLogin.visibility = View.GONE

    }

    private fun checkPass(pass: String): Boolean {
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

    override fun onStop() {
        super.onStop()
        compositeDisposable?.dispose()
    }
}
