package com.example.repokoltin.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.repokoltin.R
import com.example.repokoltin.presenter.PresenterLogin
import com.example.repokoltin.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity(), LoginView {
    private lateinit var presenterLogin: PresenterLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenterLogin = PresenterLogin(this,this)
        txtLogin.setOnClickListener(View.OnClickListener {
            txtLogin.visibility = View.GONE
            progressLogin.visibility = View.VISIBLE
            presenterLogin.login(edUserName.text.toString().trim(),
                edPass.text.toString().trim())
        })

    }

    override fun onBackPressed() {
        txtLogin.visibility = View.VISIBLE
        progressLogin.visibility = View.GONE

    }

    override fun onStop() {
        super.onStop()
        presenterLogin.onStop()
    }

    override fun onLoginFail() {
        txtLogin.visibility = View.VISIBLE
        progressLogin.visibility = View.GONE
        Toast.makeText(this, "Network not good, please try after one hour", Toast.LENGTH_SHORT).show()
    }

    override fun onLoginSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onPasswordNotValid() {
        txtLogin.visibility = View.VISIBLE
        progressLogin.visibility = View.GONE
        Toast.makeText(this, "Password not valid", Toast.LENGTH_SHORT).show()
    }

    override fun autoLogin() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}
